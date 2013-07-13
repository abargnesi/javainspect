/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
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

	/**
	 * Maps class fully qualified names to class descriptors.
	 */
	Map<String, ClassDescriptor> nameToClassMap = new HashMap<String, ClassDescriptor>();

	public Filter filter = new Filter();

	public ClassGraph() {
	}

	public ClassGraph(final Class<? extends Object> clazz) {
		addClass(clazz);
	}

	public ClassGraph(final Object root) {
		addClass(root.getClass());
	}

	public ClassDescriptor addClass(final Class<? extends Object> clazz) {

		if (clazz == null)
			return null;

		final String className = clazz.getName();

		if (nameToClassMap.containsKey(className))
			return nameToClassMap.get(className);

		return new ClassDescriptor(clazz, this);
	}

	public ClassDescriptor addObject(final Object object) {
		return addClass(object.getClass());
	}

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

	public void generateGraph(final String graphName) {
		generateGraph(graphName, false);
	}

	public void generateGraph(final String graphName, final boolean keepDotFile) {

		final String desktopPath = CommonPathResolver.getDesktopDirectory()
				.getAbsolutePath() + "/";

		final String dotFilePath = desktopPath + graphName + ".dot";
		final String imageFilePath = desktopPath + graphName + ".png";

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

		for (final Map.Entry<String, ClassDescriptor> entry : nameToClassMap
				.entrySet())
			result.append(entry.getValue().getDot());

		result.append("}\n");

		final String resultStr = result.toString();
		return resultStr;
	}

}
