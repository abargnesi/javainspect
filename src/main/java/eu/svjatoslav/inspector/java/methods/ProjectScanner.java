package eu.svjatoslav.inspector.java.methods;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.svjatoslav.commons.file.FilePathParser;
import eu.svjatoslav.inspector.tokenizer.InvalidSyntaxException;

public class ProjectScanner {

	private final File scanPath;

	Map<File, Project> projects = new HashMap<File, Project>();

	public ProjectScanner(final File projectPath) {
		scanPath = projectPath;
		parse();
	}

	public void parse() {

		if (!scanPath.exists())
			System.out.println("Path not found: " + scanPath);

		if (!scanPath.canRead())
			System.out.println("Cannot read path: " + scanPath);

		if (scanPath.isDirectory())
			parseDirectory(scanPath);

		if (scanPath.isFile())
			parseFile(scanPath);
	}

	public void parseDirectory(final File file) {

		for (final File subFile : file.listFiles()) {

			if (subFile.isFile())
				parseFile(subFile);

			if (subFile.isDirectory())
				parseDirectory(subFile);
		}
	}

	public void parseFile(final File file) {
		final String fileExtension = FilePathParser.getFileExtension(file);
		if ("java".equalsIgnoreCase(fileExtension))
			try {
				final JavaFile javaFile = new JavaFile(file);
			} catch (final IOException e) {
				System.out.println("Error parsing file: " + file.toString()
						+ ": " + e.toString());
			} catch (final InvalidSyntaxException e) {
				System.out.println("Syntax error occured while parsing file: "
						+ file.toString() + ": " + e.toString());
			}
	}

}
