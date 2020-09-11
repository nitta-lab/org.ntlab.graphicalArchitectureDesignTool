package code.ast;

import models.algebra.Type;

public class FieldDeclaration extends BodyDeclaration {
	private Type type;
	private String fieldName;
	private String initializer;
	
	public FieldDeclaration(Type type, String fieldName) {
		this.type = type;
		this.fieldName = fieldName;
	}
	
	public FieldDeclaration(Type type, String fieldName, String initializer) {
		this.type = type;
		this.fieldName = fieldName;
		this.initializer = initializer;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return fieldName;
	}

	public void setName(String fieldName) {
		this.fieldName = fieldName;
	}	

	public String getInitializer() {
		return initializer;
	}

	public void setInitializer(String initializer) {
		this.initializer = initializer;
	}

	public String toString() {
		if (initializer == null) {
			return "private " + type.getImplementationTypeName() + " " + fieldName + ";\n";
		}
		return "private " + type.getImplementationTypeName() + " " + fieldName + " = " + initializer + ";\n";
	}
}
