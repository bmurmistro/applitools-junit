package com.applitools;

import java.net.URL;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseTest
{
  protected static WebDriver driver;

  @Rule
  public EyesWatcher eyesWatcher = new EyesWatcher();

  @BeforeClass
  public static void setUp() throws Exception {
    //ChromeOptions options = new ChromeOptions();
    //options.addArguments("--headless");
    Configuration.browser = "chrome";
    final String sauceUser = System.getenv("SAUCE_USER");
    final String sauceKey = System.getenv("SAUCE_KEY");
    String url = "http://" + sauceUser + ":" + sauceKey + "@ondemand.saucelabs.com:80/wd/hub";

    DesiredCapabilities caps = DesiredCapabilities.chrome();
    caps.setCapability("platform", "Windows 10");
    caps.setCapability("version", "84.0");

    driver = new RemoteWebDriver(new URL(url), caps);
    WebDriverRunner.setWebDriver(driver);
    //driver = WebDriverRunner.getAndCheckWebDriver();
  }

  @AfterClass
  public static void tearDown() {
    driver.quit();
  }
}
