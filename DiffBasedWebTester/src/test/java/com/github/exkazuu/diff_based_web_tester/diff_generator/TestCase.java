package com.github.exkazuu.diff_based_web_tester.diff_generator;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import com.github.exkazuu.diff_based_web_tester.diff_generator.xdiff.XDiffGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.exkazuu.diff_based_web_tester.diff_generator.daisy_diff.DaisyDiffGenerator;
import com.github.exkazuu.diff_based_web_tester.diff_generator.daisy_diff.DaisyDiffGeneratorWithTagMode;


@RunWith(Parameterized.class)
public class TestCase {
  @Parameter
  public HtmlDiffGenerator generator;

  private int index;

  @Parameters
  public static Collection<HtmlDiffGenerator[]> data() {
    return Arrays.asList(new HtmlDiffGenerator[][] { {new MyersDiffGenerator()},
        {new GoogleDiffGenerator()}, {new XDiffGenerator()}, {new ThreeDMDiffGenerator()},
        {new DaisyDiffGenerator()}, {new DaisyDiffGeneratorWithTagMode()},
        {new XMLUnitDiffGenerator()}});
  }

  private void setPrefixFileName(String prefixFileName) {
    index = 0;
    LogFiles.setPrefixFileName(prefixFileName);
  }

  private String readHtml() {
    return LogFiles.readLogFile("raw" + (++index) + ".html");
  }

  @Test
  public void testGoogle() throws InterruptedException {
    System.out.println("------------- Google -------------");
    setPrefixFileName("Google");
    compareDiffAlgorithmsUsing2Htmls();
  }

  @Test
  public void testGitHub() throws InterruptedException {
    System.out.println("------------- GitHub -------------");
    setPrefixFileName("GitHub");
    compareDiffAlgorithmsUsing2Htmls();
  }

  @Test
  public void testTwitter() throws InterruptedException {
    System.out.println("------------- Twitter -------------");
    setPrefixFileName("Twitter");
    compareDiffAlgorithmsUsing2Htmls();
  }

  @Test
  public void testTodoMvc() throws InterruptedException {
    System.out.println("------------- TodoMVC -------------");
    setPrefixFileName("TodoMVC");
    compareDiffAlgorithmsUsing4Htmls(false);
  }

  @Test
  public void testTodoMvc2() throws InterruptedException {
    System.out.println("------------- TodoMVC2 -------------");
    setPrefixFileName("TodoMVC2");
    compareDiffAlgorithmsUsing4Htmls(true);
  }

  private void compareDiffAlgorithmsUsing2Htmls() {
    String html1 = readHtml();
    String html2 = readHtml();

    assertThat(html1, is(not(html2)));

    long time1 = System.currentTimeMillis();
    String diff = generator.generateDiffContent(html1, html2);
    long time2 = System.currentTimeMillis();
    String simpleName = generator.getClass().getSimpleName();
    LogFiles.writeLogFile(simpleName + ".txt", diff);
    System.out.println(simpleName + ": " + diff.length() + " (" + (time2 - time1) + ")");
  }

  private void compareDiffAlgorithmsUsing4Htmls(boolean isSame) {
    String html1 = readHtml();
    String html2 = readHtml();
    String html3 = readHtml();
    String html4 = readHtml();

    assertThat(html1, is(not(html2)));
    assertThat(html3, is(not(html4)));
    assertThat(html1, is(not(html3)));
    assertThat(html2, is(not(html4)));

    long time1 = System.currentTimeMillis();
    String diff1 = generator.generateDiffContent(html1, html2);
    long time2 = System.currentTimeMillis();
    String diff2 = generator.generateDiffContent(html3, html4);
    String simpleName = generator.getClass().getSimpleName();
    LogFiles.writeLogFile(simpleName + "1.txt", diff1);
    LogFiles.writeLogFile(simpleName + "2.txt", diff2);
    if (isSame) {
      assertEquals(diff1, diff2);
    } else {
      assertNotEquals(diff1, diff2);
    }
    System.out.println(simpleName + ": " + diff1.length() + " (" + (time2 - time1) + ")");
  }
}
