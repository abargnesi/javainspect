package eu.svjatoslav.inspector.xml.xsd;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {

	public static void main(final String[] args) throws SAXException,
			IOException, ParserConfigurationException {

		final XSD xsd = new XSD();

		xsd.parse(new FileInputStream("/home/n0/Desktop/MeterSchema.xsd"));

		System.out.println(xsd);

	}

}
