package eu.svjatoslav.inspector.xml.xsd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XSD {

	private static final String XMLNS_PREFIX = "xmlns:";
	Map<String, String> namespaces = new HashMap<String, String>();

	private void detectNamespaces(final XmlElement xsdSchema) {
		for (final String attributeName : xsdSchema.getAttributeNames())

			if (attributeName.startsWith(XMLNS_PREFIX)) {
				final String nameSpaceName = attributeName
						.substring(XMLNS_PREFIX.length());
				namespaces.put(nameSpaceName,
						xsdSchema.getAttributeValue(attributeName));
			}
	}

	public void parse(final InputStream inputStream) {

		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();

		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}

		Document document;
		try {
			document = builder.parse(inputStream);
		} catch (final SAXException e) {
			e.printStackTrace();
			return;
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		final XmlElement xsdSchema = new XmlElement(
				document.getDocumentElement());

		detectNamespaces(xsdSchema);

		System.out.println(xsdSchema.toString());
	}

	public void parse(final String filePath) throws FileNotFoundException {

		final FileInputStream inputStream = new FileInputStream(filePath);

		parse(inputStream);
	}

	@Override
	public String toString() {
		final StringBuffer result = new StringBuffer();

		result.append("namespaces:\n");

		for (final Map.Entry<String, String> entry : namespaces.entrySet())
			result.append("    " + entry.getKey() + " = " + entry.getValue()
					+ "\n");

		return result.toString();
	}

}
