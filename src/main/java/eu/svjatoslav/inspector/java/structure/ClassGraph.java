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
import java.util.HashMap;
import java.util.Map;

import eu.svjatoslav.commons.file.CommonPathResolver;
import eu.svjatoslav.inspector.java.methods.Clazz;
import eu.svjatoslav.inspector.java.methods.ProjectScanner;

public class ClassGraph {

	public static void render(final String graphName, final Class... classes) {
		final ClassGraph classGraph = new ClassGraph(classes);

		classGraph.generateGraph(graphName);
	}

	/**
	 * Maps class fully qualified names to class descriptors.
	 */
	private final Map<String, ClassDescriptor> fullyQualifiedNameToClassMap = new HashMap<String, ClassDescriptor>();

	private Filter filter = new Filter();

	public ClassGraph() {
	}

	/**
	 * @param classes
	 *            classes that shall be added to graph
	 */
	public ClassGraph(final Class<? extends Object>... classes) {
		for (final Class<? extends Object> clazz : classes)
			addClass(clazz);
	}

	/**
	 * @param objects
	 *            objects that shall be added to graph
	 */
	public ClassGraph(final Object... objects) {
		for (final Object object : objects)
			addClass(object.getClass());
	}

	/**
	 * @param clazz
	 *            class that shall be added to graph
	 */
	public ClassDescriptor addClass(final Class<? extends Object> clazz) {

		if (clazz == null)
			return null;

		final String className = clazz.getName();

		if (fullyQualifiedNameToClassMap.containsKey(className))
			return fullyQualifiedNameToClassMap.get(className);

		return new ClassDescriptor(clazz, this);
	}

	/**
	 * @param object
	 *            object that shall be added to graph
	 */
	public ClassDescriptor addObject(final Object object) {
		return addClass(object.getClass());
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
				addClass(c);
			} catch (final Exception exception) {
				System.out.println("cannot add class: "
						+ exception.getMessage());
			}
	}

	/**
	 * @param resultFileName
	 *            file name for the generated graph. Existing file with the same
	 *            name will be overwritten.
	 */
	public void generateGraph(final String resultFileName) {
		generateGraph(resultFileName, false);
	}

	/**
	 * @param resultFileName
	 *            file name for the generated graph. File extension will be
	 *            added automatically. Existing file with the same name will be
	 *            overwritten.
	 *
	 * @param keepDotFile
	 *            if set to <code>true</code> then intermediary GraphViz DOT
	 *            file will be kept.
	 */

	public void generateGraph(final String resultFileName,
			final boolean keepDotFile) {

		final String desktopPath = CommonPathResolver.getDesktopDirectory()
				.getAbsolutePath() + "/";

		generateGraph(desktopPath, resultFileName, keepDotFile);
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

	public void generateGraph(String targetDirectory,
			final String resultFileName, final boolean keepDotFile) {

		if (!targetDirectory.endsWith("/"))
			targetDirectory += "/";

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
				final File dotFile = new File(dotFilePath);
				dotFile.delete();
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

	public Filter getFilter() {
		return filter;
	}

	/**
	 * Hide orphaned class that have no references
	 */
	public void hideOrphanedClasses() {

		for (final ClassDescriptor classDescriptor : fullyQualifiedNameToClassMap
				.values())
			classDescriptor.hideClassIfNoReferences();

	}

	public void registerClass(final String classFullyQualifiedName,
			final ClassDescriptor classDescriptor) {
		fullyQualifiedNameToClassMap.put(classFullyQualifiedName,
				classDescriptor);
	}

	public void setFilter(final Filter filter) {
		this.filter = filter;
	}

}
