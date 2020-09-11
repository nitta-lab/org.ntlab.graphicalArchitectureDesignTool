package code.ast;

import java.util.List;
import java.util.ArrayList;

public class TypeDeclaration extends AbstractTypeDeclaration {
	private List<FieldDeclaration> fields = new ArrayList<>();
	private List<MethodDeclaration> methods = new ArrayList<>();
	private List<VariableDeclaration> constructors = new ArrayList<>();
	
	public TypeDeclaration(String typeName) {
		this.typeName = typeName;
	}
	
	public TypeDeclaration(String typeName, List<FieldDeclaration> fields) {
		this.typeName = typeName;
		this.fields = fields;
	}
	
	public TypeDeclaration(String typeName, List<FieldDeclaration> fields, List<MethodDeclaration> methods) {
		this.typeName = typeName;
		this.fields = fields;
		this.methods = methods;
	}
	
	public TypeDeclaration(String typeName, List<FieldDeclaration> fields, List<MethodDeclaration> methods,List<VariableDeclaration> constructors) {
		this.typeName = typeName;
		this.fields = fields;
		this.methods = methods;
		this.constructors = constructors;
	}
	
	public void addField(FieldDeclaration field) {
		fields.add(field);
	}
	
	public void addMethod(MethodDeclaration method) {
		methods.add(method);
	}

	public void addConstructors(VariableDeclaration constructor) {
		this.constructors.add(constructor);
	}

	public List<FieldDeclaration> getFields() {
		return fields;
	}
	
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	
	public List<VariableDeclaration> getConstructors() {
		return constructors;
	}
	
	public String toString() {
		String code = "public class " + typeName + " {\n";
		for (FieldDeclaration f: fields) {
			code += "\t" + f.toString();
		}
		for (MethodDeclaration m: methods) {
			code += CodeUtil.insertTab(m.toString());
		}
		code += "}";
		return code;
	}
}
