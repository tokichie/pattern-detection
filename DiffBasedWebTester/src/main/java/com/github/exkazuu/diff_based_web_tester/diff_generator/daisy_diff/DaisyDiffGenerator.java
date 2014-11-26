package com.github.exkazuu.diff_based_web_tester.diff_generator.daisy_diff;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.outerj.daisy.diff.DaisyDiff;
import org.outerj.daisy.diff.XslFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.github.exkazuu.diff_based_web_tester.diff_generator.HtmlDiffGenerator;


public class DaisyDiffGenerator extends HtmlDiffGenerator {
  private static final String TMP_OUT_FILE_PATH = "tmp_out.txt";
  private static final String TAG_ADDED = "diff-tag-added";
  private static final String TAG_REMOVED = "diff-tag-removed";
  private static final String TAG_CHANGED = "diff-html-changed";


  @Override
  public String generateDiffContent(String input1, String input2) {
    return generateDiffContent(input1, input2, "\n");
  }

  @Override
  public String generateDiffContent(String input1, String input2, String lineSeparator) {
    boolean htmlDiff = true;
    boolean htmlOut = false;
    String[] css = new String[] {};
    File outFile = new File(TMP_OUT_FILE_PATH);
    InputStream oldStream = new ByteArrayInputStream(input1.getBytes());
    InputStream newStream = new ByteArrayInputStream(input2.getBytes());

    try {
      SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

      TransformerHandler result = tf.newTransformerHandler();
      result.setResult(new StreamResult(outFile));


      XslFilter filter = new XslFilter();

      ContentHandler postProcess = result;
      postProcess.startDocument();
      postProcess.startElement("", "diffreport", "diffreport", new AttributesImpl());
      postProcess.startElement("", "diff", "diff", new AttributesImpl());

      InputStreamReader oldReader = null;
      BufferedReader oldBuffer = null;

      InputStreamReader newISReader = null;
      BufferedReader newBuffer = null;
      try {
        oldReader = new InputStreamReader(oldStream);
        oldBuffer = new BufferedReader(oldReader);

        newISReader = new InputStreamReader(newStream);
        newBuffer = new BufferedReader(newISReader);
        DaisyDiff.diffTag(oldBuffer, newBuffer, postProcess);

      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        oldBuffer.close();
        newBuffer.close();
        oldReader.close();
        newISReader.close();
      }

      postProcess.endElement("", "diff", "diff");
      postProcess.endElement("", "diffreport", "diffreport");
      postProcess.endDocument();
    } catch (Throwable e) {
      e.printStackTrace();
      if (e.getCause() != null) {
        e.getCause().printStackTrace();
      }
      if (e instanceof SAXException) {
        ((SAXException) e).getException().printStackTrace();
      }
    } finally {
      try {
        if (oldStream != null)
          oldStream.close();
      } catch (IOException e) {
        // ignore this exception
      }
      try {
        if (newStream != null)
          newStream.close();
      } catch (IOException e) {
        // ignore this exception
      }
    }
    try {
      String out = extractNecessaryStringFromRawOutput(outFile);
      outFile.delete();
      return out;
    } catch (IOException e) {
      System.err.println("Error processing output from daisydiff");
      e.printStackTrace();
      return "";
    }
  }

  private String extractNecessaryStringFromRawOutput(File outFile) throws IOException {
    Document doc = Jsoup.parse(outFile, "UTF-8", "http://example.com/");
    StringBuilder builder = new StringBuilder();
    List<Element> focusElements = new ArrayList<>();
    addAll(doc.getElementsByAttributeValue("class", TAG_ADDED), focusElements);
    addAll(doc.getElementsByAttributeValue("class", TAG_REMOVED), focusElements);
    addAll(doc.getElementsByAttributeValue("class", TAG_CHANGED), focusElements);
    for (Element focusElement : focusElements) {
      String operationName;
      if (focusElement.hasClass(TAG_ADDED)) {
        operationName = "Additional";
      } else if (focusElement.hasClass(TAG_REMOVED)) {
        operationName = "Removed";
      } else {
        operationName = "Changed";
      }
      builder.append(operationName + ":" + childrenToString(focusElement)).append(
          System.lineSeparator());
    }
    return builder.toString();
  }

  private void addAll(Elements elements, List<Element> target) {
    for (int i = 0; i < elements.size(); i++) {
      target.add(elements.get(i));
    }
  }

  private String childrenToString(Element element) {
    StringBuilder builder = new StringBuilder();
    for (Node child : element.childNodes()) {
      builder.append(child);
    }
    return builder.toString();
  }

  private static void doCSS(String[] css, ContentHandler handler) throws SAXException {
    handler.startElement("", "css", "css", new AttributesImpl());
    for (String cssLink : css) {
      AttributesImpl attr = new AttributesImpl();
      attr.addAttribute("", "href", "href", "CDATA", cssLink);
      attr.addAttribute("", "type", "type", "CDATA", "text/css");
      attr.addAttribute("", "rel", "rel", "CDATA", "stylesheet");
      handler.startElement("", "link", "link", attr);
      handler.endElement("", "link", "link");
    }

    handler.endElement("", "css", "css");

  }
}
