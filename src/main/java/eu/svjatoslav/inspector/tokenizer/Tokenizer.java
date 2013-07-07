package eu.svjatoslav.inspector.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Tokenizer {

	private final List<Terminator> terminators = new ArrayList<Terminator>();
	private final String source;

	Stack<Integer> tokenIndexes = new Stack<Integer>();

	private int currentIndex = 0;

	public Tokenizer(final String source) {
		this.source = source;
	}

	public void addTerminator(final String terminator, final boolean empty) {

		terminators.add(new Terminator(terminator, empty));
	}

	public void expectToken(final String value) throws InvalidSyntaxException {
		final TokenizerMatch match = getToken();
		if (!value.equals(match.token))
			throw new InvalidSyntaxException("Expected \"" + value
					+ "\" but got \"" + match.token + "\" instead.");
	}

	public TokenizerMatch getToken() {
		tokenIndexes.push(currentIndex);
		final StringBuffer result = new StringBuffer();

		while (true) {
			if (currentIndex >= source.length())
				return null;

			boolean accumulateCurrentChar = true;

			findTerminator: for (final Terminator terminator : terminators)
				if (terminatorMatches(terminator))
					// empty space detected
					if (terminator.empty) {
						currentIndex += terminator.value.length();
						if (result.length() > 0)
							return new TokenizerMatch(result.toString(),
									terminator);
						else {
							accumulateCurrentChar = false;
							break findTerminator;
						}
					} else if (result.length() > 0)
						return new TokenizerMatch(result.toString(), terminator);
					else {
						currentIndex += terminator.value.length();
						return new TokenizerMatch(terminator.value, terminator);
					}

			if (accumulateCurrentChar) {
				result.append(source.charAt(currentIndex));
				currentIndex++;
			}
		}

	}

	public boolean isNextToken(final String token) {
		if (token.equals(getToken().token))
			return true;

		rollbackToken();
		return false;
	}

	public void rollbackToken() {
		currentIndex = tokenIndexes.pop();
	}

	public void skipUtilEnd() {
		tokenIndexes.push(currentIndex);
		currentIndex = source.length();
	}

	public boolean terminatorMatches(final Terminator terminator) {
		if ((currentIndex + terminator.value.length()) > source.length())
			return false;

		for (int i = 0; i < terminator.value.length(); i++)
			if (terminator.value.charAt(i) != source.charAt(i + currentIndex))
				return false;

		return true;
	}

}
