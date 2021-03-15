package jmri.jmrit.throttle;

import jmri.util.JUnitUtil;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class KeyListenerInstallerTest {

    @Test
    public void testCTor() {
        InputsListenerInstaller t = new InputsListenerInstaller();
        Assert.assertNotNull("exists",t);
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(KeyListenerInstallerTest.class);

}
