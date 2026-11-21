package com.orangehrm.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiUtility {

	// method to send the GET request
	public static Response getRequest(String endPoint) {
		return RestAssured.get(endPoint);
	}

	// method to send the POST request
	public static Response postRequest(String endPoint, String payload) {
		return RestAssured.given().header("Content-Type", "application/json").body(payload).post();
	}

	// method to validate response status code
	public static boolean validateStatusCode(Response response, int expectedStatusCode) {
		return response.getStatusCode() == expectedStatusCode;
	}

	// method to extract value from json response
	public static String getValueFromResponse(Response response, String value) {
		return response.jsonPath().get(value);
	}
}
