package models.dataConstraintModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import models.algebra.Symbol;
import models.algebra.Type;
import parser.Parser;

public class DataConstraintModel {
	protected HashMap<String, IdentifierTemplate> identifierTemplates = null;
	protected HashMap<String, ChannelGenerator> channelGenerators = null;
	protected HashMap<String, ChannelGenerator> ioChannelGenerators = null;
	protected HashMap<String, Type> types = null;
	protected HashMap<String, Symbol> symbols = null;
	public static final Type typeInt = new Type("Int", "int");
	public static final Type typeFloat = new Type("Float", "float");
	public static final Type typeDouble = new Type("Double", "double");
	public static final Type typeList = new Type("List", "ArrayList<Integer>");
	public static final Type typeBoolean = new Type("Bool", "boolean");
	public static final Symbol add = new Symbol(Parser.ADD, 2, Symbol.Type.INFIX);
	public static final Symbol mul = new Symbol(Parser.MUL, 2, Symbol.Type.INFIX);;
	public static final Symbol sub = new Symbol(Parser.SUB, 2, Symbol.Type.INFIX);
	public static final Symbol div = new Symbol(Parser.DIV, 2, Symbol.Type.INFIX);
	public static final Symbol minus = new Symbol(Parser.MINUS, 1);
	public static final Symbol cons = new Symbol("cons", 2, Symbol.Type.PREFIX, "add", Symbol.Type.METHOD_WITH_SIDE_EFFECT, new int[] {1, 0});
	public static final Symbol head = new Symbol("head", 1, Symbol.Type.PREFIX, "head", Symbol.Type.METHOD);
	public static final Symbol body = new Symbol("tail", 1, Symbol.Type.PREFIX, "tail", Symbol.Type.METHOD);
	public static final Symbol cond = new Symbol("if", 3);
	public static final Symbol eq = new Symbol("eq", 2, Symbol.Type.PREFIX, "==", Symbol.Type.INFIX);
	
	static {
		add.setInverses(new Symbol[] {sub, sub});
		mul.setInverses(new Symbol[] {div, div});
		sub.setInverses(new Symbol[] {add});
		div.setInverses(new Symbol[] {mul});
		minus.setInverses(new Symbol[] {minus});
		cons.setInverses(new Symbol[] {head, body});
		cons.setSignature(new Type[] {typeList, null, typeList});
	}
	
	public DataConstraintModel() {
		identifierTemplates = new HashMap<>();
		channelGenerators = new HashMap<>();
		ioChannelGenerators = new HashMap<>();
		types = new HashMap<>();
		addType(typeInt);
		addType(typeFloat);
		addType(typeDouble);
		addType(typeList);
		addType(typeBoolean);
		symbols = new HashMap<>();
		addSymbol(add);
		addSymbol(mul);
		addSymbol(sub);
		addSymbol(div);
		addSymbol(minus);
		addSymbol(cons);
		addSymbol(head);
		addSymbol(body);
		addSymbol(cond);
		addSymbol(eq);
	}
	
	public Collection<IdentifierTemplate> getIdentifierTemplates() {
		return identifierTemplates.values();
	}
	
	public IdentifierTemplate getIdentifierTemplate(String resourceName) {
		return identifierTemplates.get(resourceName);
	}
	
	public void addIdentifierTemplates(IdentifierTemplate identifierTemplate) {
		identifierTemplates.put(identifierTemplate.getResourceName(), identifierTemplate);
	}
	
	public void setIdentifierTemplates(HashMap<String, IdentifierTemplate> identifierTemplates) {
		this.identifierTemplates = identifierTemplates;
	}
	
	public Collection<ChannelGenerator> getChannelGenerators() {
		return channelGenerators.values();
	}
		
	public ChannelGenerator getChannelGenerator(String channelName) {
		return channelGenerators.get(channelName);
	}
	
	public void setChannelGenerators(HashMap<String, ChannelGenerator> channelGenerators) {
		this.channelGenerators = channelGenerators;
		for (ChannelGenerator g: channelGenerators.values()) {
			for (IdentifierTemplate id: g.getIdentifierTemplates()) {
				identifierTemplates.put(id.getResourceName(), id);				
			}
		}
	}
	
	public void addChannelGenerator(ChannelGenerator channelGenerator) {
		channelGenerators.put(channelGenerator.getChannelName(), channelGenerator);
		for (IdentifierTemplate id: channelGenerator.getIdentifierTemplates()) {
			identifierTemplates.put(id.getResourceName(), id);				
		}
	}
	
	public Collection<ChannelGenerator> getIOChannelGenerators() {
		return ioChannelGenerators.values();
	}
	
	public void setIOChannelGenerators(HashMap<String, ChannelGenerator> ioChannelGenerators) {
		this.ioChannelGenerators = ioChannelGenerators;
		for (ChannelGenerator g: ioChannelGenerators.values()) {
			for (IdentifierTemplate id: g.getIdentifierTemplates()) {
				identifierTemplates.put(id.getResourceName(), id);				
			}
		}
	}
	
	public void addIOChannelGenerator(ChannelGenerator ioChannelGenerator) {
		ioChannelGenerators.put(ioChannelGenerator.getChannelName(), ioChannelGenerator);
		for (IdentifierTemplate id: ioChannelGenerator.getIdentifierTemplates()) {
			identifierTemplates.put(id.getResourceName(), id);				
		}
	}
	
	public void addType(Type type) {
		types.put(type.getTypeName(), type);
	}
	
	public Type getType(String name) {
		return types.get(name);
	}
	
	public void addSymbol(Symbol symbol) {
		symbols.put(symbol.getName(), symbol);
	}
	
	public Symbol getSymbol(String name) {
		return symbols.get(name);
	}
	
	@Override
	public String toString() {
		String out = "";
		for (ChannelGenerator channelGenerator: ioChannelGenerators.values()) {
			out += channelGenerator.toString();
		}
		for (ChannelGenerator channelGenerator: channelGenerators.values()) {
			out += channelGenerator.toString();
		}
		return out;
	}
}
