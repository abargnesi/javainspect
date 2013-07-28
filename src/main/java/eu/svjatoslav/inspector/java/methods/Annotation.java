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

			if ("(".equals(token.token))
				depth++;
			if (")".equals(token.token))
				depth--;

			if (depth == 0)
				return;
		}

	}

}
