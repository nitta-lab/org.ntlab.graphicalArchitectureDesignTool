package algorithms;

import java.util.*;

import models.*;
import models.dataFlowModel.*;

public class UpdateConflictCheck {
	private static int index = 0;
	private static Deque<Node> stack = new ArrayDeque<>();
	private static Set<Set<Node>> strong = new HashSet<>();
	private static Map<Node, Integer> ids = new HashMap<>();
	private static Map<Node, Integer> lowlink = new HashMap<>();
	private static Map<Node, Boolean> onStack = new HashMap<>();

	static private void init() {
		index = 0;
		stack = new ArrayDeque<>();
		strong = new HashSet<>();
		ids = new HashMap<>();
		lowlink = new HashMap<>();
		onStack = new HashMap<>();
	}

	static private void strongconnect(Node node) {
		ids.put(node, index);
		lowlink.put(node, index);
		index++;
		stack.push(node);
		onStack.put(node, true);

		for (Node n : node.getSuccessors()) {
			if (lowlink.containsKey(n)) {
				strongconnect(n);
				if (lowlink.get(node) > lowlink.get(n)) {
					lowlink.replace(node, lowlink.get(n));
				}
			} else if (onStack.get(n)) {
				if (lowlink.get(node) > lowlink.get(n)) {
					lowlink.replace(node, lowlink.get(n));
				}
			}
		}
		if (lowlink.get(node) == ids.get(node)) {
			Set<Node> tmp = new HashSet<>();
			Node w;
			do {
				w = stack.pop();
				onStack.replace(node, false);
				tmp.add(w);
			} while (node != w);
			strong.add(tmp);
		}
	}

	static public boolean run(DataFlowModel model) {
		init();
		boolean check = true;
		for (Node node : model.getResourceDependencyGraph().getNodes()) {
			if (ids.containsKey(node)) {
				strongconnect(node);
			}
		}
//		System.out.println(strong.size() + " " + model.getResourceDependencyGraph().getNodes().size());
		/*
		 * for(ChannelGenerator cg:model.getChannelGenerators()) {
		 * DataflowChannelGenerator data = (DataflowChannelGenerator)cg;
		 * for(ChannelMember channel:data.getChannelMembers()) { for(ChannelMember
		 * another:data.getChannelMembers()) { if(!channel.equals(another)) { check =
		 * channel.getIdentifierTemplate() != another.getIdentifierTemplate(); } } } }
		 */
		return strong.size() == 0 && check;
	}
}
