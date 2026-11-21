package com.orangehrm.tests;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.DatabaseManager;
import com.orangehrm.utilities.ExtentManager;

public class DatabaseVerificationTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	//enable the below test as true only when the application is up and running via xampp
	@Test(dataProvider = "dbValidLoginData", dataProviderClass = DataProviders.class,enabled = false)
	public void verifyEmployeeSearchWithDBValidation(Map<String, String> data) {
		SoftAssert softAssert = getSoftAssert();
		getDriver().get(prop.getProperty("local_url"));
		ExtentManager.logStep("Logging into application");
		loginPage.loginApplication(data.get("username"), data.get("password"));
		ExtentManager.logStep("Login successful");
		homePage.clickOnPIMTab();
		ExtentManager.logStep("Clicked on PIM Tab...");
		ExtentManager.logStep("Searching for an employee...");
		homePage.employeeSearch(data.get("empName"));
		ExtentManager.logStep("fetching data from database");
		String employeeId = data.get("empId");
		Map<String, String> employeeDetails = DatabaseManager.getEmployeeDetails(employeeId);
		String firstName = employeeDetails.get("firstName");
		String middleName = employeeDetails.get("middleName");
		String lastName = employeeDetails.get("lastName");
		String firstAndMiddleName = (firstName + " " + middleName).trim();
		ExtentManager.logStep("Verifying the employee first and middle name");
		staticWait(3);
		softAssert.assertTrue(homePage.verifyFirstAndMidName(firstAndMiddleName),
				"First and Middle Name are not matching!!!1");
		ExtentManager.logStep("Successfull Verification of first and last name");
		ExtentManager.logStep("Verifying the employee last name");
		softAssert.assertTrue(homePage.verifyLastName(lastName), "Employee last name verification failed");
		ExtentManager.logStep("Database validation was completed successfully");
		softAssert.assertAll();

	}

}
