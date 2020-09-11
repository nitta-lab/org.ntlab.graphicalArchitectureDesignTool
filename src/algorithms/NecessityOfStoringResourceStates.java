package algorithms;

import models.*;
import models.algebra.*;
import models.dataConstraintModel.*;
import models.dataFlowModel.*;

public class NecessityOfStoringResourceStates {
	static public ResourceDependencyGraph doDecide(DataFlowModel model) {
		ResourceDependencyGraph graph = model.getResourceDependencyGraph();
		DataConstraintModel dcmodel = (DataConstraintModel) model;
		for (ChannelGenerator generator : dcmodel.getChannelGenerators()) {
			boolean flag = false;
			for (ChannelMember member : ((DataflowChannelGenerator) generator).getOutputChannelMembers()) {
				Expression curexp = member.getStateTransition().getCurStateExpression();
				Expression nextexp = member.getStateTransition().getNextStateExpression();
				for (Position pos : curexp.getVariables().keySet()) {
					if (nextexp.contains(curexp.getVariables().get(pos))) {
						flag = true;
					}
				}
			}
			for (Node node : graph.getNodes()) {
				for (ChannelMember member : generator.getChannelMembers()) {
					if (((ResourceNode) node).getIdentifierTemplate().equals(member.getIdentifierTemplate())) {
						setStoreAttribute(flag, (ResourceNode) node);
					}
				}
			}
		}
		for (Node node : graph.getNodes()) {
			if (((ResourceNode) node).getPredecessors().size() > 1) {
				setStoreAttribute(true, (ResourceNode) node);
			} else if (((ResourceNode) node).getAttribute() == null) {
				setStoreAttribute(false, (ResourceNode) node);
			}
		}
		return graph;
	}

	static private void setStoreAttribute(boolean flag, ResourceNode node) {
		StoreAttribute store = new StoreAttribute();
		store.setNeeded(flag);
		node.setAttribute(store);
	}
}
