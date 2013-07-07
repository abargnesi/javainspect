package eu.svjatoslav.inspector.tokenizer;

public class InvalidSyntaxException extends Exception {

	private static final long serialVersionUID = 88294980027680555L;

	public InvalidSyntaxException(final String cause) {
		super(cause);
	}

}
