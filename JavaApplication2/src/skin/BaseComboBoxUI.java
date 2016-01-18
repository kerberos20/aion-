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

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class BaseComboBoxUI extends BasicComboBoxUI {

    private PropertyChangeListener propertyChangeListener = null;
    private FocusListener focusListener = null;
    private Border orgBorder = null;
    private Color orgBackgroundColor = null;

    public static ComponentUI createUI(JComponent c) {
        return new BaseComboBoxUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        comboBox.setRequestFocusEnabled(true);
        if (comboBox.getEditor() != null) {
            if (comboBox.getEditor().getEditorComponent() instanceof JTextField) {
                ((JTextField) (comboBox.getEditor().getEditorComponent())).setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
        }
    }

    protected void installListeners() {
        super.installListeners();
        propertyChangeListener = new PropertyChangeHandler();
        comboBox.addPropertyChangeListener(propertyChangeListener);

        if (BaseLookAndFeel.getTheme().doShowFocusFrame()) {
            focusListener = new FocusListener() {

                public void focusGained(FocusEvent e) {
                    if (comboBox != null) {
                        orgBorder = comboBox.getBorder();
                        orgBackgroundColor = comboBox.getBackground();
                        LookAndFeel laf = UIManager.getLookAndFeel();
                        if (laf instanceof BaseLookAndFeel) {
                            if (orgBorder instanceof UIResource) {
                                Border focusBorder = ((BaseLookAndFeel)laf).getBorderFactory().getFocusFrameBorder();
                                comboBox.setBorder(focusBorder);
                            }
                            Color backgroundColor = BaseLookAndFeel.getTheme().getFocusBackgroundColor();
                            comboBox.setBackground(backgroundColor);
                        }
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (comboBox != null) {
                        if (orgBorder instanceof UIResource) {
                            comboBox.setBorder(orgBorder);
                        }
                        comboBox.setBackground(orgBackgroundColor);
                    }
                }
            };
            comboBox.addFocusListener(focusListener);
        }
    }

    protected void uninstallListeners() {
        comboBox.removePropertyChangeListener(propertyChangeListener);
        comboBox.removeFocusListener(focusListener);
        propertyChangeListener = null;
        focusListener = null;
        super.uninstallListeners();
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension size = super.getPreferredSize(c);
        if (comboBox.getGraphics() != null) {
            FontMetrics fm = Utilities.getFontMetrics(comboBox, comboBox.getGraphics(), comboBox.getFont());
            size.height = fm.getHeight() + 2;
            if (UIManager.getLookAndFeel() instanceof BaseLookAndFeel) {
                BaseLookAndFeel laf = (BaseLookAndFeel)UIManager.getLookAndFeel();
                size.height = Math.max(size.height, laf.getIconFactory().getDownArrowIcon().getIconHeight() + 2);
            }
        }
        return new Dimension(size.width + 2, size.height + 2);
    }

    public JButton createArrowButton() {
        JButton button = new ArrowButton();
        if (Utilities.isLeftToRight(comboBox)) {
            Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, BaseLookAndFeel.getFrameColor());
            button.setBorder(border);
        } else {
            Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, BaseLookAndFeel.getFrameColor());
            button.setBorder(border);
        }
        return button;
    }

    protected void setButtonBorder() {
        if (Utilities.isLeftToRight(comboBox)) {
            Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, BaseLookAndFeel.getFrameColor());
            arrowButton.setBorder(border);
        } else {
            Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, BaseLookAndFeel.getFrameColor());
            arrowButton.setBorder(border);
        }
    }

    public class PropertyChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (name.equals("componentOrientation")) {
                setButtonBorder();
            }
        }
    }
//-----------------------------------------------------------------------------    

    public static class ArrowButton extends NoFocusButton {

        public void paint(Graphics g) {
            Dimension size = getSize();
            Color colors[];
            if (isEnabled()) {
                if (getModel().isArmed() && getModel().isPressed()) {
                    colors = BaseLookAndFeel.getTheme().getPressedColors();
                } else if (getModel().isRollover()) {
                    colors = BaseLookAndFeel.getTheme().getRolloverColors();
                } else {
                    colors = BaseLookAndFeel.getTheme().getButtonColors();
                }
            } else {
                colors = BaseLookAndFeel.getTheme().getDisabledColors();
            }
            Utilities.fillHorGradient(g, colors, 0, 0, size.width, size.height);
            
            boolean inverse = ColorHelper.getGrayValue(colors) < 128;
            
            Icon icon = inverse ? BaseIcons.getComboBoxInverseIcon() : BaseIcons.getComboBoxIcon();
            int x = (size.width - icon.getIconWidth()) / 2;
            int y = (size.height - icon.getIconHeight()) / 2;
            
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            if (getModel().isPressed() && getModel().isArmed()) {
                icon.paintIcon(this, g, x + 2, y + 1);
            } else {
                icon.paintIcon(this, g, x + 1, y);
            }
            g2D.setComposite(savedComposite);
            paintBorder(g2D);
            
        }
    }
}
