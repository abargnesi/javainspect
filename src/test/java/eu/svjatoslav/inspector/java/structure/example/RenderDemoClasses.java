/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.structure.example;

import eu.svjatoslav.inspector.java.structure.ClassGraph;
import eu.svjatoslav.inspector.java.structure.example.structure.SampleClass;
import eu.svjatoslav.inspector.java.structure.example.structure.SampleClass2;

public class RenderDemoClasses {

	public static void main(final String[] args) {

		new ClassGraph().add(SampleClass.class, SampleClass2.class)
				.generateGraph("example", false);
	}

}
