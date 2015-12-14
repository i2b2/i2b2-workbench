package edu.harvard.i2b2.timeline.external;

/*
 * RegExpParser.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
import java.util.*;

/*
 * A class that build a tree from a regular expression.
 */
class RegExpParser {
    private static final int TK_END = 0; // end of string
    private static final int TK_CHAR = 1; // character
    private static final int TK_CHARCLASS = 2; // 'X-Y' in character class
    private static final int TK_UNION = 10; // '|'
    private static final int TK_LPAR = 11; // '('
    private static final int TK_RPAR = 12; // ')'
    private static final int TK_CLOSURE = 13; // '*'
    private static final int TK_PLUS = 14; // '+'
    private static final int TK_QUESTION = 15; // '?'
    private static final int TK_ANYCHAR = 16; // '.'
    private static final int TK_LBRA = 17; // '['
    private static final int TK_LBRANEG = 18; // '[^'
    private static final int TK_RBRA = 19; // ']'
    private static final int TK_LHEAD = 20; // '^'
    private static final int TK_LTAIL = 21; // '$'

    private static final char CHAR_VL = '|';
    private static final char CHAR_LPAR = '(';
    private static final char CHAR_RPAR = ')';
    private static final char CHAR_ASTERISK = '*';
    private static final char CHAR_PLUS = '+';
    private static final char CHAR_QUESTION = '?';
    private static final char CHAR_DOT = '.';
    private static final char CHAR_LBRA = '[';
    private static final char CHAR_RBRA = ']';
    private static final char CHAR_CARET = '^';
    private static final char CHAR_DOLLAR = '$';
    private static final char CHAR_MINUS = '-';
    private static final char CHAR_BKSLASH = '\\';

    private static final String MSG_INVALID_ESCAPE = "Invalid escape character.";
    private static final String MSG_INVALID_CHARRANGE = "Invalid character range.";
    private static final String MSG_RBRA_EXPECTED = "\"]\" is expected.";
    private static final String MSG_RPAR_EXPECTED = "\")\" is expected.";
    private static final String MSG_CHAR_EXPECTED = "Normal character is expected.";

    private int currentToken;
    private Chars tokenChars;
    private String strbuff;
    private int pstr;
    private boolean inCharClass = false;
    private static Hashtable escapeTable;

    static {
	escapeTable = new Hashtable();
	escapeTable.put("0", "\0");
	escapeTable.put("b", "\b");
	escapeTable.put("t", "\t");
	escapeTable.put("r", "\r");
	escapeTable.put("n", "\n");
	escapeTable.put("d", "[0-9]");
	escapeTable.put("D", "[^0-9]");
	escapeTable.put("s", "[ \t\r\n]");
	escapeTable.put("S", "[^ \t\r\n]");
	escapeTable.put("w", "[0-9A-Z_a-z]");
	escapeTable.put("W", "[^0-9A-Z_a-z]");
    }

    /*
     * Constructs a parser.
     */
    public RegExpParser() {
	strbuff = "";
	pstr = 0;
    }

    /*
     * Initialize variables.
     */
    private void initialize(String str) throws RegExpSyntaxException {
	strbuff = processEscape(str);
	pstr = 0;
	nextToken();
    }

    private String processEscape(String text) {
	int i;
	char c;
	String str, escapedString;
	String result = "";

	for (i = 0; i < text.length(); i++) {
	    if ((c = text.charAt(i)) == CHAR_BKSLASH) {
		i++;
		escapedString = text.substring(i, i + 1);
		str = (String) escapeTable.get(escapedString);
		if (str == null) {
		    result = result + "\\" + escapedString;
		} else {
		    result = result + str;
		}
	    } else
		result = result + c;
	}

	return result;
    }

    /*
     * Gets a next token.
     */
    private void nextToken() throws RegExpSyntaxException {
	int x;

	if (inCharClass) {
	    x = getTokenCC();
	    if (x == TK_RBRA)
		inCharClass = false;
	} else {
	    x = getTokenStd();
	    if (x == TK_LBRA || x == TK_LBRANEG)
		inCharClass = true;
	}
    }

    /*
     * Gets a token.
     */
    private int getTokenStd() throws RegExpSyntaxException {
	char c;

	if (pstr == strbuff.length())
	    currentToken = TK_END;
	else {
	    c = strbuff.charAt(pstr++);
	    switch (c) {
	    case CHAR_VL:
		currentToken = TK_UNION;
		break;
	    case CHAR_LPAR:
		currentToken = TK_LPAR;
		break;
	    case CHAR_RPAR:
		currentToken = TK_RPAR;
		break;
	    case CHAR_ASTERISK:
		currentToken = TK_CLOSURE;
		break;
	    case CHAR_PLUS:
		currentToken = TK_PLUS;
		break;
	    case CHAR_QUESTION:
		currentToken = TK_QUESTION;
		break;
	    case CHAR_DOT:
		currentToken = TK_ANYCHAR;
		break;
	    case CHAR_CARET:
		currentToken = TK_LHEAD;
		tokenChars = new Chars(c);
		break;
	    case CHAR_DOLLAR:
		currentToken = TK_LTAIL;
		tokenChars = new Chars(c);
		break;
	    case CHAR_LBRA:
		if (pstr == strbuff.length()) {
		    throw new RegExpSyntaxException(MSG_RBRA_EXPECTED);
		} else {
		    c = strbuff.charAt(pstr++);
		    if (c == CHAR_CARET)
			currentToken = TK_LBRANEG;
		    else {
			pstr--;
			currentToken = TK_LBRA;
		    }
		}
		break;
	    case CHAR_BKSLASH:
		if (pstr == strbuff.length())
		    throw new RegExpSyntaxException(MSG_INVALID_ESCAPE);
		currentToken = TK_CHAR;
		tokenChars = new Chars(strbuff.charAt(pstr++));
		break;
	    default:
		currentToken = TK_CHAR;
		tokenChars = new Chars(c);
		break;
	    }
	}
	return currentToken;
    }

    /**
     * Gets a token in character class "[...]".
     */
    private int getTokenCC() throws RegExpSyntaxException {
	char c, c2, c3;

	if (pstr == strbuff.length()) {
	    throw new RegExpSyntaxException(MSG_RBRA_EXPECTED);
	} else {
	    c = strbuff.charAt(pstr++);
	    if (c == CHAR_RBRA) {
		currentToken = TK_RBRA;
	    } else {
		if (c == CHAR_BKSLASH) {
		    if (pstr == strbuff.length())
			throw new RegExpSyntaxException(
				"Illegal escape sequense.");
		    c = strbuff.charAt(pstr++);
		}
		currentToken = TK_CHAR;
		c2 = strbuff.charAt(pstr++);
		if (c2 == CHAR_MINUS) {
		    if (pstr == strbuff.length())
			throw new RegExpSyntaxException(MSG_RBRA_EXPECTED);
		    c3 = strbuff.charAt(pstr++);
		    if (c3 == CHAR_RBRA) {
			pstr = pstr - 2;
			tokenChars = new Chars(c);
		    } else {
			if (c3 == CHAR_BKSLASH) {
			    if (pstr == strbuff.length())
				throw new RegExpSyntaxException(
					MSG_INVALID_ESCAPE);
			    c3 = strbuff.charAt(pstr++);
			}
			currentToken = TK_CHARCLASS;
			if (c > c3)
			    throw new RegExpSyntaxException(
				    MSG_INVALID_CHARRANGE);
			tokenChars = new Chars(c, c3);
		    }
		} else {
		    pstr--;
		    tokenChars = new Chars(c);
		}
	    }
	}

	return currentToken;
    }

    /**
     * Parse <regexp>. Analize selection X|Y.
     */
    private RTree regexp() throws RegExpSyntaxException {
	RTree x;

	x = term();
	while (currentToken == TK_UNION) {
	    nextToken();
	    x = new RTree(RTree.OP_UNION, x, term());
	}
	return x;
    }

    /**
     * Parse <term>. Analize concat XY.
     */
    private RTree term() throws RegExpSyntaxException {
	RTree x;

	if (currentToken == TK_UNION || currentToken == TK_RPAR
		|| currentToken == TK_END)
	    x = new RTree(RTree.OP_EMPTY, null, null);
	else {
	    x = factor();
	    while (currentToken != TK_UNION && currentToken != TK_RPAR
		    && currentToken != TK_END) {
		x = new RTree(RTree.OP_CONCAT, x, factor());
	    }
	}
	return x;
    }

    /**
     * Parse <factor>. Analize closure X*, X+, X?.
     */
    private RTree factor() throws RegExpSyntaxException {
	RTree x;

	x = primary();
	if (currentToken == TK_CLOSURE) {
	    x = new RTree(RTree.OP_CLOSURE, x, null);
	    nextToken();
	} else if (currentToken == TK_PLUS) {
	    x = new RTree(RTree.OP_CONCAT, x, new RTree(RTree.OP_CLOSURE, x,
		    null));
	    nextToken();
	} else if (currentToken == TK_QUESTION) {
	    x = new RTree(RTree.OP_UNION, x, new RTree(RTree.OP_EMPTY, null,
		    null));
	    nextToken();
	}
	return x;
    }

    /**
     * Parse <primary>. Analize character (X).
     */
    private RTree primary() throws RegExpSyntaxException {
	RTree x;

	switch (currentToken) {
	case TK_CHAR:
	    x = new RTree(tokenChars);
	    nextToken();
	    break;
	case TK_LHEAD:
	    x = new RTree(RTree.OP_LHEAD, tokenChars, null, null);
	    nextToken();
	    break;
	case TK_LTAIL:
	    x = new RTree(RTree.OP_LTAIL, tokenChars, null, null);
	    nextToken();
	    break;
	case TK_ANYCHAR:
	    x = new RTree(new Chars(Character.MIN_VALUE, Character.MAX_VALUE));
	    nextToken();
	    break;
	case TK_LPAR:
	    nextToken();
	    x = regexp();
	    if (currentToken != TK_RPAR)
		throw new RegExpSyntaxException(MSG_RPAR_EXPECTED);
	    nextToken();
	    break;
	case TK_LBRA:
	    x = charClass();
	    if (currentToken != TK_RBRA)
		throw new RegExpSyntaxException(MSG_RBRA_EXPECTED);
	    nextToken();
	    break;
	case TK_LBRANEG:
	    x = negativeCharClass();
	    if (currentToken != TK_RBRA)
		throw new RegExpSyntaxException(MSG_RBRA_EXPECTED);
	    nextToken();
	    break;
	default:
	    throw new RegExpSyntaxException(MSG_CHAR_EXPECTED);
	}
	return x;
    }

    /*
     * Parse <charclass>. Analize characters in [...].
     */
    private RTree charClass() throws RegExpSyntaxException {
	RTree x;

	nextToken();
	if (currentToken == TK_RBRA)
	    throw new RegExpSyntaxException("Invalid character class.");
	x = new RTree(tokenChars);
	nextToken();
	while (currentToken != TK_RBRA) {
	    x = new RTree(RTree.OP_UNION, x, new RTree(tokenChars));
	    nextToken();
	}
	return x;
    }

    /*
     * Parse <negative charclass>. Analize characters in [^...].
     */
    private RTree negativeCharClass() throws RegExpSyntaxException {
	RTree x;

	nextToken();
	if (currentToken == TK_RBRA)
	    throw new RegExpSyntaxException("Invalid character class.");
	x = new RTree(new Chars(Character.MIN_VALUE, Character.MAX_VALUE));
	while (currentToken != TK_RBRA) {
	    x.removeChars(tokenChars);
	    nextToken();
	}
	return x;
    }

    /*
     * Parse a regular expression.
     */
    public RTree parse(String pattern) throws RegExpSyntaxException {
	RTree t;

	initialize(pattern);
	t = regexp();
	if (currentToken != TK_END)
	    throw new RegExpSyntaxException(
		    "Extra character at end of pattren.");

	return t;
    }
}
