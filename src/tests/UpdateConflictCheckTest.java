package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import algorithms.*;
import models.dataFlowModel.DataFlowModel;
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

public class UpdateConflictCheckTest {
	public static void main(String[] args) {
		File file = new File("models/POS2.model");
		try {
			Parser parser = new Parser(new BufferedReader(new FileReader(file)));
			try {
				DataFlowModel model = parser.doParse();
				System.out.println(UpdateConflictCheck.run(model));
			} catch (ExpectedRightBracket e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpectedChannel e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpectedChannelName e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpectedLeftCurlyBracket e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpectedInOrOutKeyword e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpectedStateTransition e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpectedEquals e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpectedRHSExpression e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongLHSExpression e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongRHSExpression e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
