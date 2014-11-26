package com.github.exkazuu.diff_based_web_tester.diff_generator.daisy_diff;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.outerj.daisy.diff.DaisyDiff;
import org.outerj.daisy.diff.XslFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.github.exkazuu.diff_based_web_tester.diff_generator.HtmlDiffGenerator;

public class DaisyDiffGeneratorWithTagMode extends HtmlDiffGenerator {
  @Override
  public String generateDiffContent(String input1, String input2, String lineSeparator) {
    boolean htmlOut = false;
    StringWriter writer = new StringWriter();

    try {
      SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

      TransformerHandler result = tf.newTransformerHandler();
      result.setResult(new StreamResult(writer));

      XslFilter filter = new XslFilter();

      ContentHandler postProcess =
          htmlOut ? filter.xsl(result, "org/outerj/daisy/diff/tagheader.xsl") : result;
      postProcess.startDocument();
      postProcess.startElement("", "diffreport", "diffreport", new AttributesImpl());
      postProcess.startElement("", "diff", "diff", new AttributesImpl());
      System.out.print(".");

      try (BufferedReader oldBuffer = new BufferedReader(new StringReader(input1));
          BufferedReader newBuffer = new BufferedReader(new StringReader(input2));) {

        DaisyDiff.diffTag(oldBuffer, newBuffer, postProcess);
      } catch (Exception e) {
        e.printStackTrace();
      }

      System.out.print(".");
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
    }

    return writer.toString();
  }
}
