/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.structure.example;

import java.io.FileNotFoundException;

import eu.svjatoslav.inspector.java.structure.ClassGraph;
import eu.svjatoslav.inspector.java.structure.Utils;

public class RenderJavaInspect {

	private static void fullProjectExample() {
		final ClassGraph graph = new ClassGraph();

		// Recursively scan current directory for Java source code and attempt
		// to detect class names from there to be added to the graph.
		graph.addProject(".");

		// Blacklist example classes from being shown on the graph
		graph.getFilter().blacklistClassPattern(
				"eu.svjatoslav.inspector.java.structure.example.*");

		// do not show single classes with no relationships on the graph
		graph.hideOrphanedClasses();

		// Produce bitmap image titled "JavaInspect full project.png" to the
		// user Desktop directory.
		graph.generateGraph("JavaInspect full project");
	}

	private static void handpickClassesExample() {
		/*
		 * This example demonstrates generating of class graph from hand picked
		 * classes.
		 */

		// Create graph
		final ClassGraph graph = new ClassGraph();

		// While classes and objects can be immediately passed to ClassGraph
		// constructor as arguments, it is also possible to add then one by one
		// as in the following example.

		// Add some object to the graph.
		graph.addObject(graph);

		// Add some class to the graph.
		graph.addClass(Utils.class);

		// Produce bitmap image titled "JavaInspect.png" to the user Desktop
		// directory and keep intermediary GraphViz DOT file for reference.
		graph.generateGraph("JavaInspect", true);
	}

	public static void main(final String[] args) throws FileNotFoundException {

		handpickClassesExample();

		fullProjectExample();

	}
}
