/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.methods;

import eu.svjatoslav.commons.string.tokenizer.InvalidSyntaxException;
import eu.svjatoslav.commons.string.tokenizer.Tokenizer;
import eu.svjatoslav.commons.string.tokenizer.TokenizerMatch;

public class Annotation {

	private String name;

	public Annotation(final Tokenizer tokenizer) throws InvalidSyntaxException {

		name = tokenizer.getNextToken().token;

		if (!tokenizer.probeNextToken("("))
			return;

		int depth = 1;

		while (true) {
			final TokenizerMatch token = tokenizer.getNextToken();

			if (token == null)
				return;

			if ("(".equals(token.token))
				depth++;
			if (")".equals(token.token))
				depth--;

			if (depth == 0)
				return;
		}

	}

}
