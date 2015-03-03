/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.structure;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.svjatoslav.commons.file.CommonPathResolver;
import eu.svjatoslav.commons.string.WildCardMatcher;
import eu.svjatoslav.inspector.java.methods.Clazz;
import eu.svjatoslav.inspector.java.methods.ProjectScanner;

public class ClassGraph {

	/**
	 * Maps class fully qualified names to class descriptors.
	 */
	private final Map<String, ClassDescriptor> fullyQualifiedNameToClassMap = new HashMap<String, ClassDescriptor>();

	private final List<String> blacklistClassPatterns = new ArrayList<String>();

	private final List<String> whitelistClassPatterns = new ArrayList<String>();

	private String targetDirectory = CommonPathResolver.getDesktopDirectory()
			.getAbsolutePath() + "/";

	private boolean keepDotFile;

	public ClassGraph() {
	}

	/**
	 * @param objects
	 *            objects that shall be added to graph
	 */
	public ClassGraph add(final Object... objects) {

		if (objects != null)
			for (final Object object : objects)
				addObject(object);

		return this;
	}

	private void addObject(final Object object) {
		if (object instanceof Class)
			getOrCreateClassDescriptor((Class) object);
		else
			getOrCreateClassDescriptor(object.getClass());
	}

	/**
	 * @param path
	 *            path to recursively scan for java source code could be
	 *            relative to current project or absolute
	 */
	public void addProject(final String path) {
		final ProjectScanner projectScanner = new ProjectScanner(new File(path));
		for (final Clazz clazz : projectScanner.getAllClasses())
			try {
				System.out.println("Class full name: " + clazz.getFullName());
				final Class c = this.getClass().forName(clazz.getFullName());
				addObject(c);
			} catch (final Exception exception) {
				System.out.println("cannot add class: "
						+ exception.getMessage());
			}
	}

	public void blacklistClassPattern(final String pattern) {
		blacklistClassPatterns.add(pattern);
	}

	/**
	 * @param targetDirectory
	 *            target directory name
	 *
	 * @param resultFileName
	 *            file name for the generated graph. File extension will be
	 *            added automatically. Existing file with the same name will be
	 *            overwritten.
	 *
	 * @param keepDotFile
	 *            if set to <code>true</code> then intermediary GraphViz DOT
	 *            file will be kept.
	 */

	public void generateGraph(final String resultFileName) {

		final String dotFilePath = targetDirectory + resultFileName + ".dot";
		final String imageFilePath = targetDirectory + resultFileName + ".png";

		System.out.println("Dot file path:" + dotFilePath);

		try {
			// write DOT file to disk
			final PrintWriter out = new PrintWriter(dotFilePath);
			out.write(getDot());
			out.close();

			// execute GraphViz to visualize graph
			try {
				Runtime.getRuntime()
				.exec(new String[] { "dot", "-Tpng", dotFilePath, "-o",
						imageFilePath }).waitFor();
			} catch (final InterruptedException e) {
			} finally {
			}

			if (!keepDotFile) {
				// delete dot file
				new File(dotFilePath).delete();
			}
		} catch (final IOException e) {
			System.err.println(e);
		}

	}

	private String getDot() {
		final StringBuffer result = new StringBuffer();

		result.append("digraph Java {\n");
		result.append("graph [rankdir=LR, overlap = false, concentrate=true];\n");

		for (final Map.Entry<String, ClassDescriptor> entry : fullyQualifiedNameToClassMap
				.entrySet())
			result.append(entry.getValue().getDot());

		result.append("}\n");

		final String resultStr = result.toString();
		return resultStr;
	}

	/**
	 * @param clazz
	 *            class that shall be added to graph
	 */
	protected ClassDescriptor getOrCreateClassDescriptor(final Class clazz) {

		if (clazz == null)
			return null;

		final String classFullyQualifiedName = clazz.getName();

		// reuse existing instance if possible
		if (fullyQualifiedNameToClassMap.containsKey(classFullyQualifiedName))
			return fullyQualifiedNameToClassMap.get(classFullyQualifiedName);

		// create new class descriptor
		final ClassDescriptor newClassDescriptor = new ClassDescriptor(this);
		fullyQualifiedNameToClassMap.put(classFullyQualifiedName,
				newClassDescriptor);

		newClassDescriptor.analyzeClass(clazz);

		return newClassDescriptor;
	}

	/**
	 * Hide orphaned class that have no references
	 */
	public void hideOrphanedClasses() {

		for (final ClassDescriptor classDescriptor : fullyQualifiedNameToClassMap
				.values())
			classDescriptor.hideClassIfNoReferences();

	}

	protected boolean isClassShown(final String className) {
		for (final String pattern : blacklistClassPatterns)
			if (WildCardMatcher.match(className, pattern))
				return false;

		if (!whitelistClassPatterns.isEmpty()) {
			for (final String pattern : whitelistClassPatterns)
				if (WildCardMatcher.match(className, pattern))
					return true;
			return false;
		}

		return true;
	}

	public ClassGraph setKeepDotFile(final boolean keepDotFile) {
		this.keepDotFile = keepDotFile;

		return this;
	}

	public ClassGraph setTargetDirectory(String directoryPath) {
		if (!directoryPath.endsWith("/"))
			directoryPath += "/";

		targetDirectory = directoryPath;

		return this;
	}

	public ClassGraph whitelistClassPattern(final String pattern) {
		whitelistClassPatterns.add(pattern);

		return this;
	}

}
