package code.ast;

public class CodeUtil {
	
	public static String insertTab(String code) {
		String newString = "";
		String[] lines = code.split("\n");
		for (String line: lines) {
			newString = newString + "\t" + line + "\n";
		}
		return newString;
	}

}
