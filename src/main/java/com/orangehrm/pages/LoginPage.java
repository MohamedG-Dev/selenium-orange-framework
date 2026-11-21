package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actionDriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class LoginPage {

	private ActionDriver actionDriver;

	public LoginPage(WebDriver driver) {
		//actionDriver = new ActionDriver(driver);
		this.actionDriver = BaseClass.getActionDriver();
	}

	// Define locators using By
	private By usernameField = By.name("username");
	private By passwordField = By.cssSelector("input[name='password']");
	private By loginButton = By.xpath("//button[normalize-space()='Login']");
	private By errorMessage = By.xpath("//p[normalize-space()='Invalid credentials']");

	// method to perform login
	public void loginApplication(String username, String password) {
		actionDriver.enterText(usernameField, username);
		actionDriver.enterText(passwordField, password);
		actionDriver.click(loginButton);
	}

	// method to check if error message is displayed
	public boolean errorMessageDisplayed() {
		return actionDriver.isDisplayed(errorMessage);
	}

	// method to get the text from the error message
	public String getText() {
		return actionDriver.getText(errorMessage);
	}

	// verify if error message is correct or not
	public boolean verifyErrorMessage(String expectedMessage) {
		return actionDriver.compareText(errorMessage, expectedMessage);
	}

}
