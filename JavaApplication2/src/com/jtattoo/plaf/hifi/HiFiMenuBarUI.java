package com.jtattoo.plaf.hifi;

import static skin.BaseBorders.getMenuBarBorder;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;


/**
 * @author Michael Hagen
 */
public class HiFiMenuBarUI extends BasicMenuBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new HiFiMenuBarUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        if ((c != null) && (c instanceof JMenuBar)) {
            ((JMenuBar) c).setBorder(getMenuBarBorder());
        }
    }

    public void paint(Graphics g, JComponent c) {
        HiFiUtils.fillComponent(g, c);
    }
}