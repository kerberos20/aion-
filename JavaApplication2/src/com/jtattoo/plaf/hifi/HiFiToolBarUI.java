
 
package com.jtattoo.plaf.hifi;

import skin.BaseToolBarUI;
import static skin.BaseBorders.getRolloverToolButtonBorder;
import static skin.BaseBorders.getToolButtonBorder;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class HiFiToolBarUI extends BaseToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new HiFiToolBarUI();
    }

    public Border getRolloverBorder() {
        return getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return true;
    }

    public void paint(Graphics g, JComponent c) {
        HiFiUtils.fillComponent(g, c);
    }
}

