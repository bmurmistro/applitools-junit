package com.applitools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

public class EyesWatcher
    extends TestWatcher
{
  public static Eyes eyes = new Eyes();

  private String testName;

  private static BatchInfo batch;

  private static final String APPLITOOLS_KEY = System.getProperty("APPLITOOLS_API_KEY", System.getenv("APPLITOOLS_API_KEY"));

  private static final String APPLICATION_NAME = System.getProperty("applicationName", "Branch Test");
  
  private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

  static {
    String localBranchName = System.getProperty("branchName", System.getenv("GIT_BRANCH_NAME"));
    if (localBranchName == null) {
      localBranchName = "default";
    }
    eyes.setIsDisabled(APPLITOOLS_KEY == null);

    if (!eyes.getIsDisabled()) {
      String buildNumber = System.getenv("BUILD_NUMBER");
      BatchInfo batchInfo = new BatchInfo((buildNumber != null ? "#" + buildNumber : " " + localBranchName));

      String batchId = System.getenv("APPLITOOLS_BATCH_ID");
      if (batchId != null) {
        batchInfo.setId(batchId);
      }
      eyes.setBatch(batchInfo);
      eyes.setApiKey(APPLITOOLS_KEY);
    }
  }

  @Override
  protected void starting(Description description) {
    testName = description.getTestClass().getSimpleName() + "." + description.getMethodName();
  }

  @Override
  protected void finished(Description description) {
    try {
      // End visual testing. Validate visual correctness.
      if (eyes.getIsOpen()) {
        eyes.close(true);
      }
    }
    finally {
      testName = null;
      // Abort test in case of an unexpected error.
      eyes.abortIfNotClosed();
    }
  }

  public void eyesCheck(ICheckSettings settings) {
    eyesCheck(null, settings);
  }

  /**
   * Convenience method for performing the Applitools validation.
   *
   * @param tag or step name of the validation
   */
  public void eyesCheck(String tag, ICheckSettings settings) {
    if (!eyes.getIsOpen()) {
      WebDriver remoteDriver = WebDriverRunner.getAndCheckWebDriver();

      if (remoteDriver instanceof WrapsDriver) {
        remoteDriver = ((WrapsDriver) remoteDriver).getWrappedDriver();
      }

      eyes.open(remoteDriver, APPLICATION_NAME, testName, new RectangleSize(800, 600));
    }
    eyes.check(tag, settings);
  }
}
