package com.orangehrm.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.orangehrm.base.BaseClass;

public class ExcelReader {

	private static Logger logger = BaseClass.logger;
	private static final DataFormatter formatter = new DataFormatter();

	public static List<String[]> getSheetData(String filePath, String sheetName) {
		// data variable is defined as list of arrays of string
		List<String[]> data = new ArrayList<>();

		//
		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null)
				throw new IllegalArgumentException("Sheet: " + sheetName + " doesn't exists!!!");

			// iterate over rows
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue;
				// Read all the cells of a row
				List<String> rowData = new ArrayList<>();
				for (Cell cell : row) {
					rowData.add(getCellValue(cell));
				}
				// convert the rowData to string array
				data.add(rowData.toArray(new String[0]));
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return data;
	}

//	private static String getCellValue(Cell cell) {
//		if (cell == null)
//			return "";
//		switch (cell.getCellType()) {
//		case STRING:
//			return cell.getStringCellValue();
//		case NUMERIC:
//			if (DateUtil.isCellDateFormatted(cell))
//				return cell.getDateCellValue().toString();
//			return String.valueOf((int) cell.getNumericCellValue());
//		case BOOLEAN:
//			return String.valueOf(cell.getBooleanCellValue());
//		default:
//			return "";
//		}
//
//	}

	

	private static String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
		}
		return formatter.formatCellValue(cell);
	}

	// new imports needed: java.util.Map, java.util.LinkedHashMap

	public static List<Map<String, String>> getSheetDataAsMap(String filePath, String sheetName) {
		// each row becomes one Map: column header -> cell value
		List<Map<String, String>> data = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null)
				throw new IllegalArgumentException("Sheet: " + sheetName + " doesn't exists!!!");

			// Row 0 is the header row. Its values become the map keys
			Row headerRow = sheet.getRow(0);
			if (headerRow == null)
				throw new IllegalArgumentException("Sheet: " + sheetName + " has no header row");

			List<String> headers = new ArrayList<>();
			for (Cell cell : headerRow) {
				headers.add(getCellValue(cell).trim());
			}

			// iterate over the data rows (skip the header)
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue;

				// LinkedHashMap keeps the columns in the same order as the sheet
				Map<String, String> rowData = new LinkedHashMap<>();
				for (int i = 0; i < headers.size(); i++) {
					// getCell(i) reads by column index, so a blank cell stays in its own column
					// (getCellValue already returns "" for a null cell)
					rowData.put(headers.get(i), getCellValue(row.getCell(i)));
				}
				data.add(rowData);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return data;
	}

}
