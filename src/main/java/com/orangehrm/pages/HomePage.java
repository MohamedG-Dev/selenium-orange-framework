package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actionDriver.ActionDriver;

public class HomePage {

	private ActionDriver actionDriver;

	public HomePage(WebDriver driver) {
		actionDriver = new ActionDriver(driver);
	}

	private By adminTab = By.xpath("//span[normalize-space()='Admin']");
	private By userIdButton = By.className("oxd-userdropdown-tab");
	private By logOutLink = By.linkText("Logout");
	private By orangeHRMLogo = By.xpath("//div[@class='oxd-brand-banner']/img");
	private By pimTab = By.xpath("//span[normalize-space()='PIM']");
	private By employeeSearch = By
			.xpath("//label[normalize-space()='Employee Name']/parent::div/following-sibling::div//input");
	private By searchButton = By.xpath("//button[normalize-space()='Search']");
	private By firstAndMidName = By.xpath("//div[contains(@class,'oxd-table-body')]/div/div/div[3]");
	private By lastName = By.xpath("//div[contains(@class,'oxd-table-body')]/div/div/div[4]");

	// method to verify if Admin tab is visible
	public boolean isAdminTabVisible() {
		return actionDriver.isDisplayed(adminTab);
	}

	public boolean verifyOrangeHRMLogoDisplayed() {
		return actionDriver.isDisplayed(orangeHRMLogo);
	}

	public void logoutApplication() {
		actionDriver.click(userIdButton);
		actionDriver.click(logOutLink);
	}

	public void clickOnPIMTab() {
		actionDriver.click(pimTab);
	}

	public void employeeSearch(String value) {
		actionDriver.enterText(employeeSearch, value);
		actionDriver.click(searchButton);
		actionDriver.scrollToElement(firstAndMidName);
	}

	public boolean verifyFirstAndMidName(String empFirstMidNameFromDB) {
		return actionDriver.compareText(firstAndMidName, empFirstMidNameFromDB);
	}

	public boolean verifyLastName(String empLastNameFromDB) {
		return actionDriver.compareText(lastName, empLastNameFromDB);
	}
}
