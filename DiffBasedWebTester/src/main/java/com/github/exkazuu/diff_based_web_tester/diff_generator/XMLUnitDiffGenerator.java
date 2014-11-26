package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Generate diff using XMLUnit library {@see http://xmlunit.sourceforge.net/}
 */
public class XMLUnitDiffGenerator extends HtmlDiffGenerator {
  public static String nodeToText(Node node) {
    StringWriter writer = new StringWriter();
    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.METHOD, "html");
      transformer.transform(new DOMSource(node), new StreamResult(writer));
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return writer.toString().replace(" xmlns=\"http://www.w3.org/1999/xhtml\"", "");
  }

  @Override
  public String generateDiffContent(String input1, String input2, String lineSeparator) {
    StringBuilder builder = new StringBuilder();
    try {
      DetailedDiff myDiff = new DetailedDiff(new Diff(input2, input1));
      List<Difference> allDifferences = myDiff.getAllDifferences();
      for (Difference difference : allDifferences) {
        String str = difference.toString();
        String desc = difference.getDescription();
        int id = difference.getId();
        builder.append(difference.getDescription()).append("[A=")
            .append(difference.getControlNodeDetail().getValue()).append(",B=")
            .append(difference.getTestNodeDetail().getValue()).append("]")
            .append(System.lineSeparator());

        Node node1 = difference.getControlNodeDetail().getNode();
        Node node2 = difference.getTestNodeDetail().getNode();
        switch (difference.getDescription()) {
          case "attribute value":
            builder.append("before:").append(System.lineSeparator());
            if (node1 != null) {
              builder.append(node1).append(System.lineSeparator());
            }
            builder.append("after:").append(System.lineSeparator());
            if (node2 != null) {
              builder.append(node2).append(System.lineSeparator());
            }
            break;
          case "presence of child node":
            builder.append("before:").append(System.lineSeparator());
            if (node1 != null) {
              builder.append(nodeToText(node1)).append(System.lineSeparator());
            }
            builder.append("after:").append(System.lineSeparator());
            if (node2 != null) {
              builder.append(nodeToText(node2)).append(System.lineSeparator());
            }
            break;
        }
      }
    } catch (IOException | SAXException e) {
      System.err.println("XMLUnit diff error");
      e.printStackTrace();
    }
    return builder.toString();
  }
}
