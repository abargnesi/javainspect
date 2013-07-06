package eu.svjatoslav.inspector.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

	private final List<Terminator> terminators = new ArrayList<Terminator>();
	private final String source;

	private int currentIndex = 0;

	public Tokenizer(final String source) {
		this.source = source;
	}

	public void addTerminator(final String terminator, final boolean empty) {

		terminators.add(new Terminator(terminator, empty));
	}

	public TokenizerMatch getToken() {
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

	public boolean terminatorMatches(final Terminator terminator) {
		if ((currentIndex + terminator.value.length()) > source.length())
			return false;

		for (int i = 0; i < terminator.value.length(); i++)
			if (terminator.value.charAt(i) != source.charAt(i + currentIndex))
				return false;

		return true;
	}

}
