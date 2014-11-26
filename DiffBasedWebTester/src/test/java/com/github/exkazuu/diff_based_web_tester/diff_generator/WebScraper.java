package com.github.exkazuu.diff_based_web_tester.diff_generator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;

public class WebScraper {
  private WebDriver driver;
  private int index;

  public WebScraper() {}

  @Before
  public void before() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setEnableNativeEvents(true);
    driver = new FirefoxDriver(profile);
  }

  @After
  public void after() {
    driver.quit();
  }

  @Test
  public void scrape() {
    setPrefixFileName("Google");
    writeLogFile("https://www.google.co.jp/?#q=abc", 2000);
    writeLogFile("https://www.google.co.jp/?#q=def", 2000);

    setPrefixFileName("GitHub");
    writeLogFile("https://github.com/erikhuda/thor", 1000);
    writeLogFile("https://github.com/junit-team/junit", 1000);

    setPrefixFileName("Twitter");
    writeLogFile("https://twitter.com/john", 0);
    writeLogFile("https://twitter.com/bob", 0);

    setPrefixFileName("TodoMVC");
    writeLogFile("http://todomvc.com/architecture-examples/backbone/", 0);
    driver.findElement(By.id("new-todo")).sendKeys("test\n");
    sleep(1000);
    writeLogFile();
    removeFirstTodo();
    writeLogFile();
    driver.findElement(By.id("new-todo")).sendKeys("test2\n");
    sleep(1000);
    writeLogFile();
    removeFirstTodo();

    setPrefixFileName("TodoMVC2");
    driver.get("http://todomvc.com/architecture-examples/backbone/");
    String[] htmls1 = addTodos("abc", "test");
    String[] htmls2 = addTodos("def", "test");
    writeLogFile(htmls1[0]);
    writeLogFile(htmls1[1]);
    writeLogFile(htmls2[0]);
    writeLogFile(htmls2[1]);
  }

  private void setPrefixFileName(String prefixFileName) {
    index = 0;
    LogFiles.setPrefixFileName(prefixFileName);
  }

  private void writeLogFile(String url, int sleepTime) {
    driver.get(url);
    sleep(sleepTime);
    writeLogFile();
  }

  private void writeLogFile() {
    writeLogFile(driver.getPageSource());
  }

  private void writeLogFile(String html) {
    LogFiles.writeLogFile("raw" + (++index) + ".html", html);
  }

  private String[] addTodos(String todo1, String todo2) {
    driver.findElement(By.id("new-todo")).sendKeys(todo1 + "\n");
    sleep(1000);
    String html1 = driver.getPageSource();
    removeFirstTodo();
    driver.findElement(By.id("new-todo")).sendKeys(todo1 + "\n");
    sleep(1000);
    driver.findElement(By.id("new-todo")).sendKeys(todo2 + "\n");
    sleep(1000);
    String html2 = driver.getPageSource();
    removeFirstTodo();
    removeFirstTodo();
    return new String[] {html1, html2};
  }

  private void removeFirstTodo() {
    new Actions(driver).moveToElement(driver.findElement(By.className("toggle"))).build().perform();
    sleep(500);
    driver.findElement(By.className("destroy")).click();
    sleep(1000);
  }

  private static void sleep(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
