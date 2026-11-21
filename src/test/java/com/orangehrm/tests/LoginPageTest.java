package com.orangehrm.tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class)
	public void verifyLoginTest(String username, String password) {
		// ExtentManager.startTest("Valid Login Test"); //--This has been implemented in
		// TestListener.java class
		loginPage.loginApplication(username, password);
		ExtentManager.logStep("Entered username and password");
		ExtentManager.logStep("Verifying admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login Unsuccessful");
		ExtentManager.logStep("Valid successful");
		homePage.logoutApplication();
		ExtentManager.logStep("Log out successfully");
		staticWait(3);
	}

	@Test(dataProvider = "inValidLoginData", dataProviderClass = DataProviders.class)
	public void verifyInvalidLogin(String username, String password) {
		// ExtentManager.startTest("Invalid Login Test"); //--This has been implemented
		// in TestListener.java class
		loginPage.loginApplication(username, password);
		ExtentManager.logStep("Entered username and password");
		String expectedErrorMessage = "Invalid credentials";
		ExtentManager.logStep("Verifying error messagge is displayed");
		Assert.assertTrue(loginPage.errorMessageDisplayed(), "Login error message should be displayed");
		ExtentManager.logStep("Verifying login error message");
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage), "Test Failed:  Invalid error message");
		ExtentManager.logStep("Verified login error message");
	}
	
	//enable the below test as true only when the application is up and running via xampp
	@Test(dataProvider = "dbValidLoginData", dataProviderClass = DataProviders.class,enabled = false)
	public void verifyLoginTestViaDB(Map<String,String> data) {
		getDriver().get(prop.getProperty("local_url"));
		loginPage.loginApplication(data.get("username"), data.get("password"));
		ExtentManager.logStep("Entered username and password");
		ExtentManager.logStep("Verifying admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login Unsuccessful");
		ExtentManager.logStep("Valid successful");
		homePage.logoutApplication();
		ExtentManager.logStep("Log out successfully");
	}

}
