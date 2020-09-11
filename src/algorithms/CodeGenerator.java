package algorithms;

import java.util.ArrayList;
import java.util.HashSet;

import code.ast.Block;
import code.ast.CompilationUnit;
import code.ast.FieldDeclaration;
import code.ast.ImportDeclaration;
import code.ast.MethodDeclaration;
import code.ast.TypeDeclaration;
import code.ast.VariableDeclaration;
import models.Edge;
import models.Node;
import models.algebra.Expression;
import models.algebra.Field;
import models.algebra.Parameter;
import models.algebra.Symbol;
import models.algebra.Term;
import models.algebra.Type;
import models.dataConstraintModel.ChannelGenerator;
import models.dataConstraintModel.ChannelMember;
import models.dataConstraintModel.DataConstraintModel;
import models.dataConstraintModel.IdentifierTemplate;
import models.dataFlowModel.DataFlowModel;
import models.dataFlowModel.DataflowChannelGenerator.IResourceStateAccessor;
import models.dataFlowModel.PushPullAttribute;
import models.dataFlowModel.PushPullValue;
import models.dataFlowModel.ResourceDependency;
import models.dataFlowModel.ResourceDependencyGraph;
import models.dataFlowModel.ResourceNode;
import models.dataFlowModel.StoreAttribute;

public class CodeGenerator {
	public static final Type typeVoid = new Type("Void", "void");
	private static String defaultMainTypeName = "Main";
	static String mainTypeName = defaultMainTypeName;

	public static String getMainTypeName() {
		return mainTypeName;
	}

	public static void setMainTypeName(String mainTypeName) {
		CodeGenerator.mainTypeName = mainTypeName;
	}

	public static void resetMainTypeName() {
		CodeGenerator.mainTypeName = defaultMainTypeName;
	}

	static public ArrayList<CompilationUnit> doGenerate(ResourceDependencyGraph graph, DataFlowModel model) {
		ArrayList<CompilationUnit> codes = new ArrayList<>();
		ArrayList<ResourceNode> resources = StoreResourceCheck(graph);

		TypeDeclaration mainType = new TypeDeclaration(mainTypeName);
		CompilationUnit mainCU = new CompilationUnit(mainType);
		mainCU.addImport(new ImportDeclaration("java.util.*"));
		codes.add(mainCU);
		for (ResourceNode rn : resources) {
			boolean f = false;
			String name = rn.getIdentifierTemplate().getResourceName().substring(0, 1).toUpperCase()
					+ rn.getIdentifierTemplate().getResourceName().substring(1);
			String consstr = "new " + name + "(";
			TypeDeclaration type = new TypeDeclaration(name);
			for (Edge e : rn.getOutEdges()) {
				ResourceDependency re = (ResourceDependency) e;
				String rename = ((ResourceNode) re.getDestination()).getIdentifierTemplate().getResourceName()
						.substring(0, 1).toUpperCase()
						+ ((ResourceNode) re.getDestination()).getIdentifierTemplate().getResourceName().substring(1);
				if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) == PushPullValue.PUSH) {
					consstr += rename.toLowerCase() + ",";
					f = true;
				}
			}
			for (Edge e : rn.getInEdges()) {
				ResourceDependency re = (ResourceDependency) e;
				String rename = ((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName()
						.substring(0, 1).toUpperCase()
						+ ((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName().substring(1);
				if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) != PushPullValue.PUSH) {
					consstr += rename.toLowerCase() + ",";
					f = true;
				} else {
					if (rn.getIndegree() > 1)
						type.addField(new FieldDeclaration(
								((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceStateType(),
								((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName()));
				}
			}
			if (f)
				consstr = consstr.substring(0, consstr.length() - 1);
			consstr += ")";
			FieldDeclaration field = new FieldDeclaration(new Type(name, name),
					rn.getIdentifierTemplate().getResourceName(), consstr);
			mainType.addField(field);
			MethodDeclaration cons = new MethodDeclaration(name, true);
			Block block = new Block();
			for (Edge e : rn.getOutEdges()) {
				ResourceDependency re = (ResourceDependency) e;
				String rename = ((ResourceNode) re.getDestination()).getIdentifierTemplate().getResourceName()
						.substring(0, 1).toUpperCase()
						+ ((ResourceNode) re.getDestination()).getIdentifierTemplate().getResourceName().substring(1);
				if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) == PushPullValue.PUSH) {
					type.addField(new FieldDeclaration(new Type(rename, rename),
							((ResourceNode) re.getDestination()).getIdentifierTemplate().getResourceName()));
					cons.addParameter(new VariableDeclaration(new Type(rename, rename),
							((ResourceNode) re.getDestination()).getIdentifierTemplate().getResourceName()));
					block.addStatement("this." + rename.toLowerCase() + " = " + rename.toLowerCase() + ";");
					cons.setBody(block);
				}
			}
			block = new Block();
			for (Edge e : rn.getInEdges()) {
				ResourceDependency re = (ResourceDependency) e;
				String rename = ((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName()
						.substring(0, 1).toUpperCase()
						+ ((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName().substring(1);
				if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) != PushPullValue.PUSH) {
					type.addField(new FieldDeclaration(new Type(rename, rename),
							((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName()));
					cons.addParameter(new VariableDeclaration(new Type(rename, rename),
							((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName()));
					block.addStatement("this." + rename.toLowerCase() + " = " + rename.toLowerCase() + ";");
					cons.setBody(block);
				} else {
					ArrayList<VariableDeclaration> vars = new ArrayList<>();
					vars.add(new VariableDeclaration(
							((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceStateType(),
							((ResourceNode) re.getSource()).getIdentifierTemplate().getResourceName()));
					type.addMethod(new MethodDeclaration("update" + rename, false, typeVoid, vars));
				}
			}
			if (cons.getParameters() != null)
				type.addMethod(cons);
			for (ChannelGenerator cg : model.getIOChannelGenerators()) {
				for (ChannelMember cm : cg.getChannelMembers()) {
					if (cm.getIdentifierTemplate().getResourceName().equals(type.getTypeName().toLowerCase())) {
						if (cm.getStateTransition().getMessageExpression().getClass() == Term.class) {
							ArrayList<VariableDeclaration> params = new ArrayList<>();
							params.add(new VariableDeclaration(cm.getIdentifierTemplate().getResourceStateType(),
									cm.getIdentifierTemplate().getResourceName()));
							MethodDeclaration io = new MethodDeclaration(
									((Term) cm.getStateTransition().getMessageExpression()).getSymbol().getImplName(),
									false, typeVoid, params);
							type.addMethod(io);
							String str;
							str = ((Term) cm.getStateTransition().getMessageExpression()).getSymbol().getImplName();
							io = new MethodDeclaration(str, false, typeVoid, params);
							mainType.addMethod(io);
						}
					}
				}
			}
			if (((StoreAttribute) rn.getAttribute()).isStored()) {
				String str = "new " + rn.getIdentifierTemplate().getResourceStateType().getImplementationTypeName()
						+ "()";
				if (!rn.getIdentifierTemplate().getResourceStateType().getTypeName().contains("List"))
					str = null;
				type.addField(new FieldDeclaration(rn.getIdentifierTemplate().getResourceStateType(),
						rn.getIdentifierTemplate().getResourceName(), str));
			}
			type.addMethod(new MethodDeclaration("get" + type.getTypeName(),
					rn.getIdentifierTemplate().getResourceStateType()));
			CompilationUnit cu = new CompilationUnit(type);
			cu.addImport(new ImportDeclaration("java.util.*"));
			codes.add(cu);
		}
		for (Node n : graph.getNodes()) {
			ResourceNode rn = (ResourceNode) n;
			MethodDeclaration get = new MethodDeclaration(
					"get" + rn.getIdentifierTemplate().getResourceName().substring(0, 1).toUpperCase()
							+ rn.getIdentifierTemplate().getResourceName().substring(1),
					rn.getIdentifierTemplate().getResourceStateType());
			get.setBody(new Block());
			get.getBody().addStatement(
					"return " + rn.getIdentifierTemplate().getResourceName() + "." + get.getName() + "();");
			mainType.addMethod(get);
		}
		HashSet<String> tmps = new HashSet<>();
		HashSet<String> cont = new HashSet<>();
		for (MethodDeclaration method : mainType.getMethods()) {
			if (!tmps.contains(method.getName()))
				tmps.add(method.getName());
			else
				cont.add(method.getName());
		}
		for (MethodDeclaration method : mainType.getMethods()) {
			if (cont.contains(method.getName())) {
				method.setName(method.getName() + method.getParameters().get(0).getName().substring(0, 1).toUpperCase()
						+ method.getParameters().get(0).getName().substring(1));
			}
		}
		return codes;
	}

	static public ArrayList<String> getCodes(ArrayList<TypeDeclaration> codeTree) {
		ArrayList<String> codes = new ArrayList<>();
		for (TypeDeclaration type : codeTree) {
			codes.add("public class " + type.getTypeName() + "{");
			for (FieldDeclaration field : type.getFields()) {
				if (type.getTypeName() != mainTypeName) {
					String cons = "\t" + "private " + field.getType().getImplementationTypeName() + " "
							+ field.getName();
					if (field.getType().equals(DataConstraintModel.typeList))
						cons += " = new ArrayList<>()";
					cons += ";";
					codes.add(cons);
				} else {
					String cons = "\t" + "private " + field.getType().getImplementationTypeName() + " "
							+ field.getName() + " = new " + field.getType().getTypeName() + "(";
					for (TypeDeclaration tree : codeTree) {
						if (field.getType().getTypeName() == tree.getTypeName()) {
							for (VariableDeclaration var : tree.getConstructors()) {
								cons += var.getName() + ",";
							}
							if (!tree.getConstructors().isEmpty())
								cons = cons.substring(0, cons.length() - 1);
							break;
						}
					}
					cons += ");";
					codes.add(cons);
				}
			}
			codes.add("");
			if (type.getTypeName() != mainTypeName) {
				if (!type.getConstructors().isEmpty()) {
					String cons = "\t" + "public " + type.getTypeName() + "(";
					for (VariableDeclaration constructor : type.getConstructors()) {
						cons += constructor.getType().getTypeName() + " " + constructor.getName() + ",";
					}
					if (!type.getConstructors().isEmpty())
						cons = cons.substring(0, cons.length() - 1);
					cons += "){";
					codes.add(cons);
					for (FieldDeclaration field : type.getFields()) {
						for (VariableDeclaration vari : type.getConstructors()) {
							if (field.getType().getTypeName().equals(vari.getType().getTypeName())) {
								codes.add("\t\t" + "this." + field.getName() + " = " + field.getName() + ";");
							}
						}
					}
					codes.add("\t" + "}");
					codes.add("");
				}
			}
			for (MethodDeclaration method : type.getMethods()) {
				String varstr = "\t" + "public " + method.getReturnType().getImplementationTypeName() + " "
						+ method.getName() + "(";
				if (method.getParameters() != null) {
					for (VariableDeclaration var : method.getParameters()) {
						varstr += var.getType().getImplementationTypeName() + " " + var.getName() + ",";
					}
					if (!method.getParameters().isEmpty())
						varstr = varstr.substring(0, varstr.length() - 1);
				}
				if (method.getBody() != null) {
					for (String str : method.getBody().getStatements()) {
						codes.add("\t\t" + str + ";");
					}
				}
				codes.add(varstr + ")" + "{");
				codes.add("\t" + "}");
				codes.add("");
			}
			codes.add("}");
			codes.add("");
		}
		return codes;
	}

	static private ArrayList<ResourceNode> StoreResourceCheck(ResourceDependencyGraph graph) {
		ArrayList<ResourceNode> resources = new ArrayList<>();
		for (Node n : graph.getNodes()) {
			ResourceNode rn = (ResourceNode) n;
			boolean flag = true;
			for (Edge e : rn.getOutEdges()) {
				ResourceDependency re = (ResourceDependency) e;
				if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) == PushPullValue.PUSH) {
					flag = false;
				}
			}
			for (Edge e : rn.getInEdges()) {
				ResourceDependency re = (ResourceDependency) e;
				if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) != PushPullValue.PUSH) {
					flag = false;
				}
			}
			if (flag)
				resources.add(rn);
		}
		trackNode(resources.get(0), resources);
		return resources;
	}

	static private void trackNode(ResourceNode current, ArrayList<ResourceNode> resources) {
		if (!resources.contains(current))
			resources.add(current);
		for (Edge e : current.getOutEdges()) {
			ResourceDependency re = (ResourceDependency) e;
			if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) != PushPullValue.PUSH) {
				trackNode((ResourceNode) re.getDestination(), resources);
			}
		}
		for (Edge e : current.getInEdges()) {
			ResourceDependency re = (ResourceDependency) e;
			if (((PushPullAttribute) re.getAttribute()).getOptions().get(0) == PushPullValue.PUSH) {
				trackNode((ResourceNode) re.getSource(), resources);
			}
		}
	}

	static public IResourceStateAccessor pushAccessor = new IResourceStateAccessor() {
		@Override
		public Expression getCurrentStateAccessorFor(IdentifierTemplate target, IdentifierTemplate from) {
			if (target.equals(from)) {
				return new Field(target.getResourceName(),
						target.getResourceStateType() != null ? target.getResourceStateType()
								: DataConstraintModel.typeInt);
			}
			return null;
		}

		@Override
		public Expression getNextStateAccessorFor(IdentifierTemplate target, IdentifierTemplate from) {
			return new Parameter(target.getResourceName(),
					target.getResourceStateType() != null ? target.getResourceStateType()
							: DataConstraintModel.typeInt);
		}
	};
	static public IResourceStateAccessor pullAccessor = new IResourceStateAccessor() {
		@Override
		public Expression getCurrentStateAccessorFor(IdentifierTemplate target, IdentifierTemplate from) {
			if (target.equals(from)) {
				return new Field(target.getResourceName(),
						target.getResourceStateType() != null ? target.getResourceStateType()
								: DataConstraintModel.typeInt);
			}
			return null;
		}

		@Override
		public Expression getNextStateAccessorFor(IdentifierTemplate target, IdentifierTemplate from) {
			Term getter = new Term(new Symbol("get" + target.getResourceName().substring(0, 1).toUpperCase()
					+ target.getResourceName().substring(1), 1, Symbol.Type.METHOD));
			getter.addChild(new Field(target.getResourceName(), target.getResourceStateType()));
			return getter;
		}
	};
}
