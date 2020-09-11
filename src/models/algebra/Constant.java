package models.algebra;

import java.util.ArrayList;

public class Constant extends Term {

	public Constant(String value) {
		super(new Symbol(value, 0), new ArrayList<Expression>());
	}
	
	public Constant(String value, Type type) {
		super(new Symbol(value, 0), new ArrayList<Expression>());
		symbol.setSignature(new Type[] {type});
	}
	
	public Constant(Symbol symbol) {
		super(symbol);
	}
	
	@Override
	public boolean equals(Object another) {
		if (!(another instanceof Constant)) return false;
		return symbol.equals(((Constant) another).symbol);
	}
	
	@Override
	public Object clone() {
		return new Constant(symbol);
	}
	
	public String toString() {
		return symbol.getName();
	}
	
	public String toImplementation() {
		return symbol.getImplName();
	}
}
