package models.dataFlowModel;

import java.util.HashMap;
import java.util.Map;

import models.DirectedGraph;
import models.dataConstraintModel.IdentifierTemplate;

public class ResourceDependencyGraph extends DirectedGraph {
	protected Map<IdentifierTemplate, ResourceNode> nodeMap = null;
	
	public ResourceDependencyGraph() {
		super();
		nodeMap = new HashMap<>();
	}
	
	public void addNode(IdentifierTemplate id) {
		if (nodeMap.get(id) == null) {
			ResourceNode node = new ResourceNode(id);
			addNode(node);
			nodeMap.put(id, node);			
		}
	}

	public void addEdge(IdentifierTemplate in, IdentifierTemplate out, DataflowChannelGenerator dfChannelGen) {
		ResourceNode srcNode = nodeMap.get(in);
		if (srcNode == null) {
			srcNode = new ResourceNode(in);
			addNode(srcNode);
			nodeMap.put(in, srcNode);
		}
		ResourceNode dstNode = nodeMap.get(out);
		if (dstNode == null) {
			dstNode = new ResourceNode(out);
			addNode(dstNode);
			nodeMap.put(out, dstNode);
		}
		addEdge(new ResourceDependency(srcNode, dstNode, dfChannelGen));
	}
}
