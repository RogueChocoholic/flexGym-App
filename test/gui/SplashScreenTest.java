/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package gui;

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
public class SplashScreenTest {
    
    public SplashScreenTest() {
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
     * Test of readLoginLog method, of class SplashScreen.
     */
    @Test
    public void testReadLoginLog() {
        System.out.println("readLoginLog");
        String filePath = "C:\\Users\\kovid\\FlexGymLogs\\login.log";
        String expResult = "Logout :{0} : {1} : at {2}";
        String result = SplashScreen.readLoginLog(filePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class SplashScreen.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        SplashScreen.main(args);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
}
