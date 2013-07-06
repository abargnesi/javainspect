package eu.svjatoslav.inspector.java.methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.inspector.tokenizer.Tokenizer;
import eu.svjatoslav.inspector.tokenizer.TokenizerMatch;

public class JavaFile {

	private final List<Import> imports = new ArrayList<Import>();

	private final File file;

	StringBuffer contents = new StringBuffer();

	public JavaFile(final File file) throws IOException {
		this.file = file;
		parse();
	}

	public void parse() throws IOException {
		System.out.println("java file: " + file);

		readFile();

		final Tokenizer tokenizer = new Tokenizer(contents.toString());
		tokenizer.addTerminator(" ", true);
		tokenizer.addTerminator("\t", true);
		tokenizer.addTerminator("\n", true);

		tokenizer.addTerminator(";", false);

		while (true) {
			final TokenizerMatch match = tokenizer.getToken();
			if (match == null)
				break;

			if (match.token.equals("import"))
				parseImport(tokenizer);
		}

	}

	private void parseImport(final Tokenizer tokenizer) {
		final Import imp = new Import();

		final TokenizerMatch match = tokenizer.getToken();

		if (match.token.equals("static")) {
			imp.isStatic = true;
			imp.path = tokenizer.getToken().token;
		} else
			imp.path = match.token;

		imports.add(imp);
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

}
