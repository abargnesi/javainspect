package eu.svjatoslav.inspector.tokenizer;

public class Terminator {

	String startSequence;
	String endSequence;
	boolean ignoreTerminator;

	public Terminator(final String startPattern, final boolean ignoreTerminator) {
		this.startSequence = startPattern;
		this.ignoreTerminator = ignoreTerminator;
	}

	public Terminator(final String startSequence, final String endSequence,
			final boolean ignoreTerminator) {

		this.startSequence = startSequence;
		this.endSequence = endSequence;
		this.ignoreTerminator = ignoreTerminator;
	}

}
