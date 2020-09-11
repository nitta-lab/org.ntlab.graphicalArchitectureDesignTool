package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import algorithms.*;
import models.Node;
import models.dataFlowModel.*;
import parser.*;

public class DataStorageDecisionTest {
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
				FinalDecisionOfStoringResourceStates.doDecide(graph);
				for(Node n:graph.getNodes()) {
					System.out.println(((ResourceNode) n).getIdentifierTemplate().getResourceName() + ":" + ((StoreAttribute) ((ResourceNode) n).getAttribute()).isStored());
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
