package code.ast;

import models.algebra.Type;

public class ConstructorDeclaration extends FieldDeclaration {
	private VariableDeclaration constructor;

	public ConstructorDeclaration(Type type, String fieldName,VariableDeclaration constructor) {
		super(type, fieldName);
		this.setConstructor(constructor);
	}

	public VariableDeclaration getConstructor() {
		return constructor;
	}

	public void setConstructor(VariableDeclaration constructor) {
		this.constructor = constructor;
	}

}
