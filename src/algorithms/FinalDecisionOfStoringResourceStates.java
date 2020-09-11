package algorithms;

import java.util.HashSet;

import models.*;
import models.algebra.*;
import models.dataConstraintModel.ChannelMember;
import models.dataConstraintModel.DataConstraintModel;
import models.dataFlowModel.*;

public class FinalDecisionOfStoringResourceStates {
	static private HashSet<Node> arrivedNode = new HashSet<>();

	static public void doDecide(ResourceDependencyGraph graph) {
		arrivedNode.clear();
		for (Node n : graph.getNodes()) {
			ResourceNode resource = (ResourceNode) n;
			trackNode(resource);
		}
	}

	static private void trackNode(ResourceNode resource) {
		if (arrivedNode.contains(resource))
			return;
		arrivedNode.add(resource);
		boolean flag = false;
		for (Edge e : resource.getInEdges()) {
			if (((PushPullAttribute) e.getAttribute()).getOptions().get(0) == PushPullValue.PUSH) {
				trackNode((ResourceNode) e.getSource());
				flag = true;
			}
		}
		if (resource.getInEdges().size() == 0)
			flag = true;
		((StoreAttribute) resource.getAttribute()).setStored(flag);
		if (resource.getIdentifierTemplate().getResourceStateType() == null) {
			for (Edge e : resource.getInEdges()) {
				for (ChannelMember cm : ((ResourceDependency) e).getChannelGenerator().getChannelMembers()) {
					if (((PushPullAttribute) ((ResourceDependency) e).getAttribute()).getOptions()
							.get(0) == PushPullValue.PUSH
							&& cm.getStateTransition().getNextStateExpression().getClass() == Term.class) {
						if (((Term) cm.getStateTransition().getNextStateExpression()).getSymbol().getName()
								.equals("cons")) {
							resource.getIdentifierTemplate().setResourceStateType(DataConstraintModel.typeList);
						}
					}
					break;
				}
			}
			for (Edge e : resource.getOutEdges()) {
				for (ChannelMember cm : ((ResourceDependency) e).getChannelGenerator().getChannelMembers()) {
					if (((PushPullAttribute) ((ResourceDependency) e).getAttribute()).getOptions()
							.get(0) != PushPullValue.PUSH
							&& cm.getStateTransition().getNextStateExpression().getClass() == Term.class) {
						if (((Term) cm.getStateTransition().getNextStateExpression()).getSymbol().getName()
								.equals("cons")) {
							resource.getIdentifierTemplate().setResourceStateType(DataConstraintModel.typeList);
						}
					}
					break;
				}
			}
			if (resource.getIdentifierTemplate().getResourceStateType() == null)
				resource.getIdentifierTemplate().setResourceStateType(DataConstraintModel.typeInt);
		}
	}
}
