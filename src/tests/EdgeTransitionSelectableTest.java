package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import algorithms.*;
import models.Edge;
import models.dataFlowModel.*;
import parser.*;

public class EdgeTransitionSelectableTest {
	public static void main(String[] args) {
		File file = new File("models/POS2.model");
		try {
			Parser parser = new Parser(new BufferedReader(new FileReader(file)));
			DataFlowModel model;
			try {
				model = parser.doParse();
				System.out.println(model);
				ResourceDependencyGraph graph = NecessityOfStoringResourceStates.doDecide(model);
				SelectableDataTransfers.init(graph);
				for(Edge e:graph.getEdges()) {
					ResourceDependency re = (ResourceDependency) e;
					System.out.println(re.getSource() + "-" + re.getDestination() + ":" + ((PushPullAttribute)(re.getAttribute())).getOptions());
				}
			} catch (ExpectedChannel | ExpectedChannelName | ExpectedLeftCurlyBracket | ExpectedInOrOutKeyword
					| ExpectedStateTransition | ExpectedEquals | ExpectedRHSExpression | WrongLHSExpression
					| WrongRHSExpression | ExpectedRightBracket e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
