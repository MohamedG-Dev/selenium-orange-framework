package com.orangehrm.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.orangehrm.base.BaseClass;

public class DatabaseManager {

	private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/orangehrm";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "";
	private static Logger logger = BaseClass.logger;

	public static Connection getDBConnection() {
		logger.info("Starting DB Connection....");
		try {
			Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
			logger.info("Database Connection is successfull....");
			return connection;
		} catch (SQLException e) {
			logger.error("Error while establishing the database connection" + e.getMessage());
			return null;
		}
	}

	public static Map<String, String> getEmployeeDetails(String employeeId) {
		String query = "SELECT emp_firstname,emp_middle_name,emp_lastname FROM hs_hr_employee where employee_id="
				+ employeeId + ";";
		Map<String, String> employeeDetails = new HashMap<>();
		try (Connection connection = getDBConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(query)) {
			logger.info("Executing Query: " + query);
			if (result.next()) {
				String firstName = result.getString("emp_firstname");
				String middleName = result.getString("emp_middle_name");
				String lastName = result.getString("emp_lastname");
				employeeDetails.put("firstName", firstName);
				employeeDetails.put("middleName", middleName!=null? middleName:"");
				employeeDetails.put("lastName", lastName);
				logger.info("Query executed successfully");
				logger.info("Employee Details fetched");
			}else {
				logger.error("employee data not found");
			}
		} catch (SQLException e) {
			logger.error("Error while executing the query" + e.getMessage());
			return null;
		}
		return employeeDetails;
	}
}
