package eu.svjatoslav.inspector.xml.xsd;

import java.io.FileNotFoundException;

public class Main {

	public static void main(final String[] args) throws FileNotFoundException {

		final XSD xsd = new XSD();

		xsd.parse("/home/n0/Desktop/MeterSchema.xsd");

		System.out.println(xsd);

	}

}
