package edu.harvard.i2b2.query.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.xml.bind.StringInputStream;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;

public class DataUtils implements DataConst {
	public static final XMLOutputter XML_PRETTY_PRINTER = new XMLOutputter(
			Format.getPrettyFormat());

	private static Document DRAG_AND_DROP_XML_DOC_SHELL = null;

	/*
	 * This utility method converts an
	 * ItemType.ConstrainByModifer.ConstrainByValue into an
	 * ItemType.ConstrainByValue The parameter 'stupidValue' is stupid because
	 * ItemType.ConstrainByModifer.ConstrainByValue should have never existed.
	 * It should just use ItemType.ConstrainByValue, and this method would not
	 * need to exist. This serves as a reminder that the XSD for ItemType needs
	 * to be refactored.
	 */
	public static ItemType.ConstrainByValue toConstrainByValue(
			ItemType.ConstrainByModifier.ConstrainByValue stupidValue) {
		ItemType.ConstrainByValue val = new ConstrainByValue();
		val.setValueConstraint(stupidValue.getValueConstraint());
		val.setValueOperator(stupidValue.getValueOperator());
		val.setValueType(stupidValue.getValueType());
		val.setValueUnitOfMeasure(stupidValue.getValueUnitOfMeasure());
		return val;
	}

	public static String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for (int i = 0; i < 20; i++) {
			int num = getValidAcsiiValue();
			strWriter.append((char) num);
		}
		return strWriter.toString();
	}

	public static int getValidAcsiiValue() {
		int number = 48;
		while (true) {
			number = 48 + (int) Math.round(Math.random() * 74);
			if ((number > 47 && number < 58) || (number > 64 && number < 91)
					|| (number > 96 && number < 123))
				break;
		}
		return number;
	}

	/*
	 * For drag and drop operations where a new DND XML doc is necessary. This
	 * method returns an XML doc that has namespaces declared, but otherwise
	 * empty.
	 */
	public static Document makeEmptyDNDXMLDocument() {
		if (DRAG_AND_DROP_XML_DOC_SHELL == null) {
			DRAG_AND_DROP_XML_DOC_SHELL = new Document();
			Element dndElement = new Element(PLUGIN_DRAG_DROP,
					Namespace.getNamespace(DND_PREFIX, DND_NAMESPACE));
			DRAG_AND_DROP_XML_DOC_SHELL.addContent(dndElement);
		}
		return (Document) DRAG_AND_DROP_XML_DOC_SHELL.clone();
	}

	/* make a Filter for XML tag */
	public static Filter makeTagFilter(final String tagName) {
		return new Filter() {
			/* generated serialVersionUID */
			private static final long serialVersionUID = -1966352415673017366L;

			@Override
			public boolean matches(Object xmlContent) {
				if (xmlContent instanceof Element
						&& ((Element) xmlContent).getName().equals(tagName))
					return true;
				return false;
			}
		};
	}

	/* Pretty Print methods for JDOM Documents and Elements */
	public static void prettyPrintXMLDoc(Document doc, OutputStream outStream)
			throws IOException {
		XML_PRETTY_PRINTER.output(doc, outStream);
	}

	public static void prettyPrintXMLElement(Element element,
			OutputStream outStream) throws IOException {
		XML_PRETTY_PRINTER.output(element, outStream);
	}

	public static void prettyPrintXMLDoc(Document doc, Writer writer)
			throws IOException {
		XML_PRETTY_PRINTER.output(doc, writer);
	}

	public static void prettyPrintXMLElement(Element element, Writer writer)
			throws IOException {
		XML_PRETTY_PRINTER.output(element, writer);
	}

	public static void prettyPrintXMLDoc(String xmlString, OutputStream outStream)
	{
		try 
		{
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new StringInputStream( xmlString ) );
			DataUtils.prettyPrintXMLDoc(doc, outStream);
		} 
		catch (JDOMException e) 		{ e.printStackTrace(); } 
		catch (NullPointerException e) 	{ e.printStackTrace(); } 
		catch (IOException e) 			{ e.printStackTrace(); }
	}

	public static void main(String[] args) {
		Document doc = DataUtils.makeEmptyDNDXMLDocument();
		System.err.println(doc.getContent());

		StringWriter strWriter = new StringWriter();
		try {
			DataUtils.prettyPrintXMLDoc(doc, strWriter);
			System.err.println(strWriter.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
