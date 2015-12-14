package edu.harvard.i2b2.timeline.external;

/*
 * RegExpDebug.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
import java.io.*;

/**
 * A class to debug RegExp
 */
class RegExpDebug {

	private RegExpDebug() {
	}

	/**
	 * It is called automatically by the system the application is started.
	 */
	public static void main(String args[]) {
		RegExp re = new RegExp();
		String pattern;

		DataInputStream in = new DataInputStream(System.in);
		try {
			while (true) {
				// System.out.println("Pattern ?");
				if ((pattern = in.readLine()) == null)
					break;
				re.setPattern(pattern);
				// System.out.println("Parse \"" + re + "\":");
				// System.out.println("dump tree...");
				// System.out.println(re.treeString());
				// System.out.println("dump NFA...");
				// System.out.println(re.nfaString());
				// System.out.println("dump DFA...");
				// System.out.println(re.dfaString());
			}
		} catch (IOException e) {
			System.out.println(e);
		} catch (RegExpSyntaxException e) {
			System.out.println(e);
		} catch (NFABuildException e) {
			System.out.println(e);
		}
	}

	/*
	 * Dumps DFA.
	 */
	public static String dfaToString(RegExpDFA dfa) {
		String result = "";
		DSList l;
		int i, j;

		for (i = 0; i < dfa.count(); i++) {
			result += "state " + i + (dfa.getDState(i).accepted() ? "A" : " ")
					+ ": ";
			for (l = dfa.getDState(i).next(); l != null; l = l.next()) {
				for (j = 0; j < dfa.count(); j++) {
					if (dfa.getDState(j) == l.to())
						break;
				}
				result += "(" + l.chars().toString() + " => " + j + ") ";
			}
			result += "... { ";
			for (j = 0; j < dfa.getNfa().count(); j++) {
				if (dfa.getDState(i).nfaStateSet().get(j))
					result += j + " ";
			}
			result += "} ";
			result += "\n";
		}
		return result;
	}

	/*
	 * Dumps NFA.
	 */
	public static String nfaToString(RegExpNFA nfa) {
		String result = "";
		int i;
		NList nl;

		for (i = 0; nfa.getNList(i) != null || i == nfa.exit(); i++) {
			if (nfa.getNList(i) != null) {
				result += "state " + i + ": ";
				for (nl = nfa.getNList(i); nl != null; nl = nl.next()) {
					result += "(" + nl.chars().toString() + " => " + nl.to()
							+ ") ";
				}
				result += "\n";
			} else if (i == nfa.exit()) {
				result += "state " + i + ": exit\n";
			}
		}
		return result;
	}

	/*
	 * Dumps the regular expression tree.
	 */
	public static String treeToString(RTree tree) {
		String result;
		switch (tree.operation()) {
		case RTree.OP_CHAR:
			result = tree.chars().toString();
			break;
		case RTree.OP_LHEAD:
			result = "LHEAD";
			break;
		case RTree.OP_LTAIL:
			result = "LTAIL";
			break;
		case RTree.OP_CONCAT:
			result = "(concat " + treeToString(tree.left()) + " "
					+ treeToString(tree.right()) + ")";
			break;
		case RTree.OP_UNION:
			result = "(or " + treeToString(tree.left()) + " "
					+ treeToString(tree.right()) + ")";
			break;
		case RTree.OP_CLOSURE:
			result = "(closure " + treeToString(tree.left()) + ")";
			break;
		case RTree.OP_EMPTY:
			result = "EMPTY";
			break;
		default:
			result = "This can't happen in <dumpTree>";
		}
		return result;
	}
}
