package eu.svjatoslav.inspector.xml.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import eu.svjatoslav.commons.data.xml.XmlElement;
import eu.svjatoslav.commons.data.xml.XmlHelper;

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

	public void parse(final InputStream inputStream) throws SAXException,
			IOException, ParserConfigurationException {

		final XmlElement xsdSchema = XmlHelper.parseXml(inputStream);

		detectNamespaces(xsdSchema);

		System.out.println(xsdSchema.toString());
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
