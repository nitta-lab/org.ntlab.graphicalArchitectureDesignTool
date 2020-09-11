package models.algebra;

public class Symbol {
	private String name;
	private String implName;
	private int arity = 0;
	private Type operatorType = Type.PREFIX;
	private Type implOperatorType = Type.PREFIX;
	private Symbol[] inverses = null;
	private models.algebra.Type[] signature = null;
	private int[] implParamOrder = null;
	
	public Symbol(String name) {
		this.name = name;
		this.implName = name;
		this.arity = 0;
	}
	
	public Symbol(String name, int arity) {
		this.name = name;
		this.implName = name;
		this.arity = arity;
	}
	
	public Symbol(String name, int arity, Type operatorType) {
		this(name, arity);
		this.operatorType = operatorType;
		this.implOperatorType = operatorType;
	}
	
	public Symbol(String name, int arity, Type operatorType, String implName, Type implOperatorType) {
		this.name = name;
		this.implName = implName;
		this.arity = arity;
		this.operatorType = operatorType;
		this.implOperatorType = implOperatorType;
	}
	
	public Symbol(String name, int arity, Type operatorType, String implName, Type implOperatorType, int[] implParamOrder) {
		this.name = name;
		this.implName = implName;
		this.arity = arity;
		this.operatorType = operatorType;
		this.implOperatorType = implOperatorType;
		this.implParamOrder = implParamOrder;
	}
	
	public void setArity(int arity) {
		this.arity = arity;
	}

	public int getArity() {
		return arity;
	}

	public String getName() {
		return name;
	}

	public Type getOperatorType() {
		return operatorType;
	}
	
	public boolean isInfix() {
		return (operatorType == Type.INFIX);
	}
	
	public boolean isMethod() {
		return (operatorType == Type.METHOD || operatorType == Type.METHOD_WITH_SIDE_EFFECT);
	}

	public Symbol[] getInverses() {
		return inverses;
	}

	public void setInverses(Symbol[] inverses) {
		this.inverses = inverses;
	}

	public models.algebra.Type[] getSignature() {
		return signature;
	}

	public void setSignature(models.algebra.Type[] signature) {
		this.signature = signature;
	}

	public String getImplName() {
		return implName;
	}

	public void setImplName(String implName) {
		this.implName = implName;
	}

	public Type getImplOperatorType() {
		return implOperatorType;
	}
	
	public boolean isImplInfix() {
		return (implOperatorType == Type.INFIX);
	}
	
	public boolean isImplMethod() {
		return (implOperatorType == Type.METHOD || implOperatorType == Type.METHOD_WITH_SIDE_EFFECT);
	}

	public void setImplOperatorType(Type implOperatorType) {
		this.implOperatorType = implOperatorType;
	}
	
	public int[] getImplParamOrder() {
		return implParamOrder;
	}

	public boolean equals(Object another) {
		if (!(another instanceof Symbol)) return false;
		return name.equals(((Symbol) another).name) && arity == ((Symbol) another).arity;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public String toString() {
		return name;
	}
	
	public String toImplementation() {
		return implName;
	}
	
	public enum Type {
		PREFIX,
		INFIX,
		METHOD,
		METHOD_WITH_SIDE_EFFECT
	}
	
	public Memento createMemento() {
		return new Memento(implName, implOperatorType);
	}
	
	public void setMemento(Memento memento) {
		this.implName = memento.implName;
		this.implOperatorType = memento.implOperatorType;
	}
	
	public static class Memento {
		private String implName;
		private Type implOperatorType = Type.PREFIX;
		
		public Memento(String implName, Type implOperatorType) {
			this.implName = implName;
			this.implOperatorType = implOperatorType;
		}
	}
}
