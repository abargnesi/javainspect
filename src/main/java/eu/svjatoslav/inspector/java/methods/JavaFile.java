package eu.svjatoslav.inspector.java.methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.inspector.tokenizer.InvalidSyntaxException;
import eu.svjatoslav.inspector.tokenizer.Tokenizer;
import eu.svjatoslav.inspector.tokenizer.TokenizerMatch;

public class JavaFile {

	private final List<Import> imports = new ArrayList<Import>();

	private String packageName;

	private final File file;

	StringBuffer contents = new StringBuffer();

	public JavaFile(final File file) throws IOException, InvalidSyntaxException {
		this.file = file;
		parse();
	}

	public void parse() throws IOException, InvalidSyntaxException {
		System.out.println("java file: " + file);

		readFile();

		final Tokenizer tokenizer = new Tokenizer(contents.toString());
		tokenizer.addTerminator(" ", true);
		tokenizer.addTerminator("\t", true);
		tokenizer.addTerminator("\n", true);

		tokenizer.addTerminator(";", false);
		tokenizer.addTerminator("{", false);
		tokenizer.addTerminator("}", false);
		tokenizer.addTerminator("(", false);
		tokenizer.addTerminator(")", false);
		tokenizer.addTerminator("[", false);
		tokenizer.addTerminator("]", false);
		tokenizer.addTerminator("<", false);
		tokenizer.addTerminator(">", false);
		tokenizer.addTerminator(",", false);

		final Modifiers modifiers = new Modifiers();

		while (true) {
			final TokenizerMatch match = tokenizer.getToken();
			if (match == null)
				break;

			if (match.token.equals("package")) {
				parsePackage(tokenizer);
				continue;
			}

			if (match.token.equals("import")) {
				parseImport(tokenizer);
				continue;
			}

			final boolean wasModifier = modifiers.parseModifier(match.token);
			if (wasModifier)
				continue;

			if ("class".equals(match.token)) {
				parseClass(tokenizer);
				continue;
			}

			System.out.println("    " + modifiers.toString() + " "
					+ match.token);
			modifiers.reset();
			skipUntilSemicolon(tokenizer);
		}

	}

	private void parseClass(final Tokenizer tokenizer)
			throws InvalidSyntaxException {

		final TokenizerMatch match = tokenizer.getToken();
		final Clazz clazz = new Clazz(packageName, match.token, tokenizer);
		System.out.println(clazz.toString());

	}

	private void parseImport(final Tokenizer tokenizer)
			throws InvalidSyntaxException {

		final Import imp = new Import();

		final TokenizerMatch match = tokenizer.getToken();

		if (match.token.equals("static")) {
			imp.isStatic = true;
			imp.path = tokenizer.getToken().token;
		} else
			imp.path = match.token;

		imports.add(imp);

		tokenizer.expectToken(";");
	}

	private void parsePackage(final Tokenizer tokenizer)
			throws InvalidSyntaxException {

		final TokenizerMatch match = tokenizer.getToken();

		packageName = match.token;

		tokenizer.expectToken(";");
	}

	private void readFile() throws FileNotFoundException, IOException {
		final FileReader fileReader = new FileReader(file);

		final BufferedReader bufferedReader = new BufferedReader(fileReader);

		while (true) {
			final String line = bufferedReader.readLine();

			if (line == null)
				break;

			contents.append(line);
			contents.append("\n");
		}

		bufferedReader.close();
		fileReader.close();
	}

	public void skipUntilSemicolon(final Tokenizer tokenizer) {
		while (true) {
			final TokenizerMatch token = tokenizer.getToken();
			if (token.token.equals(";"))
				return;
		}
	}

}
