package eu.svjatoslav.inspector.java.methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.commons.string.tokenizer.InvalidSyntaxException;
import eu.svjatoslav.commons.string.tokenizer.Tokenizer;
import eu.svjatoslav.commons.string.tokenizer.TokenizerMatch;

public class JavaFile {

	private final List<Import> imports = new ArrayList<Import>();

	private String packageName;

	private final File file;

	StringBuffer contents = new StringBuffer();

	public List<Clazz> classes = new ArrayList<Clazz>();

	public JavaFile(final File file) throws IOException, InvalidSyntaxException {
		this.file = file;
		parse();
	}

	public void parse() throws IOException, InvalidSyntaxException {
		System.out.println("java file: " + file);

		readFile();

		final Tokenizer tokenizer = new Tokenizer(contents.toString());

		// empty space
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
		tokenizer.addTerminator("@", false);

		// comments
		tokenizer.addTerminator("//", "\n", true);
		tokenizer.addTerminator("/*", "*/", true);

		final Modifiers modifiers = new Modifiers();

		while (true) {
			final TokenizerMatch match = tokenizer.getNextToken();
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

			if ("interface".equals(match.token)) {
				parseInterface(tokenizer);
				continue;
			}

			if ("@".equals(match.token)) {
				final Annotation annotation = new Annotation(tokenizer);
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

		final TokenizerMatch match = tokenizer.getNextToken();
		final Clazz clazz = new Clazz(packageName, match.token, tokenizer,
				false);
		// System.out.println(clazz.toString());
		classes.add(clazz);

	}

	private void parseImport(final Tokenizer tokenizer)
			throws InvalidSyntaxException {

		final Import imp = new Import();

		final TokenizerMatch match = tokenizer.getNextToken();

		if (match.token.equals("static")) {
			imp.isStatic = true;
			imp.path = tokenizer.getNextToken().token;
		} else
			imp.path = match.token;

		imports.add(imp);

		tokenizer.expectNextToken(";");
	}

	private void parseInterface(final Tokenizer tokenizer)
			throws InvalidSyntaxException {

		final TokenizerMatch match = tokenizer.getNextToken();
		final Clazz clazz = new Clazz(packageName, match.token, tokenizer, true);
		// System.out.println(clazz.toString());
		classes.add(clazz);
	}

	private void parsePackage(final Tokenizer tokenizer)
			throws InvalidSyntaxException {

		final TokenizerMatch match = tokenizer.getNextToken();

		packageName = match.token;

		tokenizer.expectNextToken(";");
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
			final TokenizerMatch token = tokenizer.getNextToken();

			if (token == null)
				return;

			if (token.token.equals(";"))
				return;
		}
	}

}
