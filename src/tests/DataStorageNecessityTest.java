package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import algorithms.NecessityOfStoringResourceStates;
import models.Node;
import models.dataFlowModel.*;
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

public class DataStorageNecessityTest {
	public static void main(String[] args) {
		File file = new File("models/POS2.model");
		try {
			Parser parser = new Parser(new BufferedReader(new FileReader(file)));
			DataFlowModel model;
			try {
				model = parser.doParse();
				System.out.println(model);
				ResourceDependencyGraph graph = NecessityOfStoringResourceStates.doDecide(model);
				for (Node n:graph.getNodes()) {
					ResourceNode resource = (ResourceNode)n;
					if((StoreAttribute)resource.getAttribute() != null) {
						System.out.println(resource.toString() + ":"  + ((StoreAttribute)resource.getAttribute()).isNeeded());
					}
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
