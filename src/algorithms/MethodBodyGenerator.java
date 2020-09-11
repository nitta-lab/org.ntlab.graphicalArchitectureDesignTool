package algorithms;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import code.ast.CompilationUnit;
import code.ast.MethodDeclaration;
import code.ast.TypeDeclaration;
import models.Edge;
import models.Node;
import models.algebra.Expression;
import models.algebra.InvalidMessage;
import models.algebra.ParameterizedIdentifierIsFutureWork;
import models.algebra.Symbol;
import models.algebra.Term;
import models.algebra.UnificationFailed;
import models.algebra.ValueUndefined;
import models.dataConstraintModel.ChannelGenerator;
import models.dataConstraintModel.ChannelMember;
import models.dataFlowModel.DataFlowModel;
import models.dataFlowModel.DataflowChannelGenerator;
import models.dataFlowModel.PushPullAttribute;
import models.dataFlowModel.PushPullValue;
import models.dataFlowModel.ResolvingMultipleDefinitionIsFutureWork;
import models.dataFlowModel.ResourceDependency;
import models.dataFlowModel.ResourceDependencyGraph;
import models.dataFlowModel.ResourceNode;
import models.dataFlowModel.StoreAttribute;

public class MethodBodyGenerator {
	public static ArrayList<CompilationUnit> doGenerate(ResourceDependencyGraph graph, DataFlowModel model, ArrayList<CompilationUnit> codes) {
		Symbol floor = model.getSymbol("floor");
		Symbol.Memento floorMem = null;
		if (floor != null) {
			floorMem = floor.createMemento();
			floor.setImplName("(int)Math.floor");
			floor.setImplOperatorType(Symbol.Type.PREFIX);
		}
		Symbol sum = model.getSymbol("sum");
		Symbol.Memento sumMem = null;
		if (sum != null) {
			sumMem = sum.createMemento();
			sum.setImplName("stream().mapToInt(x->x).sum");
			sum.setImplOperatorType(Symbol.Type.METHOD);
		}

		// Create a map from type names (lower case) to their types.
		Map<String, TypeDeclaration> typeMap = new HashMap<>();
		for (CompilationUnit code: codes) {
			for (TypeDeclaration type: code.types()) {
				typeMap.put(type.getTypeName().substring(0,1).toLowerCase() + type.getTypeName().substring(1), type);
			}
		}
		
		// Generate the body of each update or getter method.
		try {
			for (Edge e: graph.getEdges()) {
				ResourceDependency d = (ResourceDependency) e;
				PushPullAttribute pushPull = (PushPullAttribute) d.getAttribute();
				ResourceNode src = (ResourceNode) d.getSource();
				ResourceNode dst = (ResourceNode) d.getDestination();
				String srcResourceName = src.getIdentifierTemplate().getResourceName();
				String dstResourceName = dst.getIdentifierTemplate().getResourceName();
				TypeDeclaration srcType = typeMap.get(srcResourceName);
				TypeDeclaration dstType = typeMap.get(dstResourceName);
				for (ChannelMember out: d.getChannelGenerator().getOutputChannelMembers()) {
					if (out.getIdentifierTemplate() == dst.getIdentifierTemplate()) {
						if (pushPull.getOptions().get(0) == PushPullValue.PUSH && srcType != null) {
							// for push data transfer
							MethodDeclaration update = getUpdateMethod(dstType, srcType);
							if (((StoreAttribute) dst.getAttribute()).isStored()) {
								// update stored state of dst resource (when every incoming edge is in push style)
								Expression updateExp = d.getChannelGenerator().deriveUpdateExpressionOf(out, CodeGenerator.pushAccessor);
								String curState = updateExp.toImplementation();
								String updateStatement;
								if (updateExp instanceof Term && ((Term) updateExp).getSymbol().getImplOperatorType() == Symbol.Type.METHOD_WITH_SIDE_EFFECT) {
									updateStatement = curState + ";";								
								} else {
									updateStatement = dstResourceName + " = " + curState + ";";
								}
								if (update.getBody() == null || !update.getBody().getStatements().contains(updateStatement)) {
									update.addFirstStatement(updateStatement);
								}
							}
							if (dst.getIndegree() > 1) {
								// update a cash of src resource (when incoming edges are multiple)
								String cashStatement = "this." + srcResourceName + " = " + srcResourceName + ";";
								if (update.getBody() == null || !update.getBody().getStatements().contains(cashStatement)) {
									update.addFirstStatement(cashStatement);
								}								
							}
							MethodDeclaration getter = getGetterMethod(dstType);
							if (getter.getBody() == null || getter.getBody().getStatements().size() == 0) {
								getter.addStatement("return " + dstResourceName + ";");
							}
							// src side (for a chain of update method invocations)
							for (MethodDeclaration srcUpdate: getUpdateMethods(srcType)) {
								srcUpdate.addStatement(dstResourceName + ".update" + srcType.getTypeName() + "(" + srcResourceName + ");");
							}
							MethodDeclaration srcInput = getInputMethod(srcType, src, model);
							if (srcInput != null) srcInput.addStatement(dstResourceName + ".update" + srcType.getTypeName() + "(" + srcResourceName + ");");
						} else {
							// for pull (or push/pull) data transfer
							MethodDeclaration getter = getGetterMethod(dstType);
							if (getter.getBody() == null || getter.getBody().getStatements().size() == 0) {
								String curState = d.getChannelGenerator().deriveUpdateExpressionOf(out, CodeGenerator.pullAccessor).toImplementation();
								getter.addStatement("return " + curState + ";");
							}
						} 
					}
				}
			}
			// for source nodes
			String mainTypeName = CodeGenerator.mainTypeName.substring(0,1).toLowerCase() + CodeGenerator.mainTypeName.substring(1);
			TypeDeclaration mainType = typeMap.get(mainTypeName);
			for (Node n: graph.getNodes()) {
				ResourceNode resource = (ResourceNode) n;
				String resourceName = resource.getIdentifierTemplate().getResourceName();
				TypeDeclaration type = typeMap.get(resourceName);
				if (type != null) {
					// getter method
					MethodDeclaration getter = getGetterMethod(type);
					if (getter.getBody() == null || getter.getBody().getStatements().size() == 0) {
						getter.addStatement("return " + resource.getIdentifierTemplate().getResourceName() + ";");
					}
					// methods for input events
					Map.Entry<DataflowChannelGenerator, ChannelMember> ioChannelAndMember = getIOChannelAndMember(resource, model);
					if (ioChannelAndMember != null) {
						ChannelMember out = ioChannelAndMember.getValue();
						Term message = (Term) out.getStateTransition().getMessageExpression();
						MethodDeclaration input = getMethod(type, message.getSymbol().getName());
						if (input != null) {
							Expression updateExp = ioChannelAndMember.getKey().deriveUpdateExpressionOf(out, CodeGenerator.pushAccessor);
							String newState = updateExp.toImplementation();
							String updateStatement;
							if (updateExp instanceof Term && ((Term) updateExp).getSymbol().getImplOperatorType() == Symbol.Type.METHOD_WITH_SIDE_EFFECT) {
								updateStatement = newState + ";";								
							} else {
								updateStatement = "this." + resourceName + " = " + newState + ";";
							}
							if (input.getBody() == null || !input.getBody().getStatements().contains(updateStatement)) {
								input.addFirstStatement(updateStatement);
							}
							if (mainType != null) {
								MethodDeclaration mainInput = getMethod(mainType, input.getName());
								if (mainInput != null) {
									mainInput.addStatement("this." + resourceName + "." + input.getName() + "(" + resourceName + ");");
								}
							}
						}
					}
				}
			}
		} catch (ParameterizedIdentifierIsFutureWork | ResolvingMultipleDefinitionIsFutureWork
				| InvalidMessage | UnificationFailed | ValueUndefined e1) {
			e1.printStackTrace();
		}
		
		if (floor != null) floor.setMemento(floorMem);
		if (sum != null) sum.setMemento(sumMem);
		return codes;
	}

	private static MethodDeclaration getUpdateMethod(TypeDeclaration type, TypeDeclaration from) {
		for (MethodDeclaration m: type.getMethods()) {
			if (m.getName().equals("update" + from.getTypeName())) return m;
		}
		return null;
	}

	private static ArrayList<MethodDeclaration> getUpdateMethods(TypeDeclaration type) {
		ArrayList<MethodDeclaration> updates = new ArrayList<>();
		for (MethodDeclaration m: type.getMethods()) {
			if (m.getName().startsWith("update")) {
				updates.add(m);
			}
		}
		return updates;
	}

	private static MethodDeclaration getGetterMethod(TypeDeclaration type) {
		for (MethodDeclaration m: type.getMethods()) {
			if (m.getName().startsWith("get")) return m;
		}
		return null;
	}
	
	private static Map.Entry<DataflowChannelGenerator, ChannelMember> getIOChannelAndMember(ResourceNode resource, DataFlowModel model) {
		for (ChannelGenerator c: model.getIOChannelGenerators()) {
			DataflowChannelGenerator channel = (DataflowChannelGenerator) c;
			// I/O channel
			for (ChannelMember out: channel.getOutputChannelMembers()) {
				if (out.getIdentifierTemplate().equals(resource.getIdentifierTemplate())) {
					if (out.getStateTransition().getMessageExpression() instanceof Term) {
						// not an identity element
						return new AbstractMap.SimpleEntry<>(channel, out);
					}
				}
			}
		}
		return null;
	}

	private static MethodDeclaration getInputMethod(TypeDeclaration type, ResourceNode resource, DataFlowModel model) {
		for (ChannelGenerator c: model.getIOChannelGenerators()) {
			DataflowChannelGenerator channel = (DataflowChannelGenerator) c;
			// I/O channel
			for (ChannelMember out: channel.getOutputChannelMembers()) {
				if (out.getIdentifierTemplate().equals(resource.getIdentifierTemplate())) {
					if (out.getStateTransition().getMessageExpression() instanceof Term) {
						// not an identity element
						Term message = (Term) out.getStateTransition().getMessageExpression();
						MethodDeclaration input = getMethod(type, message.getSymbol().getName());
						return input;
					}
				}
			}
		}
		return null;
	}

	private static MethodDeclaration getMethod(TypeDeclaration type, String methodName) {
		for (MethodDeclaration m: type.getMethods()) {
			if (m.getName().equals(methodName)) return m;
		}
		return null;
	}
}
