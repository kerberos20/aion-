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
import skin.BaseRadioButtonUI;
import skin.ColorHelper;
import skin.BaseLookAndFeel;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * @author Michael Hagen
 */
public class HiFiRadioButtonUI extends BaseRadioButtonUI {

    private static HiFiRadioButtonUI radioButtonUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (radioButtonUI == null) {
            radioButtonUI = new HiFiRadioButtonUI();
        }
        return radioButtonUI;
    }

    protected void paintText(Graphics g, JComponent c, String text, Rectangle textRect) {
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            v.paint(g, textRect);
        } else {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            int mnemIndex = -1;
            if (Utilities.getJavaVersion() >= 1.4) {
                mnemIndex = b.getDisplayedMnemonicIndex();
            } else {
                mnemIndex = Utilities.findDisplayedMnemonicIndex(b.getText(), model.getMnemonic());
            }
            g.setFont(b.getFont());
            FontMetrics fm = Utilities.getFontMetrics(b, g, b.getFont());
            if (model.isEnabled()) {
                Color fc = b.getForeground();
                if (BaseLookAndFeel.getTheme().isTextShadowOn() && ColorHelper.getGrayValue(fc) > 128) {
                    g.setColor(Color.black);
                    Utilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x + 1, textRect.y + 1 + fm.getAscent());
                }
                g.setColor(fc);
                Utilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
            } else {
                g.setColor(Color.black);
                Utilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x + 1, textRect.y + 1 + fm.getAscent());
                g.setColor(BaseLookAndFeel.getDisabledForegroundColor());
                Utilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
            }
        }
    }

    public void paintBackground(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            if ((c.getBackground() instanceof ColorUIResource) && c.getBackground().equals(BaseLookAndFeel.getBackgroundColor())) {
                HiFiUtils.fillComponent(g, c);
            } else {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
        }
    }
}
