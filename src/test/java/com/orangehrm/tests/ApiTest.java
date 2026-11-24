package com.orangehrm.tests;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.utilities.ApiUtility;
import com.orangehrm.utilities.ExtentManager;

import io.restassured.response.Response;

public class ApiTest {

	//@Test(retryAnalyzer = RetryAnalyzer.class)
	@Test
	public void verifyGetUserAPI() {
		SoftAssert softassert = new SoftAssert();
		String endPoint = "https://jsonplaceholder.typicode.com/users/1";
		ExtentManager.logStep("API EndPoint: " + endPoint);
		ExtentManager.logStep("Sending GET request to the API");
		Response response = ApiUtility.getRequest(endPoint);
		ExtentManager.logStep("Validating API response status code");
		boolean isStatusCodeValid = ApiUtility.validateStatusCode(response, 200);
		if (isStatusCodeValid) {
			ExtentManager.logStepValidationForAPI(isStatusCodeValid + "Status Code is as expected");
		} else {
			ExtentManager.logFailureAPI(isStatusCodeValid + "Status Code is not as expected");
		}
		softassert.assertTrue(isStatusCodeValid, "Status Code is not as expected");
		ExtentManager.logStep("Validating response body for username");
		String userName = ApiUtility.getValueFromResponse(response, "username");
		boolean isUserNameValid = "Bret".equals(userName);
		if (isUserNameValid) {
			ExtentManager.logStepValidationForAPI("username: " + userName + " is as expected");
		} else {
			ExtentManager.logFailureAPI("username: " + userName + " is not as expected");
		}
		softassert.assertTrue(isUserNameValid, "UserName: " + userName + "is not as expected");
		String emailID = ApiUtility.getValueFromResponse(response, "email");
		boolean isEmailValid = "Sincere@april.biz".equals(emailID);
		if (isEmailValid) {
			ExtentManager.logStepValidationForAPI("email: " + emailID + " is valid");
		} else {
			ExtentManager.logFailureAPI("email: " + emailID + " is not valid");
		}
		softassert.assertAll();
	}

}
