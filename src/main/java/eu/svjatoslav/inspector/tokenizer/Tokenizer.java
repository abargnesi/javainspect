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

	public void addTerminator(final String startSequence,
			final boolean ignoreTerminator) {
		terminators.add(new Terminator(startSequence, ignoreTerminator));
	}

	public void addTerminator(final String startSequence,
			final String endSequence, final boolean ignoreTerminator) {
		terminators.add(new Terminator(startSequence, endSequence,
				ignoreTerminator));
	}

	public void expectNextToken(final String value)
			throws InvalidSyntaxException {
		final TokenizerMatch match = getNextToken();
		if (!value.equals(match.token))
			throw new InvalidSyntaxException("Expected \"" + value
					+ "\" but got \"" + match.token + "\" instead.");
	}

	public TokenizerMatch getNextToken() {
		tokenIndexes.push(currentIndex);
		final StringBuffer result = new StringBuffer();

		while (true) {
			if (currentIndex >= source.length())
				return null;

			boolean accumulateCurrentChar = true;

			findTerminator: for (final Terminator terminator : terminators)
				if (sequenceMatches(terminator.startSequence))

					if (terminator.ignoreTerminator) {
						currentIndex += terminator.startSequence.length();

						if (terminator.endSequence != null)
							skipUntilSequence(terminator.endSequence);

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
						currentIndex += terminator.startSequence.length();
						return new TokenizerMatch(terminator.startSequence,
								terminator);
					}

			if (accumulateCurrentChar) {
				result.append(source.charAt(currentIndex));
				currentIndex++;
			}
		}

	}

	public boolean probeNextToken(final String token) {
		if (token.equals(getNextToken().token))
			return true;

		unreadToken();
		return false;
	}

	public boolean sequenceMatches(final String sequence) {
		if ((currentIndex + sequence.length()) > source.length())
			return false;

		for (int i = 0; i < sequence.length(); i++)
			if (sequence.charAt(i) != source.charAt(i + currentIndex))
				return false;

		return true;
	}

	public void skipUntilDataEnd() {
		tokenIndexes.push(currentIndex);
		currentIndex = source.length();
	}

	public void skipUntilSequence(final String sequence) {
		while (currentIndex < source.length()) {
			if (sequenceMatches(sequence)) {
				currentIndex += sequence.length();
				return;
			}

			currentIndex++;
		}
	}

	public void unreadToken() {
		currentIndex = tokenIndexes.pop();
	}

}
