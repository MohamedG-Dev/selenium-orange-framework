package com.orangehrm.actionDriver;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver,
				Duration.ofSeconds(Integer.parseInt(BaseClass.getProp().getProperty("webdriverWait"))));
	}

	// Wait for ElementToBeClickable
	private void waitForElementToBeClickable(By locator) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(locator));
		} catch (Exception e) {
			logger.error("element is not clickable" + e.getMessage());
		}
	}

	// Wait for Element to be visible
	private WebElement waitForElementToBeVisible(By locator) {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		} catch (Exception e) {
			logger.error("element is not visible: " + e.getMessage());
			return null;
		}
	}

	// method to click an element
	public void click(By locator) {
		String elementDescription = getElementDescription(locator);
		try {
			waitForElementToBeClickable(locator);
			applyBorder(locator, "green");
			driver.findElement(locator).click();
			ExtentManager.logStep("Clicked an element: " + elementDescription);
			logger.info("Clicked an Element: " + elementDescription);
		} catch (Exception e) {
			applyBorder(locator, "red");
			ExtentManager.logFailure(BaseClass.getDriver(), "unable to click the element: ", e.getMessage());
			logger.error("unable to click the element: " + e.getMessage());
		}
	}

	// method to enter value into input field
	public void enterText(By locator, String input) {
		String elementDescription = getElementDescription(locator);
		try {
			WebElement element = waitForElementToBeVisible(locator);
			applyBorder(locator, "green");
			element.clear();
			element.sendKeys(input);
			ExtentManager.logStep("Entered value: " + input + " in: " + elementDescription);
			logger.info("Entered the text on: " + elementDescription);
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to enter value to the input field: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to enter value to the input field: ",
					e.getMessage());
		}
	}

	// method to get text from input field
	public String getText(By locator) {
		try {
			waitForElementToBeVisible(locator);
			applyBorder(locator, "green");
			return driver.findElement(locator).getText();
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to get the text from the field: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to get the text from the field: ", e.getMessage());
			return "";
		}
	}

	// method to compare two text
	public boolean compareText(By locator, String expectedText) {
		try {
			waitForElementToBeVisible(locator);
			String actualText = driver.findElement(locator).getText();
			if (actualText.equals(expectedText)) {
				applyBorder(locator, "green");
				logger.info("Text are matching: " + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Text Comparison Passed",
						"Text Verified successfully! " + actualText + " equals" + expectedText);
				return true;
			} else {
				applyBorder(locator, "fail");
				logger.error("Text are not matching: " + actualText + " not equals " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text Comparison Failed!!!",
						"Text Comparison Failed!!! " + actualText + " not equals" + expectedText);
				return false;
			}
		} catch (Exception e) {
			applyBorder(locator, "fail");
			logger.error("Unable to compare text: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to compare text: ", e.getMessage());
			return false;
		}

	}

	// Method to check if an element is displayed
	public boolean isDisplayed(By locator) {
		try {
			WebElement element = waitForElementToBeVisible(locator);
			applyBorder(locator, "green");
			logger.info("Element is displayed: " + getElementDescription(locator));
			ExtentManager.logStep("Element is displayed: " + getElementDescription(locator));
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed:",
					getElementDescription(locator));
			return element.isDisplayed();
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("element is not displayed: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "element is not displayed: ", e.getMessage());
			return false;
		}
	}

	// method for scroll to an element
	public void scrollToElement(By locator) {
		try {
			applyBorder(locator, "green");
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(locator);
			jse.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to scroll into view element: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to scroll into view element: ", e.getMessage());
		}
	}

	// wait for page load
	public void waitForPageLoad(int timeout) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeout)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
					.executeScript("return document.readyState").equals("complete"));
		} catch (Exception e) {
			logger.error("Page didn't load within " + timeout + " and the error message is: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Page didn't load within " + timeout, e.getMessage());
		}
	}

	// method to get the description of an element using by locator
	public String getElementDescription(By locator) {
		if (driver == null)
			return "driver is null";
		if (locator == null)
			return "locator is null";
		WebElement element = driver.findElement(locator);
		String name = element.getDomAttribute("name");
		String id = element.getDomAttribute("id");
		String text = element.getText();
		String className = element.getDomAttribute("class");
		String placeholder = element.getDomAttribute("placeholder");

		// return the description based on element attributes
		try {
			if (isNotNullOrNotEmpty(name))
				return "Element with name: " + name;
			else if (isNotNullOrNotEmpty(id))
				return "Element with id: " + id;
			else if (isNotNullOrNotEmpty(text))
				return "Element with Text: " + truncateText(text, 50);
			else if (isNotNullOrNotEmpty(className))
				return "Element with Placeholder: " + className;
			else if (isNotNullOrNotEmpty(placeholder))
				return "Element with Placeholder: " + placeholder;
		} catch (Exception e) {
			logger.error("Unable to describe the element: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to describe the element: " + e.getMessage(),
					"unable to describe element");
		}
		return "Unable to describe the element";
	}

	private boolean isNotNullOrNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	// utility method to truncate long string
	private String truncateText(String text, int maxLength) {
		if (text == null || text.length() <= maxLength) {
			return text;
		}
		return text.substring(0, maxLength) + "...";
	}

	// utility method to border an element
	public void applyBorder(By locator, String color) {
		try {
			WebElement element = driver.findElement(locator);
			String script = "arguments[0].style.border='3px solid " + color + "'";
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript(script, element);
			logger.info("Applied the border with color: " + color + " To Element: " + getElementDescription(locator));
		} catch (Exception e) {
			logger.warn("Failed to apply border to the element: " + getElementDescription(locator), e.getMessage());
		}
	}

	public void selectByVisibleText(By locator, String value) {
		try {
			WebElement element = driver.findElement(locator);
			new Select(element).selectByVisibleText(value);
			applyBorder(locator, "green");
			logger.info("Selected dropdown value: " + value);
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to select the dropdown value: " + value, e);
		}
	}

	public void selectByVisibleValue(By locator, String value) {
		try {
			WebElement element = driver.findElement(locator);
			new Select(element).selectByValue(value);
			applyBorder(locator, "green");
			logger.info("Selected dropdown value: " + value);
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to select the dropdown value: " + value, e);
		}
	}

	public void selectByIndex(By locator, int index) {
		try {
			WebElement element = driver.findElement(locator);
			new Select(element).selectByIndex(index);
			applyBorder(locator, "green");
			logger.info("Selected dropdown value: " + index);
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to select the dropdown value: " + index, e);
		}
	}

	public List<String> getDropdownOptions(By locator) {
		try {
			WebElement element = driver.findElement(locator);
			Select select = new Select(element);
			List<String> optionsList = select.getOptions().stream().map(WebElement::getText).toList();
			applyBorder(locator, "green");
			logger.info("Retrieved dropdown options: {}" + getElementDescription(locator));
			return optionsList;
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			applyBorder(locator, "red");
			logger.error("Unable to get dropdown options: " + getElementDescription(locator), e);
			throw e;
		}
	}

	public void clickByJavaScriptExecutor(By locator) {
		try {
			WebElement element = driver.findElement(locator);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			applyBorder(locator, "green");
			logger.info("Clicked element using JavascriptExecutor: " + getElementDescription(locator));
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to click the element using JavascriptExecutor: " + getElementDescription(locator));
		}
	}

	public void scrolltoBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight);");
		logger.info("Scrolled to the bottom of the page.");
	}

	// Method to highlight an element using JavaScript
	public void highlightElementJS(By locator) {
		try {
			WebElement element = driver.findElement(locator);
			((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid yellow'", element);
			logger.info("Highlighted element using JavaScript: " + getElementDescription(locator));
		} catch (Exception e) {
			logger.error("Unable to highlight element using JavaScript", e);
		}
	}

	// Method to switch between browser windows
	public void switchToWindow(String windowTitle) {
		try {
			Set<String> windows = driver.getWindowHandles();
			for (String window : windows) {
				driver.switchTo().window(window);
				if (driver.getTitle().equals(windowTitle)) {
					logger.info("Switched to window: {}", windowTitle);
					return;
				}
			}
			logger.warn("Window with title: {} not found", windowTitle);
		} catch (Exception e) {
			logger.error("Unable to swith the window: {}", e);
		}
	}

	// Method to switch to an iframe
	public void switchToIFrame(By locator) {
		try {
			driver.switchTo().frame(driver.findElement(locator));
			logger.info("Switched to iframe: {}", getElementDescription(locator));
		} catch (Exception e) {
			logger.error("Unable to switch to iframe: {}", e);
		}
	}

	// Method to switch back to the default content
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
		logger.info("Switched back to default content.");
	}

	// Method to accept an alert popup
	public void acceptAlert() {
		try {
			driver.switchTo().alert().accept();
			logger.info("Alert accepted.");
		} catch (Exception e) {
			logger.error("No alert found to accept: {}", e);
		}
	}

	// Method to dismiss an alert popup
	public void dismissAlert() {
		try {
			driver.switchTo().alert().dismiss();
			logger.info("Alert dismissed.");
		} catch (Exception e) {
			logger.error("No alert found to dismiss: {}", e);
		}
	}

	// Method to get alert text
	public String getAlertText() {
		try {
			return driver.switchTo().alert().getText();
		} catch (Exception e) {
			logger.error("No alert text found", e);
			throw e;
		}
	}

	// Refresh a browser page
	public void refereshPage() {
		try {
			driver.navigate().refresh();
			ExtentManager.logStep("Page refreshed successfully.");
			logger.info("Page refreshed successfully.");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to refresh page", "refresh_page_failed");
			logger.error("Unable to refresh page: {}", e.getMessage());
		}
	}

	// getCurrentURL
	public String getCurrentURL() {
		try {
			String currentUrl = driver.getCurrentUrl();
			ExtentManager.logStep("Current URL fetched: " + currentUrl);
			logger.info("Current URL fetched: {}", currentUrl);
			return currentUrl;
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to fetch current URL", "get_current_url_failed");
			logger.error("Unable to fetch current URL: {}", e.getMessage());
			throw e;
		}
	}

	// maximize a window
	public void maximizeWindow() {
		try {
			driver.manage().window().maximize();
			ExtentManager.logStep("Browser window maximized.");
			logger.info("Browser window maximized.");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to maximize window", "maximize_window_failed");
			logger.error("Unable to maximize window: {}", e.getMessage());
		}
	}

	// move to element
	public void moveToElement(By locator) {
		String elementDescription = getElementDescription(locator);
		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(driver.findElement(locator)).perform();
			ExtentManager.logStep("Moved to element: " + elementDescription);
			logger.info("Moved to element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to move to element",
					elementDescription + "_move_failed");
			logger.error("Unable to move to element: {}", e.getMessage());
		}
	}

	public void dragAndDrop(By sourceLocator, By targetLocator) {
		String sourceDescription = getElementDescription(sourceLocator);
		String targetDescription = getElementDescription(targetLocator);
		try {
			Actions actions = new Actions(driver);
			actions.dragAndDrop(driver.findElement(sourceLocator), driver.findElement(targetLocator)).perform();
			ExtentManager.logStep("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
			logger.info("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to drag and drop",
					sourceDescription + "_drag_failed");
			logger.error("Unable to drag and drop: " + e.getMessage());
		}
	}

	public void doubleClick(By locator) {
		String elementDescription = getElementDescription(locator);
		try {
			Actions actions = new Actions(driver);
			actions.doubleClick(driver.findElement(locator)).perform();
			ExtentManager.logStep("Double-clicked on element: " + elementDescription);
			logger.info("Double-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to double-click element",
					elementDescription + "_doubleclick_failed");
			logger.error("Unable to double-click element: " + e.getMessage());
		}
	}

	public void rightClick(By locator) {
		String elementDescription = getElementDescription(locator);
		try {
			Actions actions = new Actions(driver);
			actions.contextClick(driver.findElement(locator)).perform();
			ExtentManager.logStep("Right-clicked on element: " + elementDescription);
			logger.info("Right-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to right-click element",
					elementDescription + "_rightclick_failed");
			logger.error("Unable to right-click element: " + e.getMessage());
		}
	}

	public void sendKeysWithActions(By locator, String value) {
		String elementDescription = getElementDescription(locator);
		try {
			Actions actions = new Actions(driver);
			actions.sendKeys(driver.findElement(locator), value).perform();
			ExtentManager.logStep("Sent keys to element: " + elementDescription + " | Value: " + value);
			logger.info("Sent keys to element --> " + elementDescription + " | Value: " + value);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to send keys",
					elementDescription + "_sendkeys_failed");
			logger.error("Unable to send keys to element: " + e.getMessage());
		}
	}

	public void clearText(By locator) {
		String elementDescription = getElementDescription(locator);
		try {
			driver.findElement(locator).clear();
			ExtentManager.logStep("Cleared text in element: " + elementDescription);
			logger.info("Cleared text in element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to clear text",
					elementDescription + "_clear_failed");
			logger.error("Unable to clear text in element: " + e.getMessage());
		}
	}

	public void uploadFile(By locator, String filePath) {
		try {
			driver.findElement(locator).sendKeys(filePath);
			applyBorder(locator, "green");
			logger.info("Uploaded file: " + filePath);
		} catch (Exception e) {
			applyBorder(locator, "red");
			logger.error("Unable to upload file: " + e.getMessage());
		}
	}
}
