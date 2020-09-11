package graphicalrefactor.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import algorithms.NecessityOfStoringResourceStates;
import algorithms.SelectableDataTransfers;
import algorithms.UpdateConflictCheck;
import code.ast.CompilationUnit;
import models.Edge;
import models.Node;
import models.dataFlowModel.DataFlowModel;
import models.dataFlowModel.DataflowChannelGenerator;
import models.dataFlowModel.ResourceDependency;
import models.dataFlowModel.ResourceDependencyGraph;
import models.dataFlowModel.ResourceNode;
import parser.ExpectedChannel;
import parser.ExpectedChannelName;
import parser.ExpectedEquals;
import parser.ExpectedInOrOutKeyword;
import parser.ExpectedLeftCurlyBracket;
import parser.ExpectedRHSExpression;
import parser.ExpectedRightBracket;
import parser.ExpectedStateTransition;
import parser.Parser;
import parser.WrongLHSExpression;
import parser.WrongRHSExpression;

public class Editor {
	final int PORT_DIAMETER = 8;
	final int PORT_RADIUS = PORT_DIAMETER / 2;

	private mxGraph graph = null;
	private String curFileName = null;
	private DataFlowModel model = null;
	private ResourceDependencyGraph resourceDependencyGraph = null;
	private ArrayList<CompilationUnit> codes = null;

	public Editor(mxGraph graph) {
		this.graph = graph;
	}

	public mxGraph getGraph() {
		return graph;
	}

	public void setGraph(mxGraph graph) {
		this.graph = graph;
	}

	public DataFlowModel getModel() {
		return model;
	}

	public void setModel(DataFlowModel model) {
		this.model = model;
	}

	public ResourceDependencyGraph getResourceDependencyGraph() {
		return resourceDependencyGraph;
	}

	public void setResourceDependencyGraph(ResourceDependencyGraph resourceDependencyGraph) {
		this.resourceDependencyGraph = resourceDependencyGraph;
	}

	public ArrayList<CompilationUnit> getCodes() {
		return codes;
	}

	public void setCodes(ArrayList<CompilationUnit> codes) {
		this.codes = codes;
	}

	public String getCurFileName() {
		return curFileName;
	}

	public DataFlowModel open(File file) {
		try {
			Parser parser = new Parser(new BufferedReader(new FileReader(file)));
			try {
				model = parser.doParse();
				curFileName = file.getName();
				if(!UpdateConflictCheck.run(model)) return null;
				ResourceDependencyGraph resourceGraph = NecessityOfStoringResourceStates.doDecide(model);
				resourceDependencyGraph = SelectableDataTransfers.init(resourceGraph);
				graph = constructGraph(model, resourceDependencyGraph);
				return model;
			} catch (ExpectedChannel | ExpectedChannelName | ExpectedLeftCurlyBracket | ExpectedInOrOutKeyword
					| ExpectedStateTransition | ExpectedEquals | ExpectedRHSExpression | WrongLHSExpression
					| WrongRHSExpression | ExpectedRightBracket e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public mxGraph constructGraph(DataFlowModel model, ResourceDependencyGraph resourceDependencyGraph) {
		((mxGraphModel) graph.getModel()).clear();
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			mxGeometry geo1 = new mxGeometry(0, 0.5, PORT_DIAMETER, PORT_DIAMETER);
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);

			mxGeometry geo2 = new mxGeometry(1.0, 0.5, PORT_DIAMETER, PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo2.setRelative(true);

			HashMap<DataflowChannelGenerator, Object> channelsIn = new HashMap<>();
			HashMap<DataflowChannelGenerator, Object> channelsOut = new HashMap<>();
			HashMap<ResourceNode, Object> resources = new HashMap<>();

			for (Edge e : resourceDependencyGraph.getEdges()) {
				if (e instanceof ResourceDependency) {
					ResourceDependency dependency = (ResourceDependency) e;
					DataflowChannelGenerator channelGen = dependency.getChannelGenerator();
					if (channelsIn.get(channelGen) == null || channelsOut.get(channelGen) == null) {
						Object channel = graph.insertVertex(parent, null, channelGen.getChannelName(), 150, 20, 30, 30); // insert
																															// a
																															// channel
																															// as
																															// a
																															// vertex
						mxCell port_in = new mxCell(null, geo1, "shape=ellipse;perimter=ellipsePerimeter");
						port_in.setVertex(true);
						graph.addCell(port_in, channel);
						mxCell port_out = new mxCell(null, geo2, "shape=ellipse;perimter=ellipsePerimeter");
						port_out.setVertex(true);
						graph.addCell(port_out, channel);
						channelsIn.put(channelGen, port_in);
						channelsOut.put(channelGen, port_out);
					}
				}
			}

			for (Node n : resourceDependencyGraph.getNodes()) {
				if (n instanceof ResourceNode) {
					ResourceNode resourceNode = (ResourceNode) n;
					Object resource = graph.insertVertex(parent, null,
							resourceNode.getIdentifierTemplate().getResourceName(), 20, 20, 80, 30,
							"shape=ellipse;perimeter=ellipsePerimeter"); // insert a resource as a node
					resources.put(resourceNode, resource);
				}
			}

			for (Edge e : resourceDependencyGraph.getEdges()) {
				if (e instanceof ResourceDependency) {
					ResourceDependency dependency = (ResourceDependency) e;
					DataflowChannelGenerator channelGen = dependency.getChannelGenerator();
					graph.insertEdge(parent, null, dependency.getAttribute(), resources.get(dependency.getSource()),
							channelsIn.get(channelGen));
					graph.insertEdge(parent, null, null, channelsOut.get(channelGen),
							resources.get(dependency.getDestination()));
				}
			}

		} finally {
			graph.getModel().endUpdate();
		}
		setTreeLayout();

		return graph;
	}

	public void setTreeLayout() {
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			mxCompactTreeLayout ctl = new mxCompactTreeLayout(graph);
			ctl.setLevelDistance(100);
//		ctl.setHorizontal(false);
			ctl.setEdgeRouting(false);
			ctl.execute(parent);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void setCircleLayout() {
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			mxCircleLayout ctl = new mxCircleLayout(graph);
			ctl.execute(parent);
		} finally {
			graph.getModel().endUpdate();
		}
	}
}
