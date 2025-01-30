/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package gui;

import javax.swing.JLabel;
import javax.swing.JTextField;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kovid
 */
public class AddNewStockTest {
    
    public AddNewStockTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getSupplierField method, of class AddNewStock.
     */
    @Test
    public void testGetSupplierField() {
        System.out.println("getSupplierField");
        AddNewStock instance = new AddNewStock();
        JTextField expResult = null;
        JTextField result = instance.getSupplierField();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBrandLabel method, of class AddNewStock.
     */
    @Test
    public void testGetBrandLabel() {
        System.out.println("getBrandLabel");
        AddNewStock instance = new AddNewStock();
        JLabel expResult = null;
        JLabel result = instance.getBrandLabel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCategoryLabel method, of class AddNewStock.
     */
    @Test
    public void testGetCategoryLabel() {
        System.out.println("getCategoryLabel");
        AddNewStock instance = new AddNewStock();
        JLabel expResult = null;
        JLabel result = instance.getCategoryLabel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProductField method, of class AddNewStock.
     */
    @Test
    public void testGetProductField() {
        System.out.println("getProductField");
        AddNewStock instance = new AddNewStock();
        JTextField expResult = null;
        JTextField result = instance.getProductField();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadSizes method, of class AddNewStock.
     */
    @Test
    public void testLoadSizes() {
        System.out.println("loadSizes");
        AddNewStock instance = new AddNewStock();
        instance.loadSizes();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class AddNewStock.
     */
  
    /**
     * Test of validateDate method, of class AddNewStock.
     */
    @Test
    public void testValidateDate() {
        System.out.println("validateDate");
        String date = "";
        boolean expResult = false;
        boolean result = AddNewStock.validateDate(date);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
