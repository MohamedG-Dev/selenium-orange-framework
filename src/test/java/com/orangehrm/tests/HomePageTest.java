package com.orangehrm.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class HomePageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class)
	public void verifyOrangeHRMLogo(String username, String password) {
		// ExtentManager.startTest("Validating Home Logo Test"); //--This has been
		// implemented in TestListener.java class
		loginPage.loginApplication(username, password);
		ExtentManager.logStep("Entered credentials and Login is successfull");
		Assert.assertTrue(homePage.verifyOrangeHRMLogoDisplayed(), "OrangeHRM Logo is not visible");
		ExtentManager.logStep("Orange HRM Logo validation is successful");
	}

}
