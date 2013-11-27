package eu.svjatoslav.inspector.java.structure.example;

import eu.svjatoslav.inspector.java.structure.ClassGraph;
import eu.svjatoslav.inspector.java.structure.example.structure.SampleClass;

public class RenderDemoClasses {

	public static void main(final String[] args) {
		final ClassGraph graph = new ClassGraph();

		graph.addClass(SampleClass.class);

		graph.generateGraph("example");
	}

}
