package com.orangehrm.utilities;

import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

public class DataProviders {

	private static final String FILE_PATH = System.getProperty("user.dir")
			+ "/src/test/resources/testdata/TestData.xlsx";

	@DataProvider(name="validLoginData")
	public static Object[][] validLoginData() {
		return getSheetData("ValidLoginData");
	}

	@DataProvider(name="inValidLoginData")
	public static Object[][] inValidLoginData() {
		return getSheetData("InValidLoginData");
	}
	
	@DataProvider(name="dbValidLoginData")
	public static Object[][] dbValidLoginData() {
		//return getSheetData("DB_ValidLoginData");
		return getSheetDataAsMap("DB_ValidLoginData");
	}

	private static Object[][] getSheetData(String sheetName) {
		List<String[]> sheetData = ExcelReader.getSheetData(FILE_PATH, sheetName);
		Object[][] data = new Object[sheetData.size()][sheetData.get(0).length];
		for (int i = 0; i < sheetData.size(); i++) {
			data[i] = sheetData.get(i);

		}
		return data;
	}
	
	private static Object[][] getSheetDataAsMap(String sheetName) {
		List<Map<String, String>> sheetData = ExcelReader.getSheetDataAsMap(FILE_PATH, sheetName);
		Object[][] data = new Object[sheetData.size()][1]; // one parameter per test: the Map
		for (int i = 0; i < sheetData.size(); i++) {
			data[i][0] = sheetData.get(i);
		}
		return data;
	}

}
