package shared;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import shared.InvalidMapException;

/**
 * Provides some basic methods for parsing xml nodes
 * @author Barney
 */
public class XMLParsing {
	/**
	 * Gets string value of child node
	 * @param node The parent node
	 * @param childNodeName The tag name of the child node containing the string value
	 */
	public static String parseChildNodeString(Node node, String childNodeName) throws InvalidMapException {
		// Get value
		Node childNode = findChild(node, childNodeName);
		
		if (childNode == null)
			throw new InvalidMapException(node.getNodeName() + "node did not have a value.");

		String valueAsString = childNode.getTextContent();
		
		return valueAsString;
	}

	/**
	 * Gets integer value of child node
	 * @param node The parent node
	 * @param childNodeName The tag name of the child node containing the int value
	 */
	public static int parseChildNodeInt(Node node, String childNodeName) throws InvalidMapException {
		String valueAsString = parseChildNodeString(node, childNodeName);

		int value;
		try {
			// Convert value to an int type
			value = Integer.parseInt(valueAsString);
		} catch (NumberFormatException e) {
			throw new InvalidMapException(node.getNodeName() + " node " + childNodeName + " value was not an int.");
		}
		return value;
	}

	/**
	 * Finds the child node of a node with the specified tagname
	 * @param node The node to find the child of
	 * @param tagName The tag name of the child node
	 */
	public static Node findChild(Node node, String tagName) {
		// Get children
		NodeList children = node.getChildNodes();
		
		// Iterate through until we find the element
		int i = 0;
		for (; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(tagName))
				break;
		}

		// Returns null if not a valid index
		return children.item(i);
	}
}
