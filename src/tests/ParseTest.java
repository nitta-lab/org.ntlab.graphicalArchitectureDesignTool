package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import models.Edge;
import models.algebra.InvalidMessage;
import models.algebra.ParameterizedIdentifierIsFutureWork;
import models.algebra.UnificationFailed;
import models.algebra.ValueUndefined;
import models.dataConstraintModel.ChannelGenerator;
import models.dataConstraintModel.ChannelMember;
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

public class ParseTest {

	public static void main(String[] args) {
		File file = new File("models/POS.model");
		try {
			Parser parser = new Parser(new BufferedReader(new FileReader(file)));
			DataFlowModel model;
			try {
				model = parser.doParse();
				System.out.println(model);
				
				for (ChannelGenerator c: model.getChannelGenerators()) {
					for (ChannelMember out: ((DataflowChannelGenerator) c).getOutputChannelMembers()) {
						System.out.println("next" + out.getIdentifierTemplate().getResourceName() + " = " + ((DataflowChannelGenerator) c).deriveUpdateExpressionOf(out).toImplementation());
					}
				}
				
				System.out.println();
				
				ResourceDependencyGraph resourceDependencyGraph = model.getResourceDependencyGraph();
				for (Edge e: resourceDependencyGraph.getEdges()) {
					System.out.println(e.getSource() + "-(" + e + ")->" + e.getDestination());
				}
			} catch (ExpectedChannel | ExpectedChannelName | ExpectedLeftCurlyBracket | ExpectedInOrOutKeyword
					| ExpectedStateTransition | ExpectedEquals | ExpectedRHSExpression | WrongLHSExpression
					| WrongRHSExpression | ExpectedRightBracket | ParameterizedIdentifierIsFutureWork 
					| ResolvingMultipleDefinitionIsFutureWork | InvalidMessage
					| UnificationFailed | ValueUndefined e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
