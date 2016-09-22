package eu.svjatoslav.inspector.java.methods;

import eu.svjatoslav.inspector.java.structure.ClassGraph;

import java.io.File;

import static java.lang.System.getProperty;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("usage: javainspect [PROJECT_DIR] [PACKAGE_GLOB] [GRAPH_NAME]");
			System.exit(1);
		}

		String projectDir  = args[0];
		String packageGlob = args[1];
		String graphName   = args[2];

		ClassGraph cg = new ClassGraph();
		cg.setTargetDirectory(getProperty("user.dir") + File.separator);

		cg.addProject(projectDir);
		cg.whitelistClassPattern(packageGlob);
		cg.setKeepDotFile(true);
		cg.generateGraph(graphName);

		System.exit(0);
	}
}
