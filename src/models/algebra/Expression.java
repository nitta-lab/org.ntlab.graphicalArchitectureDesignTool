package models.algebra;

import java.util.HashMap;

public abstract class Expression implements Cloneable {
	public abstract Expression getSubTerm(Position pos);
	public abstract Expression unify(Expression another);
	public abstract Expression getInverseMap(Expression outputValue, Position targetPos);
	public abstract boolean contains(Expression exp);
	public abstract Object clone();
	public abstract <T extends Expression> HashMap<Position, T> getSubTerms(Class<T> clazz);
	
	public HashMap<Position, Variable> getVariables() {
		return getSubTerms(Variable.class);
	}
	
	public String toImplementation() {
		return toString();
	}
}
