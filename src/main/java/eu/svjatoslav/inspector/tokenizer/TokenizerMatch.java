package eu.svjatoslav.inspector.tokenizer;

public class TokenizerMatch {

	public String token;
	public Terminator terminator;

	public TokenizerMatch(final String token, final Terminator terminator) {
		this.token = token;
		this.terminator = terminator;
	}
}
