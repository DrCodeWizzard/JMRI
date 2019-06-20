package jmri.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import com.alexandriasoftware.swing.JInputValidator;
import com.alexandriasoftware.swing.Validation;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import jmri.InstanceManager;
import jmri.Manager;
import jmri.Sensor;
import jmri.SensorManager;
import jmri.util.JUnitUtil;

/**
 *
 * @author Bob Jacobsen Copyright (C) 2017
 * @author Randall Wood Copyright (C) 2019
 */
public class NamedBeanComboBoxTest {

    @Test
    public void testSensorSimpleCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Manager<Sensor> m = InstanceManager.getDefault(jmri.SensorManager.class);
        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m);
        assertNotNull("exists", t);
    }

    @Test
    public void testSensorFullCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        m.provideSensor("IS1").setUserName("Sensor 1");
        Sensor s = m.provideSensor("IS2");
        s.setUserName("Sensor 2");
        m.provideSensor("IS3").setUserName("Sensor 3");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m, s, NamedBeanComboBox.DisplayOptions.DISPLAYNAME);

        assertNotNull("exists", t);
        assertEquals(s, t.getSelectedItem());
        assertEquals("Sensor 2", t.getSelectedItemUserName());
        assertEquals("IS2", t.getSelectedItemSystemName());
        assertEquals("Sensor 2", t.getSelectedItemDisplayName()); // Display name is user name if present
    }

    @Test
    public void testSensorSelectEntry() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        Sensor s1 = m.provideSensor("IS1");
        s1.setUserName("Sensor 1");
        Sensor s2 = m.provideSensor("IS2");
        s2.setUserName("Sensor 2");
        Sensor s3 = m.provideSensor("IS3");
        s3.setUserName("Sensor 3");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m, s2, NamedBeanComboBox.DisplayOptions.DISPLAYNAME);
        assertNotNull("exists", t);

        // s2 checked in prior test, change selection without repeating
        t.setSelectedItem(s3);
        assertEquals(s3, t.getSelectedItem());
        assertEquals("Sensor 3", t.getSelectedItemUserName());
        assertEquals("IS3", t.getSelectedItemSystemName());
        assertEquals("Sensor 3", t.getSelectedItemDisplayName()); // Display name is user name if present
    }

    @Test
    public void testSensorExcludeSome() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        Sensor s1 = m.provideSensor("IS1");
        s1.setUserName("Sensor 1");
        Sensor s2 = m.provideSensor("IS2");
        s2.setUserName("Sensor 2");
        Sensor s3 = m.provideSensor("IS3");
        s3.setUserName("Sensor 3");
        Sensor s4 = m.provideSensor("IS4");
        s4.setUserName("Sensor 4");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m, s2, NamedBeanComboBox.DisplayOptions.DISPLAYNAME);

        assertEquals(4, t.getItemCount());
        assertEquals(s2, t.getSelectedItem());

        t.setExcludedItems(new HashSet<>(Arrays.asList(new Sensor[]{s4})));
        assertNotNull(t.getExcludedItems());

        assertEquals(3, t.getItemCount());
        assertEquals(s2, t.getSelectedItem());

        t.setExcludedItems(new HashSet<>(Arrays.asList(new Sensor[]{s2, s4})));

        assertEquals(2, t.getItemCount());
        assertTrue(!s2.equals(t.getSelectedItem())); // just has to change, don't care what to
    }

    @Test
    public void testSensorChangeDisplayMode() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        Sensor s1 = m.provideSensor("IS1");
        s1.setUserName("Sensor 1");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m, s1, NamedBeanComboBox.DisplayOptions.DISPLAYNAME);
        JList<Sensor> l = new JList<>(t.getModel());
        assertNotNull("exists", t);
        assertEquals(NamedBeanComboBox.DisplayOptions.DISPLAYNAME, t.getDisplayOrder());

        assertEquals("Sensor 1",
                ((JLabel) t.getRenderer().getListCellRendererComponent(l, s1, 0, false, false)).getText());

        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.SYSTEMNAME);
        assertEquals(NamedBeanComboBox.DisplayOptions.SYSTEMNAME, t.getDisplayOrder());
        assertEquals("IS1",
                ((JLabel) t.getRenderer().getListCellRendererComponent(l, s1, 0, false, false)).getText());

        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.USERNAMESYSTEMNAME);
        assertEquals(NamedBeanComboBox.DisplayOptions.USERNAMESYSTEMNAME, t.getDisplayOrder());
        assertEquals("Sensor 1 (IS1)",
                ((JLabel) t.getRenderer().getListCellRendererComponent(l, s1, 0, false, false)).getText());

        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.SYSTEMNAMEUSERNAME);
        assertEquals(NamedBeanComboBox.DisplayOptions.SYSTEMNAMEUSERNAME, t.getDisplayOrder());
        assertEquals("IS1 (Sensor 1)",
                ((JLabel) t.getRenderer().getListCellRendererComponent(l, s1, 0, false, false)).getText());
    }

    @Test
    public void testSensorSetAndDefaultValidate() {
        Manager<Sensor> m = InstanceManager.getDefault(jmri.SensorManager.class);
        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m);

        assertTrue(t.isValidatingInput());

        t.setValidatingInput(false);
        assertTrue(!t.isValidatingInput());

        t.setValidatingInput(true);
        assertTrue(t.isValidatingInput());

    }

    @Test
    public void testSensorEditText()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        Sensor s1 = m.provideSensor("IS1");
        s1.setUserName("Sensor 1");
        Sensor s2 = m.provideSensor("IS2");
        s2.setUserName("Sensor 2");
        Sensor s3 = m.provideSensor("IS3");
        s3.setUserName("Sensor 3");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m);
        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.SYSTEMNAME);
        t.setAllowNull(true);
        t.setEditable(true);
        JTextField c = ((JTextField) t.getEditor().getEditorComponent());

        assertEquals("", c.getText());

        c.setText("IS2");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS2", c.getText());
        assertEquals(s2, t.getSelectedItem());
    }

    @Test
    public void testSensorTestProvidingValidity()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m);
        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.SYSTEMNAMEUSERNAME);
        t.setAllowNull(true);
        t.setEditable(true);
        t.setProviding(true);
        JTextField c = ((JTextField) t.getEditor().getEditorComponent());

        // test with no matching bean and isValidatingInput() == false
        // should always match NONE
        t.setValidatingInput(false);

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        Sensor s1 = t.getSelectedItem();
        assertEquals(s1, m.getBeanBySystemName("IS1"));
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertEquals(s1, t.getSelectedItem()); // selection did not change because of invalid input
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        // clear manager
        m.deregister(s1);
        s1 = null;

        // test with no matching bean and isValidatingInput() == true
        // should match NONE when empty and DANGER otherwise
        t.setValidatingInput(true);

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        s1 = t.getSelectedItem();
        assertEquals(s1, m.getBeanBySystemName("IS1"));
        assertEquals(Validation.Type.INFORMATION, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertEquals(s1, t.getSelectedItem()); // selection did not change because of invalid input
        assertEquals(Validation.Type.DANGER, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        // clear manager
        m.deregister(s1);
        s1 = null;

        // test with a matching bean and isValidatingInput() == false
        // should always match NONE
        t.setValidatingInput(false);

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        s1 = t.getSelectedItem();
        assertEquals(s1, m.getBeanBySystemName("IS1"));
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertEquals(s1, t.getSelectedItem()); // selection did not change because of invalid input
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        // clear manager
        m.deregister(s1);
        s1 = null;

        // test with a matching bean and isValidatingInput() == true
        // should match DANGER with text "K " and NONE otherwise
        t.setValidatingInput(true);

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        t.setSelectedItem(null); // change selection to verify selection changes
        assertNull(t.getSelectedItem());
        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        s1 = t.getSelectedItem();
        assertEquals(s1, m.getBeanBySystemName("IS1"));
        assertEquals(Validation.Type.INFORMATION, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertEquals(s1, t.getSelectedItem()); // selection did not change because of invalid input
        assertEquals(Validation.Type.DANGER, ((JInputValidator) c.getInputVerifier()).getValidation().getType());
    }

    @Test
    public void testSensorTestNonProvidingValidity()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m);
        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.SYSTEMNAMEUSERNAME);
        t.setAllowNull(true);
        t.setEditable(true);
        t.setProviding(false);
        JTextField c = ((JTextField) t.getEditor().getEditorComponent());

        // test with no matching bean and isValidatingInput() == false
        // should always match NONE
        t.setValidatingInput(false);

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        // test with no matching bean and isValidatingInput() == true
        // should match NONE when empty and WARNING otherwise
        t.setValidatingInput(true);

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.WARNING, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertNull(t.getSelectedItem());        
        assertEquals(Validation.Type.WARNING, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        // test with a matching bean and isValidatingInput() == false
        // should always match NONE
        t.setValidatingInput(false);
        Sensor s = m.provide("IS1");

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertNull(t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        assertEquals(s, t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertEquals(s, t.getSelectedItem()); // selection did not change because of invalid input
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        // test with a matching bean and isValidatingInput() == true
        // should match WARNING with text "K " and NONE otherwise
        t.setValidatingInput(true);

        c.setText("");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("", c.getText());
        assertEquals(s, t.getSelectedItem()); // selection did not change because of invalid input
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        t.setSelectedItem(null); // change selection to verify selection changes
        assertNull(t.getSelectedItem());
        c.setText("IS1");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("IS1", c.getText());
        assertEquals(s, t.getSelectedItem());
        assertEquals(Validation.Type.NONE, ((JInputValidator) c.getInputVerifier()).getValidation().getType());

        c.setText("K ");
        c.getInputVerifier().verify(c); // manually force validation because not on AWT thread
        assertEquals("K ", c.getText());
        assertEquals(s, t.getSelectedItem()); // selection did not change because of invalid input
        assertEquals(Validation.Type.WARNING, ((JInputValidator) c.getInputVerifier()).getValidation().getType());
    }

    @Test
    public void testSensorSetBean() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        Sensor s1 = m.provideSensor("IS1");
        s1.setUserName("Sensor 1");
        Sensor s2 = m.provideSensor("IS2");
        s2.setUserName("Sensor 2");
        Sensor s3 = m.provideSensor("IS3");
        s3.setUserName("Sensor 3");
        Sensor s4 = m.provideSensor("IS4");
        s4.setUserName("Sensor 4");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m, s1, NamedBeanComboBox.DisplayOptions.DISPLAYNAME);

        assertEquals("Sensor 1", t.getSelectedItemDisplayName());

        t.setSelectedItem(s2);
        assertEquals(s2, t.getSelectedItem());

        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.SYSTEMNAME);
        t.setSelectedItem(s3);
        assertEquals(s3, t.getSelectedItem());

        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.USERNAMESYSTEMNAME);
        t.setSelectedItem(s4);
        assertEquals(s4, t.getSelectedItem());

        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.SYSTEMNAMEUSERNAME);
        t.setSelectedItem(s3);
        assertEquals(s3, t.getSelectedItem());

        t.setDisplayOrder(NamedBeanComboBox.DisplayOptions.USERNAME);
        t.setSelectedItem(s2);
        assertEquals(s2, t.getSelectedItem());
    }

    @Test
    public void testSensorNameChange() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        Sensor s1 = m.provideSensor("IS1");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m, s1, NamedBeanComboBox.DisplayOptions.DISPLAYNAME);

        assertEquals("IS1", t.getSelectedItemDisplayName());

        s1.setUserName("Sensor 1");
        assertEquals("Sensor 1", t.getSelectedItemDisplayName());

        s1.setUserName("new name");
        assertEquals("new name", t.getSelectedItemDisplayName());
    }

    @Test
    public void testSensorAddTracking() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SensorManager m = InstanceManager.getDefault(jmri.SensorManager.class);
        Sensor s1 = m.provideSensor("IS1");
        s1.setUserName("Sensor 1");

        NamedBeanComboBox<Sensor> t = new NamedBeanComboBox<>(m, s1, NamedBeanComboBox.DisplayOptions.DISPLAYNAME);
        assertEquals(1, t.getItemCount());

        Sensor s2 = m.provideSensor("IS2");
        s2.setUserName(null);
        assertEquals(2, t.getItemCount());

        Sensor s3 = m.provideSensor("IS3");
        s3.setUserName("Sensor 3");
        assertEquals(3, t.getItemCount());
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.initInternalSensorManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(NamedBeanComboBoxTest.class);

}