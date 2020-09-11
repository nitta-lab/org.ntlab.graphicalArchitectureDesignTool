package code.ast;

import models.algebra.Type;

public class VariableDeclaration extends ASTNode {
	private Type type;
	private String variableName;
	
	public VariableDeclaration(Type type, String variableName) {
		this.type = type;
		this.variableName = variableName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return variableName;
	}

	public void setName(String variableName) {
		this.variableName = variableName;
	}
	
	public String toString() {
		return type.getImplementationTypeName() + " " + variableName;
	}
}
