package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actionDriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {
	protected static Properties prop;
	// protected static WebDriver driver;
	// private static ActionDriver actionDriver;
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>(); // New Changes as per thread
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();// New Changes as per thread
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);
	private static ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);

	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}

	// Load config file
	@BeforeSuite
	public void loadConfig() {
		prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream(
					System.getProperty("user.dir") + "/src/main/resources/config.properties");
			prop.load(fis);
			for (String key : prop.stringPropertyNames())
				prop.setProperty(key, prop.getProperty(key).trim());
			logger.info("config.propeties file loaded");
			// Start the Extent Report
			// ExtentManager.getReporter(); //--This has been implemented in
			// TestListener.java class
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Initialize the WebDriver based on the browser defined in the properties file
	private synchronized void launchBrowser() {
		logger.info("headless raw value = [" + prop.getProperty("headless") + "]");
		String browser = prop.getProperty("browser");
		String value = prop.getProperty("headless");
		if (value == null)
			throw new IllegalStateException("'headless' is missing in config.properties");
		boolean headless = Boolean.parseBoolean(value);
		switch (browser.toUpperCase()) {
		case "CHROME":
			ChromeOptions options = new ChromeOptions();
			if (headless) {
				options.addArguments("--headless=new");
				options.addArguments("--window-size=1920,1080");
			}
			options.addArguments("--disable-gpu");// Disbale GPU for headless mode
			options.addArguments("--disable-notifications");// Disable browser notifications
			options.addArguments("--no-sandbox");// Required for some CI environments library
			options.addArguments("--disable-dev-shm-usage");// Resolve issues in
			// driver = new ChromeDriver();
			driver.set(new ChromeDriver(options));// New Changes as per thread
			ExtentManager.registerDriver(getDriver());
			logger.info("Chrome Driver instance is created");
			break;
		case "EDGE":
			// driver = new EdgeDriver();
			EdgeOptions edgeoptions = new EdgeOptions();
			if (headless) {
				edgeoptions.addArguments("--headless=new");
				edgeoptions.addArguments("--window-size=1920,1080");
			}
			edgeoptions.addArguments("--disable-gpu");// Disbale GPU for headless mode
			edgeoptions.addArguments("--disable-notifications");// Disable browser notifications
			edgeoptions.addArguments("--no-sandbox");// Required for some CI environments library
			edgeoptions.addArguments("--disabled-dev-shm-usage");// Resolve issues in
			driver.set(new EdgeDriver(edgeoptions));// New Changes as per thread
			ExtentManager.registerDriver(getDriver());
			logger.info("Edge Driver instance is created");
			break;
		case "FIREFOX":
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			if (headless) {
				firefoxOptions.addArguments("--headless=new");
				firefoxOptions.addArguments("--window-size=1920,1080");
			}
			firefoxOptions.addArguments("--disable-gpu");// Disbale GPU for headless mode
			firefoxOptions.addArguments("--disable-notifications");// Disable browser notifications
			firefoxOptions.addArguments("--no-sandbox");// Required for some CI environments library
			firefoxOptions.addArguments("--disabled-dev-shm-usage");// Resolve issues in
			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver(firefoxOptions));// New Changes as per thread
			ExtentManager.registerDriver(getDriver());
			logger.info("Firefox Driver instance is created");
			break;
		default:
			throw new IllegalArgumentException("Invalid browser: " + browser);
		}
	}

	private void configureBrowser() {
		// Implicit wait
		// New Changes as per thread
		getDriver().manage().timeouts()
				.implicitlyWait(Duration.ofSeconds(Integer.parseInt(prop.getProperty("implicitWait"))));
		// Maximize the browser
		if (Boolean.parseBoolean(prop.getProperty("headless")))
			getDriver().manage().window().setSize(new Dimension(1980, 1080));
		else
			getDriver().manage().window().maximize();// New Changes as per thread
		// Navigate to the application
		getDriver().get(prop.getProperty("url"));// New Changes as per thread
	}

	@BeforeMethod
	public synchronized void setup() {
		logger.info("Setting up WebDriver for: " + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		logger.info("WebDriver is initialized and Maximized");
		// Initialize the actionDriver object once
//		if (actionDriver == null) {
//			actionDriver = new ActionDriver(driver);
//			logger.info("Action Driver instance is created"+Thread.currentThread().threadId());
//		}

		// Initialize action driver for the current thread
		actionDriver.set(new ActionDriver(getDriver()));// New Changes as per thread
		logger.info("ActionDriver initialized for thread: " + Thread.currentThread().threadId());
	}

	@AfterMethod
	public synchronized void tearDown() {
		if (getDriver() != null) {// New Changes as per thread
			try {
				getDriver().quit();// New Changes as per thread
			} catch (Exception e) {
				logger.error("unable to quit the driver: " + e.getMessage());
			}
		}
		logger.info("WebDriver instance is closed");
		// driver = null;
		// actionDriver = null;
		driver.remove();// New Changes as per thread
		actionDriver.remove();// New Changes as per thread
		// ExtentManager.endTest(); //--This has been implemented in TestListener.java
		// class
	}

	// Static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

//	// Driver getter method
//	public WebDriver getDriver() {
//		return this.driver;
//	}
//
//	// Driver setter method
//	public void setDriver(WebDriver driver) {
//		this.driver = driver;
//	}

	// Getter method for WebDriver - //New Changes as per thread
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			logger.error("WebDriver is not initialized");
			throw new IllegalStateException("WenDriver is not initialized");
		}

		return driver.get();
	}

	// New Changes as per thread
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			logger.error("ActionDriver is not initialized");
			throw new IllegalStateException("Action Driver is not initialized");
		}
		return actionDriver.get();
	}

	// Getter method for Properties obj
	public static Properties getProp() {
		return prop;
	}

}
