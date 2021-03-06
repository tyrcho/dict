package com.tyrcho.dictionary.model;

import junit.framework.TestCase;

/**
 * @author  ALEXIS
 */
public class TwoWayDictionnaryTest extends TestCase {

    private static final String SALUT_A_TOUS = "Salut e tous";
    private static final String HELLO_WORLD = "Hello, world";
    private static final String SALUT = "salut";
    private static final String HELLO = "hello";
    private static final String BONJOUR = "bonjour";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TwoWayDictionnaryTest.class);
    }
    
    private TwoWayDictionary dictionnary;
    
    public void setUp() {
        dictionnary=new TwoWayDictionary("francais", "anglais");
        dictionnary.addTranslation(SALUT, HELLO);
        dictionnary.addTranslation(BONJOUR, HELLO);
        dictionnary.addSecondLanguageExplaination(HELLO, HELLO_WORLD);
        dictionnary.addFirstLanguageExplaination(SALUT, SALUT_A_TOUS);
    }
    
    public void testFirstGetter()
    {
        assertTrue(dictionnary.getFirstLanguageEntry(BONJOUR).translations().contains(HELLO));
        assertTrue(dictionnary.getFirstLanguageEntry(SALUT).translations().contains(HELLO));
    }

    public void testSecondGetter()
    {
        assertTrue(dictionnary.getSecondLanguageEntry(HELLO).translations().contains(BONJOUR));
        assertTrue(dictionnary.getSecondLanguageEntry(HELLO).translations().contains(SALUT));
    }
    
    public void testExplaination() {
        assertEquals(HELLO_WORLD, dictionnary.getSecondLanguageEntry(HELLO).explaination());
        assertEquals(SALUT_A_TOUS, dictionnary.getFirstLanguageEntry(SALUT).explaination());
    }
    
    public void testRemove() {
        dictionnary.removeFirstLanguageWord(SALUT);
        assertTrue(dictionnary.getFirstLanguageEntry(BONJOUR).translations().contains(HELLO));
        assertNull(dictionnary.getFirstLanguageEntry(SALUT));
        assertTrue(dictionnary.getSecondLanguageEntry(HELLO).translations().contains(BONJOUR));
        assertFalse(dictionnary.getSecondLanguageEntry(HELLO).translations().contains(SALUT));
    }
}
