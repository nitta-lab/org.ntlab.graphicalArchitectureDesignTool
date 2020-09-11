package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.algebra.Constant;
import models.algebra.Expression;
import models.algebra.Symbol;
import models.algebra.Term;
import models.algebra.Type;
import models.algebra.Variable;
import models.dataConstraintModel.ChannelMember;
import models.dataConstraintModel.IdentifierTemplate;
import models.dataConstraintModel.StateTransition;
import models.dataFlowModel.DataFlowModel;
import models.dataFlowModel.DataflowChannelGenerator;

public class Parser {
	private BufferedReader reader;
	public static final String CHANNEL = "channel";
	public static final String LEFT_CURLY_BRACKET = "{";
	public static final String RIGHT_CURLY_BRACKET = "}";
	public static final String LEFT_CURLY_BRACKET_REGX = "\\{";
	public static final String RIGHT_CURLY_BRACKET_REGX = "\\}";
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";
	public static final String LEFT_BRACKET_REGX = "\\(";
	public static final String RIGHT_BRACKET_REGX = "\\)";
	public static final String ADD = "+";
	public static final String MUL = "*";
	public static final String SUB = "-";
	public static final String DIV = "/";
	public static final String MINUS = "-";
	public static final String ADD_REGX = "\\+";
	public static final String MUL_REGX = "\\*";
	public static final String SUB_REGX = "\\-";
	public static final String DIV_REGX = "/";
	public static final String IN = "in";
	public static final String OUT = "out";
	public static final String EQUALS = "==";
	public static final String COMMA = ",";
	public static final String COLON = ":";
		
	public Parser(BufferedReader reader) {
		this.reader = reader;
	}
	
	public DataFlowModel doParse() 
			throws ExpectedRightBracket, ExpectedChannel, ExpectedChannelName, ExpectedLeftCurlyBracket, ExpectedInOrOutKeyword, ExpectedStateTransition, ExpectedEquals, ExpectedRHSExpression, WrongLHSExpression, WrongRHSExpression {
		TokenStream stream = new TokenStream();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				stream.addLine(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return parseDataFlowModel(stream);
	}

	public static DataFlowModel parseDataFlowModel(TokenStream stream) 
			throws ExpectedRightBracket, ExpectedChannel, ExpectedChannelName, ExpectedLeftCurlyBracket, ExpectedInOrOutKeyword, ExpectedStateTransition, ExpectedEquals, ExpectedRHSExpression, WrongLHSExpression, WrongRHSExpression {
		DataFlowModel model = new DataFlowModel();
		DataflowChannelGenerator channel;
		while ((channel = parseChannel(stream, model)) != null) {
			if (channel.getInputChannelMembers().size() == 0) {
				model.addIOChannelGenerator(channel);
			} else {
				model.addChannelGenerator(channel);
			}
		}
		return model;
	}

	public static DataflowChannelGenerator parseChannel(TokenStream stream, DataFlowModel model) 
			throws ExpectedRightBracket, ExpectedChannel, ExpectedChannelName, ExpectedLeftCurlyBracket, ExpectedInOrOutKeyword, ExpectedStateTransition, ExpectedEquals, ExpectedRHSExpression, WrongLHSExpression, WrongRHSExpression {
		if (!stream.hasNext()) return null;
		String channelKeyword = stream.next();
		if (!channelKeyword.equals(CHANNEL)) throw new ExpectedChannel(stream.getLine());
		if (!stream.hasNext()) throw new ExpectedChannelName(stream.getLine());
		String channelName = stream.next();
		if (channelName.equals(LEFT_CURLY_BRACKET)) throw new ExpectedChannelName(stream.getLine());
		
		DataflowChannelGenerator channel = new DataflowChannelGenerator(channelName);
		String leftBracket = stream.next();
		if (!leftBracket.equals(LEFT_CURLY_BRACKET)) throw new ExpectedLeftCurlyBracket(stream.getLine());
		String inOrOut = null;
		while (stream.hasNext() && !(inOrOut = stream.next()).equals(RIGHT_CURLY_BRACKET)) {
			ChannelMember channelMember = null;
			if (inOrOut.equals(IN)) {
				channelMember = parseChannelMember(stream, model);
				if (channelMember != null) {
					channel.addChannelMemberAsInput(channelMember);
				}
			} else if (inOrOut.equals(OUT)) {
				channelMember = parseChannelMember(stream, model);
				if (channelMember != null) {
					channel.addChannelMemberAsOutput(channelMember);
				}				
			} else {
				throw new ExpectedInOrOutKeyword(stream.getLine());
			}
		}
		return channel;
	}

	public static ChannelMember parseChannelMember(TokenStream stream, DataFlowModel model) 
			throws ExpectedRightBracket, ExpectedStateTransition, ExpectedEquals, ExpectedRHSExpression, WrongLHSExpression, WrongRHSExpression {
		if (!stream.hasNext()) throw new ExpectedStateTransition(stream.getLine());
		Expression leftTerm = parseTerm(stream, model);
		if (leftTerm == null || !(leftTerm instanceof Term)) throw new WrongLHSExpression(stream.getLine());
		
		if (!stream.hasNext()) throw new ExpectedEquals(stream.getLine());
		String equals = stream.next();
		if (!equals.equals(EQUALS)) throw new ExpectedEquals(stream.getLine());
		
		if (!stream.hasNext()) throw new ExpectedRHSExpression(stream.getLine());		
		Expression rightTerm = parseTerm(stream, model);		
		if (rightTerm == null) throw new WrongRHSExpression(stream.getLine());
		
		String resourceName = ((Term) leftTerm).getSymbol().getName();
		IdentifierTemplate identifier = model.getIdentifierTemplate(resourceName);
		if (identifier == null) {
			identifier = new IdentifierTemplate(resourceName, 0);
			model.addIdentifierTemplates(identifier);
		}
		ChannelMember channelMember = new ChannelMember(identifier);
		StateTransition stateTransition = new StateTransition();
		stateTransition.setCurStateExpression(((Term) leftTerm).getChild(0));
		stateTransition.setMessageExpression(((Term) leftTerm).getChild(1));
		stateTransition.setNextStateExpression(rightTerm);
		channelMember.setStateTransition(stateTransition);
		// for type definition
		if (identifier.getResourceStateType() == null && ((Term) leftTerm).getChild(0) instanceof Variable) {
			Variable stateVar = (Variable) ((Term) leftTerm).getChild(0);
			if (stateVar.getType() != null) {
				identifier.setResourceStateType(stateVar.getType());
			}
		}
		if (((Term) leftTerm).getChild(1) instanceof Term) {
			Term messageTerm = (Term) ((Term) leftTerm).getChild(1);
			if (messageTerm.getSymbol().getSignature() == null && messageTerm.getChildren().size() > 0) {
				Type[] signature = new Type[messageTerm.getChildren().size() + 1];
				int i = 1;
				for (Expression e: messageTerm.getChildren()) {
					if (e instanceof Variable && ((Variable) e).getType() != null) {
						signature[i] = ((Variable) e).getType();
					}
					i++;
				}
				messageTerm.getSymbol().setSignature(signature);
			}
		}
		return channelMember;
	}

	public static Expression parseTerm(TokenStream stream, DataFlowModel model) throws ExpectedRightBracket {
		ArrayList<Expression> expressions = new ArrayList<>();
		ArrayList<Symbol> operators = new ArrayList<>();
		String operator = null;
		for (;;) {
			String leftBracketOrMinus = stream.next();
			if (leftBracketOrMinus.equals(LEFT_BRACKET)) {
				Expression exp = parseTerm(stream, model);
				String rightBracket = stream.next();
				if (!rightBracket.equals(RIGHT_BRACKET)) throw new ExpectedRightBracket(stream.getLine());
				expressions.add(exp);
			} else {
				Symbol minus = null;
				String symbolName = null;
				if (leftBracketOrMinus.equals(MINUS)) {
					minus = DataFlowModel.minus;		// not sub
					symbolName = stream.next();
				} else {
					symbolName = leftBracketOrMinus;
				}
				Expression exp = null;
				if (stream.checkNext() != null && stream.checkNext().equals(LEFT_BRACKET)) {
					// a function symbol
					Symbol symbol = model.getSymbol(symbolName);
					if (symbol == null) {
						symbol = new Symbol(symbolName);
						model.addSymbol(symbol);
					}
					Term term = new Term(symbol);
					int arity = 0;
					do {
						stream.next();		// LEFT_BRACKET or COMMA
						arity++;
						Expression subTerm = parseTerm(stream, model);
						term.addChild(subTerm, true);
						if (!stream.hasNext()) throw new ExpectedRightBracket(stream.getLine());
					} while (stream.checkNext().equals(COMMA));
					String rightBracket = stream.next();
					if (!rightBracket.equals(RIGHT_BRACKET)) throw new ExpectedRightBracket(stream.getLine());
					symbol.setArity(arity);
					exp = term;
				} else {
					// constant or variable
					try {
						Double d = Double.parseDouble(symbolName);
						exp = new Constant(symbolName);
					} catch (NumberFormatException e) {
						if (stream.checkNext() != null && stream.checkNext().equals(COLON)) {
							// when a type is specified.
							stream.next();
							String typeName = stream.next();
							Type type = model.getType(typeName);
							if (type == null) {
								type = new Type(typeName, typeName);
							}
							exp = new Variable(symbolName, type);							
						} else {
							exp = new Variable(symbolName);
						}
					}
				}
				if (minus != null) {
					Term minusTerm = new Term(minus);
					minusTerm.addChild(exp);
					expressions.add(minusTerm);
				} else {
					expressions.add(exp);
				}
			}
			operator = stream.checkNext();
			if (operator == null) {
				break;
			} else if (operator.equals(ADD)) {
				operators.add(DataFlowModel.add);
			} else if (operator.equals(MUL)) {
				operators.add(DataFlowModel.mul);
			} else if (operator.equals(SUB)) {
				operators.add(DataFlowModel.sub);	// not minus
			} else if (operator.equals(DIV)) {
				operators.add(DataFlowModel.div);
			} else {
				break;
			}
			stream.next();		// an arithmetic operator
		}
		if (expressions.size() == 1) {
			// no arithmetic operators
			return expressions.get(0);
		}
		ArrayList<Expression> monomials = new ArrayList<>();
		ArrayList<Symbol> addSubs = new ArrayList<>();
		Expression first = expressions.get(0);
		int i = 1;
		for (Symbol op: operators) {
			Expression second = expressions.get(i);
			if (op.getName().equals(MUL) || op.getName().equals(DIV)) {
				Term term = new Term(op);
				term.addChild(first);
				term.addChild(second);
				first = term;
			} else {
				// add or sub ==> new monomial
				monomials.add(first);
				addSubs.add(op);
				first = second;
			}
			i++;
		}
		if (first != null) monomials.add(first);
		Expression firstMonomial = monomials.get(0);
		i = 1;
		for (Symbol op: addSubs) {
			Expression secondMonomial = monomials.get(i);
			Term term = new Term(op);
			term.addChild(firstMonomial);
			term.addChild(secondMonomial);
			firstMonomial = term;
			i++;
		}
		return firstMonomial;
	}

	public static class TokenStream {
		private ArrayList<ArrayList<String>> tokens = new ArrayList<>();
		private int line = 0;
		private int n = 0;
		
		public TokenStream() {
			line = 0;
			n = 0;
		}
		
		public void addLine(String line) {
			line = line.trim();
			tokens.add(
					splitBy(
							splitBy(
									splitBy(
											splitBy(
													splitBy(
															splitBy(
																	splitBy(
																			splitBy(
																					splitBy(
																							splitBy(
																									splitBy(
																											Arrays.asList(line.split("[ \t]")), 
																											ADD,
																											ADD_REGX),
																									MUL,
																									MUL_REGX),
																							SUB,
																							SUB_REGX),
																					DIV,
																					DIV_REGX),
																			COMMA, 
																			COMMA),
																	COLON, 
																	COLON),
															LEFT_BRACKET, 
															LEFT_BRACKET_REGX),
													RIGHT_BRACKET,
													RIGHT_BRACKET_REGX),
											EQUALS,
											EQUALS),
									LEFT_CURLY_BRACKET,
									LEFT_CURLY_BRACKET_REGX),
							RIGHT_CURLY_BRACKET,
							RIGHT_CURLY_BRACKET_REGX));
		}
		
		private ArrayList<String> splitBy(List<String> tokens, String delimiter, String delimiterRegx) {
			ArrayList<String> newTokens = new ArrayList<>();
			for (String token: tokens) {
				String[] splitTokens = token.split(delimiterRegx);
				boolean fFirstToken = true;
				for (String t: splitTokens) {
					if (!fFirstToken) {
						newTokens.add(delimiter);
					}
					if (t.length() > 0) {
						newTokens.add(t);
					}
					fFirstToken = false;
				}
				while (token.endsWith(delimiter)) {
					newTokens.add(delimiter);
					token = token.substring(0, token.length() - 1);
				}
			}
			return newTokens;
		}
		
		public String next() {
			if (line >= tokens.size()) return null;
			while (n >= tokens.get(line).size()) {
				line++;
				n = 0;
				if (line >= tokens.size()) return null;
			}
			String token = tokens.get(line).get(n);
			n++;
			return token;
		}
				
		public String checkNext() {
			if (line >= tokens.size()) return null;
			while (n >= tokens.get(line).size()) {
				line++;
				n = 0;
				if (line >= tokens.size()) return null;
			}
			return tokens.get(line).get(n);
		}
		
		public boolean hasNext() {
			if (line >= tokens.size()) return false;
			while (n >= tokens.get(line).size()) {
				line++;
				n = 0;
				if (line >= tokens.size()) return false;
			}
			return true;
		}
		
		public int getLine() {
			return line;
		}
	}
}
