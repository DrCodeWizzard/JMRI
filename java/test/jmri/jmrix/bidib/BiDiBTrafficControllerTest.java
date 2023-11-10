package jmri.jmrix.bidib;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jmri.util.JUnitUtil;
import org.junit.Assert;

/**
 * Tests for the BiDiBTrafficController class
 * 
 * This class does not derive from the standard traffic controller classes
 * since the BiDiB traffic controller does not do so.
 * Instead, we have to create some useful test cases ourself.
 * Testing the jbidibc library is not in the scope of this test.
 *
 * @author  Eckart Meyer  Copyright (C) 2020
 */

public class BiDiBTrafficControllerTest {
    
    protected BiDiBTrafficController tc;
    
    @Test
    public void testCtor() {
        Assert.assertNotNull(tc);
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
            tc = new TestBiDiBTrafficController(new BiDiBInterfaceScaffold());
        }
    
    @After
    public void tearDown() {
    }

    
}
