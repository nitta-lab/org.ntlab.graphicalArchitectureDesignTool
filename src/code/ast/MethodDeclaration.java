package code.ast;

import java.util.List;
import java.util.ArrayList;
import models.algebra.Type;

public class MethodDeclaration extends BodyDeclaration {
	private String name = null;
	private boolean isConstructor = false;
	private Type returnType = null;
	private List<VariableDeclaration> parameters = null;
	private Block body = null;
	
	public MethodDeclaration(String methodName) {
		this(methodName, false);
	}
	
	public MethodDeclaration(String methodName,Type returnType) {
		this(methodName, false);
		this.returnType = returnType;
	}
	
	public MethodDeclaration(String methodName, boolean isConstructor) {
		this.name = methodName;
		this.isConstructor = isConstructor;
	}
	
	public MethodDeclaration(String methodName, boolean isConstructor, Type returnType, List<VariableDeclaration> parameters) {
		this(methodName, isConstructor, returnType, parameters, null);
	}
	
	public MethodDeclaration(String methodName, boolean isConstructor, Type returnType, List<VariableDeclaration> parameters, Block body) {
		this(methodName, isConstructor);
		this.returnType = returnType;
		this.parameters = parameters;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public List<VariableDeclaration> getParameters() {
		return parameters;
	}

	public void setParameters(List<VariableDeclaration> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(VariableDeclaration parameter) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		parameters.add(parameter);
	}

	public Block getBody() {
		return body;
	}

	public void setBody(Block body) {
		this.body = body;
	}
	
	public void addStatement(String statement) {
		if (body == null) {
			body = new Block();
		}
		body.addStatement(statement);
	}
	
	public void addFirstStatement(String statement) {
		if (body == null) {
			body = new Block();
		}
		body.addFirstStatement(statement);
	}
	
	public String toString() {
		String code = "public ";
		if (returnType == null) {
			if(!isConstructor) code += "void ";
		}else {
			code += returnType.getImplementationTypeName() + " ";
		}
		code += (name + "(");
		if (parameters != null) {
			String delimitar = "";
			for (VariableDeclaration parameter: parameters) {
				code = code + delimitar + parameter.toString();
				delimitar = ", ";
			}
		}
		code += ") {\n";
		if (body != null) {
			code += CodeUtil.insertTab(body.toString());
		}
		code += "}";
		return code;
	}
}
