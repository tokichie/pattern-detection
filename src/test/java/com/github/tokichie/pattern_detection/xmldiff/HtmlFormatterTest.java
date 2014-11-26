package com.github.tokichie.pattern_detection.xmldiff;

import org.junit.Test;

/**
 * Created by tokitake on 2014/11/26.
 */
public class HtmlFormatterTest {
    @Test
    public void testHtmlFormatter() throws Exception {
        HtmlFormatter formatter = new HtmlFormatter();
        String input = "<TAG1><TAG2></TAG2><TAG1>";
        String result = formatter.format(input);

        String expectedHeadTag = "<HEAD xmlns=\"http://www.w3.org/1999/xhtml\"/>";

        org.junit.Assert.assertTrue(result.indexOf(expectedHeadTag) != -1);
    }
}
