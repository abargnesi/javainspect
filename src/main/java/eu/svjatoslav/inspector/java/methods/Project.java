package eu.svjatoslav.inspector.java.methods;

import java.io.File;
import java.io.IOException;

import eu.svjatoslav.commons.file.FilePathParser;

public class Project {

	private final File projectPath;

	public Project(final File projectPath) {
		this.projectPath = projectPath;
		parse();
	}

	public void parse() {

		if (!projectPath.exists())
			System.out.println("Project not found on path: " + projectPath);

		if (!projectPath.canRead())
			System.out.println("Cannot read project path: " + projectPath);

		if (projectPath.isDirectory())
			parseDirectory(projectPath);

		if (projectPath.isFile())
			parseFile(projectPath);
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
						+ ", " + e.toString());
			}
	}

}
