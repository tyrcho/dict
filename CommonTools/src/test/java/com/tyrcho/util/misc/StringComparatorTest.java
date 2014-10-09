package com.tyrcho.util.misc;

import static org.junit.Assert.*;

import org.junit.Test;

import com.tyrcho.util.misc.StringComparator;



public class StringComparatorTest {

	@Test
	public void compareWithEmptyString() {
		StringComparator comparator=new StringComparator("");
		assertTrue("same", comparator.compare("a123", "a123")==0);
		assertTrue("lowest number", comparator.compare("a123", "a124")<0);
		assertTrue("shortest", comparator.compare("a12", "a124")<0);
	}
	
	@Test
	public void compareWithLanguage() {
		StringComparator comparator=new StringComparator("321");
		assertTrue("same", comparator.compare("a123", "a123")==0);
		assertTrue("lowest number", comparator.compare("a123", "a124")>0);
		assertTrue("shortest", comparator.compare("a12", "a124")<0);
		assertTrue("language first", comparator.compare("a12", "aaa")<0);
	}
}
