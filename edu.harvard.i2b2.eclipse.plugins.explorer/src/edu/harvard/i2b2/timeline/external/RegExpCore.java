package edu.harvard.i2b2.timeline.external;

/*
 * RegExpCore.java
 * 
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
/**
 * A class that handles regular expressions.
 */
class RegExpCore {
	protected String pattern;
	protected RegExpDFA dfa;

	/**
	 * constructor.
	 */
	public RegExpCore() {
	}

	/**
	 * constructor.
	 */
	public RegExpCore(String pattern) throws RegExpSyntaxException,
			NFABuildException {
		setPattern(pattern);
	}

	/**
	 * Sets the regular expression string.
	 * <p>
	 * 
	 * @param regexp
	 *            the regular expression.
	 * @exception RegExpSyntaxException
	 *                If the regular expression syntax is invalid.
	 * @exception NFABuildException
	 */
	public void setPattern(String pattern) throws RegExpSyntaxException,
			NFABuildException {
		RTree tree;
		RegExpParser parser;
		RegExpNFA nfa;

		this.pattern = pattern;
		parser = new RegExpParser();
		tree = parser.parse(pattern);
		nfa = new RegExpNFA(tree);
		dfa = new RegExpDFA(nfa);
	}

	public String pattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return pattern;
	}

	public String treeString() {
		return RegExpDebug.treeToString(dfa.getTree());
	}

	public String nfaString() {
		return RegExpDebug.nfaToString(dfa.getNfa());
	}

	public String dfaString() {
		return RegExpDebug.dfaToString(dfa);
	}
}
