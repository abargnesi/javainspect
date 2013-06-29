/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 */

package eu.svjatoslav.javainspect.example;

import java.io.FileNotFoundException;

import eu.svjatoslav.javainspect.structure.ClassGraph;
import eu.svjatoslav.javainspect.structure.Utils;

public class RenderJavaInspect {

	public static void main(final String[] args) throws FileNotFoundException {

		// Create graph
		final ClassGraph graph = new ClassGraph();

		// Add some object to the graph.
		graph.addObject(graph);

		// Add some class to the graph.
		graph.addClass(Utils.class);

		// Produce bitmap image titled "JavaInspect.png" to the user Desktop
		// directory.
		graph.generateGraph("JavaInspect", true);
	}
}
