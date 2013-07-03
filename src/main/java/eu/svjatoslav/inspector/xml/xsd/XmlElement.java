package eu.svjatoslav.inspector.xml.xsd;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlElement {

	Element element;

	public XmlElement(final Element element) {
		this.element = element;
	}

	public List<String> getAttributeNames() {
		final ArrayList<String> result = new ArrayList<String>();

		final NamedNodeMap attributes = element.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			final Node node = attributes.item(i);

			result.add(node.getNodeName());
		}

		return result;
	}

	public String getAttributeValue(final String attributeName) {
		return element.getAttribute(attributeName);
	}

	@Override
	public String toString() {
		final StringBuffer result = new StringBuffer();

		result.append("node name: " + element.getNodeName() + "\n");

		final NamedNodeMap attributes = element.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			final Node node = attributes.item(i);

			result.append("    " + node.getNodeName() + "\n");
		}

		return result.toString();
	}

}
