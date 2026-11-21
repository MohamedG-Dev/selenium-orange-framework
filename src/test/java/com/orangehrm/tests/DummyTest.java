package com.orangehrm.tests;

import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;

public class DummyTest extends BaseClass {

	@Test
	public void dummyTest() {
		String title = getDriver().getTitle();
		assert title.equals("OrangeHRM") : "Test Failed - Title is not Matching";
		System.out.println("Test passed and Title is matching");
	}

}
