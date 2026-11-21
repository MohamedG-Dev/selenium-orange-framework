package com.orangehrm.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class TestListener implements ITestListener,IAnnotationTransformer {

	// Triggered when the test starts
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		// Start logging in Extent Report
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test Started:" + testName);
	}

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}

	// Triggered when a test passes
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		if (result.getTestClass().getName().toLowerCase().contains("api")) {
			ExtentManager.logStepValidationForAPI("End of Test: " + testName + " test is passed successfully");
		} else {
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test passed successfully",
					"End of Test: " + testName + " - Test Passed.");
		}

	}

	// Triggered when a test fails
	@Override
	public void onTestFailure(ITestResult result) {
		result.getThrowable().printStackTrace();
		String testName = result.getMethod().getMethodName();
		String failureMessage = result.getThrowable().getMessage();
		ExtentManager.logStep(failureMessage);
		if (result.getTestClass().getName().toLowerCase().contains("api")) {
			ExtentManager.logFailureAPI("End of Test: " + testName + " - Test Failed!!");
		} else {
			ExtentManager.logFailure(BaseClass.getDriver(), "Test Failed!!!",
					"End of Test: " + testName + " - Test Failed!!");
		}
	}

	// Triggered when a test skips
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test Skipped:" + testName);
	}

	// Triggered when the suite starts
	@Override
	public void onStart(ITestContext context) {
		// Initialize the extent reports
		ExtentManager.getReporter();
	}

	// Triggered when the suite ends
	@Override
	public void onFinish(ITestContext context) {
		// Flush the extent report
		ExtentManager.endTest();
	}

}
