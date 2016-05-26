// PrintOptionAction.java
package jmri.jmrit.operations.setup;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Swing action to load the print options.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2009
 * @version $Revision$
 */
public class PrintOptionAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 3517810572798192308L;

    public PrintOptionAction() {
        this(Bundle.getMessage("TitlePrintOptions"));
    }

    public PrintOptionAction(String s) {
        super(s);
    }

    PrintOptionFrame f = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (f == null || !f.isVisible()) {
            f = new PrintOptionFrame();
            f.initComponents();
        }
        f.setExtendedState(Frame.NORMAL);
        f.setVisible(true); // this also brings the frame into focus
    }
}

/* @(#)PrintOptionAction.java */
