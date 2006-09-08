// BDL16Action.java

package jmri.jmrix.loconet.bdl16;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

import jmri.jmrix.loconet.LnTrafficController;


/**
 * Create and register a BDL16Frame object.
 *
 * @author	Bob Jacobsen    Copyright (C) 2002
 * @version	$Revision: 1.4 $
 */
public class BDL16Action 			extends AbstractAction {

    public BDL16Action(String s) { super(s);}
    public BDL16Action() {
        this("BDL16 programmer");
    }

    public void actionPerformed(ActionEvent e) {
        // create a BDL16Frame
        BDL16Frame f = new BDL16Frame();
        f.setVisible(true);
    }
}

/* @(#)BDL16Action.java */
