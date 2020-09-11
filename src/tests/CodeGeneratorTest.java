package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import algorithms.*;
import code.ast.CompilationUnit;
import code.ast.TypeDeclaration;
import models.dataFlowModel.*;
import parser.*;

public class CodeGeneratorTest {
	public static void main(String[] args) {
		File file = new File("models/POS.model");
		try {
			Parser parser = new Parser(new BufferedReader(new FileReader(file)));
			DataFlowModel model;
			try {
				model = parser.doParse();
				ResourceDependencyGraph graph = NecessityOfStoringResourceStates.doDecide(model);
				SelectableDataTransfers.init(graph);
				FinalDecisionOfStoringResourceStates.doDecide(graph);
				ArrayList<CompilationUnit> codetree = MethodBodyGenerator.doGenerate(graph, model, CodeGenerator.doGenerate(graph, model));
				System.out.println(codetree);
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
