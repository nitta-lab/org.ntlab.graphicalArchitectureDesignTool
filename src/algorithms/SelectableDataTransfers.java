package algorithms;

import models.*;
import models.dataFlowModel.*;

public class SelectableDataTransfers {
	static public ResourceDependencyGraph init(ResourceDependencyGraph graph) {
		PushPullAttribute ppat = new PushPullAttribute();
		ppat.addOption(PushPullValue.PUSHorPULL);
		ppat.addOption(PushPullValue.PUSH);
		ppat.addOption(PushPullValue.PULL);
		for (Node n : graph.getNodes()) {
			if (((StoreAttribute) ((ResourceNode) n).getAttribute()).isNeeded()) {
				trackEdges(n);
			}
		}
		for (Edge e : graph.getEdges()) {
			if (((ResourceDependency) e).getAttribute() == null) {
				((ResourceDependency) e).setAttribute(ppat);
			}
		}
		return graph;
	}

	static private void trackEdges(Node n) {
		PushPullAttribute ppat = new PushPullAttribute();
		ppat.addOption(PushPullValue.PUSH);
		for (Edge e : ((ResourceNode) n).getInEdges()) {
			((ResourceDependency) e).setAttribute(ppat);
		}
	}
}
