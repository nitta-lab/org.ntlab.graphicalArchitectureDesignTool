package models.dataConstraintModel;

import models.algebra.Type;

public class IdentifierTemplate {
	private String resourceName = null;
	private Type resourceStateType = null;
	private int numParameters = 0;
	
	public IdentifierTemplate(String resourceName, int numParameters) {
		this.resourceName = resourceName;
		this.numParameters =numParameters;
	}

	public IdentifierTemplate(String resourceName, Type resourceStateType, int numParameters) {
		this.resourceName = resourceName;
		this.resourceStateType = resourceStateType;
		this.numParameters = numParameters;
	}

	public String getResourceName() {
		return resourceName;
	}
	
	public int getNumberOfParameters() {
		return numParameters;
	}

	public Type getResourceStateType() {
		return resourceStateType;
	}

	public void setResourceStateType(Type resourceStateType) {
		this.resourceStateType = resourceStateType;
	}
	
	public boolean equals(Object another) {
		if (!(another instanceof IdentifierTemplate)) return false;
		return resourceName.equals(((IdentifierTemplate) another).resourceName);
	}
	
	public int hashCode() {
		return resourceName.hashCode();
	}
}
