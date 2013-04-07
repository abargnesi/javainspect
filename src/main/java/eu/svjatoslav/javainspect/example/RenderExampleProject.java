/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 */

package eu.svjatoslav.javainspect.example;

import eu.svjatoslav.javainspect.example.structure.SampleClass;
import eu.svjatoslav.javainspect.structure.Graph;

public class RenderExampleProject {

	public static void main(final String[] args) {
		final Graph graph = new Graph();

		graph.addClass(SampleClass.class);

		graph.generateGraph("example");

	}

}
