/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/

package skin;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * @author Michael Hagen
 */
public class BaseLabelUI extends BasicLabelUI {

    private static BaseLabelUI baseLabelUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (baseLabelUI == null) {
            baseLabelUI = new BaseLabelUI();
        }
        return baseLabelUI;
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex;
        if (Utilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = Utilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        Object sc = l.getClientProperty("shadowColor");
        if (sc instanceof Color) {
            g.setColor((Color)sc);
            Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY + 1);
        }
        g.setColor(l.getForeground());
        Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex;
        if (Utilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = Utilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        g.setColor(Color.white);
        Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX + 1, textY + 1);
        g.setColor(BaseLookAndFeel.getDisabledForegroundColor());
        Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }
}

