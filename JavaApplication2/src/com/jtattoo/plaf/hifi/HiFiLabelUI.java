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
 
package com.jtattoo.plaf.hifi;

import skin.Utilities;
import skin.ColorHelper;
import skin.BaseLookAndFeel;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * @author Michael Hagen
 */
public class HiFiLabelUI extends BasicLabelUI {

    private static HiFiLabelUI hifiLabelUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (hifiLabelUI == null) {
            hifiLabelUI = new HiFiLabelUI();
        }
        return hifiLabelUI;
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex = -1;
        if (Utilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = Utilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        Color fc = l.getForeground();
        if (BaseLookAndFeel.getTheme().isTextShadowOn() && ColorHelper.getGrayValue(fc) > 128) {
            g.setColor(Color.black);
            Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX + 1, textY + 1);
        }
        g.setColor(fc);
        Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex = -1;
        if (Utilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = Utilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        g.setColor(Color.black);
        Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX + 1, textY + 1);
        g.setColor(BaseLookAndFeel.getDisabledForegroundColor());
        Utilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }
}

