package models.algebra;

public class Type {
	private String typeName;
	private String implementationTypeName;
	
	public Type(String typeName, String implementastionTypeName) {
		this.typeName = typeName;
		this.implementationTypeName = implementastionTypeName;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public String getImplementationTypeName() {
		return implementationTypeName;
	}
	
	public void setImplementationTypeName(String implementastionTypeName) {
		this.implementationTypeName = implementastionTypeName;
	}
}
