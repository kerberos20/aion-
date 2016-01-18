package skin;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class BaseBorders {

    protected static Border buttonBorder = null;
    protected static Border focusFrameBorder = null;
    protected static Border textFieldBorder = null;
    protected static Border spinnerBorder = null;
    protected static Border comboBoxBorder = null;
    protected static Border progressBarBorder = null;
    protected static Border tableHeaderBorder = null;
    protected static Border popupMenuBorder = null;
    protected static Border menuItemBorder = null;
    protected static Border toolBarBorder = null;
    protected static Border toolButtonBorder = null;
    protected static Border rolloverToolButtonBorder = null;
    protected static Border internalFrameBorder = null;
    protected static Border paletteBorder = null;
    protected static Border scrollPaneBorder = null;
    protected static Border tableScrollPaneBorder = null;
    protected static Border tabbedPaneBorder = null;
    protected static Border desktopIconBorder = null;

    public static void initDefaults() {
        buttonBorder = null;
        textFieldBorder = null;
        spinnerBorder = null;
        comboBoxBorder = null;
        progressBarBorder = null;
        tableHeaderBorder = null;
        popupMenuBorder = null;
        menuItemBorder = null;
        toolBarBorder = null;
        toolButtonBorder = null;
        rolloverToolButtonBorder = null;
        paletteBorder = null;
        internalFrameBorder = null;
        scrollPaneBorder = null;
        tableScrollPaneBorder = null;
        tabbedPaneBorder = null;
        desktopIconBorder = null;
    }

    //------------------------------------------------------------------------------------
    // Lazy access methods
    //------------------------------------------------------------------------------------
    public static Border getFocusFrameBorder() {
        if (focusFrameBorder == null) {
            focusFrameBorder = new FocusFrameBorder();
        }
        return focusFrameBorder;
    }
    
    public static Border getButtonBorder() {
        if (buttonBorder == null) {
            buttonBorder = new ButtonBorder();
        }
        return buttonBorder;
    }
    
    public static Border getRolloverToolButtonBorder() {
        if (rolloverToolButtonBorder == null) {
            rolloverToolButtonBorder = new RolloverToolButtonBorder();
        }
        return rolloverToolButtonBorder;
    }
    public static Border getToggleButtonBorder() {
        return getButtonBorder();
    }
    
    public static Border getInternalFrameBorder() {
        if (internalFrameBorder == null) {
            internalFrameBorder = new InternalFrameBorder();
        }
        return internalFrameBorder;
    }


    //------------------------------------------------------------------------------------
    // Lazy access methods
    //------------------------------------------------------------------------------------
    public static Border getTextBorder() {
        if (textFieldBorder == null) {
            textFieldBorder = new TextFieldBorder();
        }
        return textFieldBorder;
    }

    public static Border getSpinnerBorder() {
        if (spinnerBorder == null) {
            spinnerBorder = new SpinnerBorder();
        }
        return spinnerBorder;
    }

    public static Border getTextFieldBorder() {
        return getTextBorder();
    }

    public static Border getComboBoxBorder() {
        if (comboBoxBorder == null) {
            comboBoxBorder = new ComboBoxBorder();
        }
        return comboBoxBorder;
    }

    public static Border getProgressBarBorder() {
        if (progressBarBorder == null) {
            progressBarBorder = BorderFactory.createLineBorder(ColorHelper.darker(BaseLookAndFeel.getBackgroundColor(), 30));
        }
        return progressBarBorder;
    }

    public static Border getTableHeaderBorder() {
        if (tableHeaderBorder == null) {
            tableHeaderBorder = new TableHeaderBorder();
        }
        return tableHeaderBorder;
    }

    public static Border getPopupMenuBorder() {
        if (popupMenuBorder == null) {
            if (BaseLookAndFeel.getTheme().isMenuOpaque()) {
                popupMenuBorder = new BasePopupMenuBorder();
            } else {
                popupMenuBorder = new BasePopupMenuShadowBorder();
            }
        }
        return popupMenuBorder;
    }

    public static Border getMenuItemBorder() {
        if (menuItemBorder == null) {
            menuItemBorder = new MenuItemBorder();
        }
        return menuItemBorder;
    }

    public static Border getToolBarBorder() {
        if (toolBarBorder == null) {
            toolBarBorder = new ToolBarBorder();
        }
        return toolBarBorder;
    }

    public static Border getToolButtonBorder() {
        if (toolButtonBorder == null) {
            toolButtonBorder = new ToolButtonBorder();
        }
        return toolButtonBorder;
    }

    public static Border getMenuBarBorder() {
        return BorderFactory.createEmptyBorder(1, 1, 1, 1);
    }

    public static Border getPaletteBorder() {
        if (paletteBorder == null) {
            paletteBorder = new PaletteBorder();
        }
        return paletteBorder;
    }

    public static Border getScrollPaneBorder() {
        if (scrollPaneBorder == null) {
            scrollPaneBorder = new ScrollPaneBorder();
        }
        return scrollPaneBorder;
    }

    public static Border getTableScrollPaneBorder() {
        if (tableScrollPaneBorder == null) {
            tableScrollPaneBorder = new ScrollPaneBorder();
        }
        return tableScrollPaneBorder;
    }

    public static Border getTabbedPaneBorder() {
        if (tabbedPaneBorder == null) {
            tabbedPaneBorder = new TabbedPaneBorder();
        }
        return tabbedPaneBorder;
    }

    public static Border getDesktopIconBorder() {
        if (desktopIconBorder == null) {
            desktopIconBorder = new BorderUIResource.CompoundBorderUIResource(
                    new LineBorder(BaseLookAndFeel.getWindowBorderColor(), 1),
                    new MatteBorder(2, 2, 1, 2, BaseLookAndFeel.getWindowBorderColor()));
        }
        return desktopIconBorder;
    }

    //------------------------------------------------------------------------------------
    // Implementation of border classes
    //------------------------------------------------------------------------------------
    public static class FocusFrameBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color hiColor = ColorHelper.brighter(BaseLookAndFeel.getTheme().getFocusFrameColor(), 60);
            Color loColor = BaseLookAndFeel.getTheme().getFocusFrameColor();
            g.setColor(loColor);
            g.drawRect(x, y, width - 1, height - 1);
            g.setColor(hiColor);
            g.drawRect(x + 1, y + 1, width - 3, height - 3);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class FocusFrameBorder

    public static class TextFieldBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(BaseLookAndFeel.getTheme().getFrameColor());
            g.drawRect(x, y, width - 1, height - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class TextFieldBorder
    public static class InternalFrameBorder extends BaseInternalFrameBorder {

        public InternalFrameBorder() {
            insets.top = 3;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            boolean active = isActive(c);
            int th = getTitleHeight(c);
            Color titleColor = BaseLookAndFeel.getTheme().getWindowInactiveTitleColors()[0];
            Color borderColor = BaseLookAndFeel.getWindowInactiveTitleColorDark();
            Color frameColor = BaseLookAndFeel.getWindowInactiveBorderColor();
            if (active) {
                titleColor = BaseLookAndFeel.getTheme().getWindowTitleColors()[0];
                borderColor = BaseLookAndFeel.getWindowTitleColorDark();
                frameColor = BaseLookAndFeel.getWindowBorderColor();
            }
            g.setColor(titleColor);
            g.fillRect(x, y + 1, w, insets.top - 1);
            g.setColor(borderColor);
            g.fillRect(x + 1, y + h - dw, w - 2, dw - 1);
            if (active) {
                Utilities.fillHorGradient(g, BaseLookAndFeel.getTheme().getWindowTitleColors(), 1, insets.top, dw, th + 1);
                Utilities.fillHorGradient(g, BaseLookAndFeel.getTheme().getWindowTitleColors(), w - dw, insets.top, dw, th + 1);
            } else {
                Utilities.fillHorGradient(g, BaseLookAndFeel.getTheme().getWindowInactiveTitleColors(), 1, insets.top, dw - 1, th + 1);
                Utilities.fillHorGradient(g, BaseLookAndFeel.getTheme().getWindowInactiveTitleColors(), w - dw, insets.top, dw - 1, th + 1);
            }
            g.setColor(borderColor);
            g.fillRect(1, insets.top + th + 1, dw - 1, h - th - dw);
            g.fillRect(w - dw, insets.top + th + 1, dw - 1, h - th - dw);
            g.setColor(frameColor);
            g.drawRect(x, y, w - 1, h - 1);
        }
    }
    public static class SpinnerBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(BaseLookAndFeel.getTheme().getFrameColor());
            g.drawRect(x, y, width - 1, height - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class SpinnerBorder

    public static class ComboBoxBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(BaseLookAndFeel.getTheme().getFrameColor());
            g.drawRect(x, y, width - 1, height - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class ComboBoxBorder

    public static class TableHeaderBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 0);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D)g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
            g2D.setComposite(alpha);
            Color cHi = BaseLookAndFeel.getTheme().getControlHighlightColor();
            Color cLo = BaseLookAndFeel.getTheme().getControlShadowColor();
            Utilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
            g2D.setComposite(savedComposite);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class TableHeaderBorder

    public static class ScrollPaneBorder implements Border, UIResource {

        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color frameColor = BaseLookAndFeel.getTheme().getFrameColor();
            Utilities.draw3DBorder(g, frameColor, ColorHelper.brighter(frameColor, 10), x, y, w, h);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(INSETS.top, INSETS.left, INSETS.bottom, INSETS.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = INSETS.left;
            borderInsets.top = INSETS.top;
            borderInsets.right = INSETS.right;
            borderInsets.bottom = INSETS.bottom;
            return borderInsets;
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    } // class ScrollPaneBorder

    public static class BasePopupMenuBorder extends AbstractBorder implements UIResource {

        protected static Font logoFont;
        protected static Insets leftLogoInsets;
        protected static Insets rightLogoInsets;
        protected static Insets insets;
        protected static int shadowSize;

        public BasePopupMenuBorder() {
            logoFont = new Font("Dialog", Font.BOLD, 12);
            leftLogoInsets = new Insets(2, 18, 1, 1);
            rightLogoInsets = new Insets(2, 2, 1, 18);
            insets = new Insets(2, 1, 1, 1);
            shadowSize = 0;
        }
        
        public boolean isMenuBarPopup(Component c) {
            boolean menuBarPopup = false;
            if (c instanceof JPopupMenu) {
                JPopupMenu pm = (JPopupMenu) c;
                if (pm.getInvoker() != null) {
                    menuBarPopup = (pm.getInvoker().getParent() instanceof JMenuBar);
                }
            }
            return menuBarPopup;
        }

        public boolean hasLogo(Component c) {
            return ((BaseLookAndFeel.getTheme().getLogoString() != null) && (BaseLookAndFeel.getTheme().getLogoString().length() > 0));
        }

        public Color getLogoColorHi() {
            return Color.white;
        }
        
        public Color getLogoColorLo() {
            return ColorHelper.darker(BaseLookAndFeel.getTheme().getMenuSelectionBackgroundColor(), 20);
        }
        
        public void paintLogo(Component c, Graphics g, int x, int y, int w, int h) {
            if (hasLogo(c)) {
                Graphics2D g2D = (Graphics2D)g;
                
                Font savedFont = g2D.getFont();
                g.setFont(logoFont);
                FontMetrics fm = Utilities.getFontMetrics((JComponent)c, g, c.getFont());
                String logo = Utilities.getClippedText(BaseLookAndFeel.getTheme().getLogoString(), fm, h - 16);

                AffineTransform savedTransform = g2D.getTransform();
                
                Color fc = getLogoColorHi();
                Color bc = getLogoColorLo();
                
                if (Utilities.isLeftToRight(c)) {
                    g2D.translate(fm.getAscent() + 1, h - shadowSize - 4);
                    g2D.rotate(Math.toRadians(-90));
                    g2D.setColor(bc);
                    Utilities.drawString((JComponent)c, g, logo, 0, 1);
                    g2D.setColor(fc);
                    Utilities.drawString((JComponent)c, g, logo, 1, 0);
                } else {
                    g2D.translate(w - shadowSize - 4, h - shadowSize - 4);
                    g2D.rotate(Math.toRadians(-90));
                    g2D.setColor(bc);
                    Utilities.drawString((JComponent)c, g, logo, 0, 1);
                    g2D.setColor(fc);
                    Utilities.drawString((JComponent)c, g, logo, 1, 0);
                }
                
                g2D.setTransform(savedTransform);
                g2D.setFont(savedFont);
            }
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color logoColor = BaseLookAndFeel.getMenuSelectionBackgroundColor();
            Color borderColorLo = BaseLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(BaseLookAndFeel.getMenuSelectionBackgroundColor(), 40);
            g.setColor(logoColor);
            if (Utilities.isLeftToRight(c)) {
                int dx = getBorderInsets(c).left;
                g.fillRect(x, y, dx - 1, h - 1);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(BaseLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + dx, y + 1, x + w - 2, y + 1);
                g.setColor(borderColorHi);
                g.drawLine(x + 1, y, x + 1, y + h - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x + dx - 1, y, x + w, y);
                    // left
                    g.drawLine(x, y, x, y + h - 1);
                    // bottom
                    g.drawLine(x, y + h - 1, x + w, y + h - 1);
                    // right
                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
                } else {
                    g.drawRect(x, y, w - 1, h - 1);
                }
                // - logo separator
                g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - 1);
            } else {
                int dx = getBorderInsets(c).right;
                g.fillRect(x + w - dx, y, dx, h - 1);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(BaseLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + 1, y + 1, x + w - dx - 1, y + 1);
                g.drawLine(x + 1, y + 1, x + 1, y + h - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x, y, x + w - dx, y);
                    // left
                    g.drawLine(x, y, x, y + h - 1);
                    // bottom
                    g.drawLine(x, y + h - 1, x + w, y + h - 1);
                    // right
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
                } else {
                    g.drawRect(x, y, w - 1, h - 1);
                }
                // - logo separator
                g.drawLine(x + w - dx, y + 1, x + w - dx, y + h - 1);
            }
        }

        public Insets getBorderInsets(Component c) {
            if (hasLogo(c)) {
                if (Utilities.isLeftToRight(c)) {
                    return new Insets(leftLogoInsets.top, leftLogoInsets.left, leftLogoInsets.bottom + shadowSize, leftLogoInsets.right + shadowSize);
                } else {
                    return new Insets(rightLogoInsets.top, rightLogoInsets.left, rightLogoInsets.bottom + shadowSize, rightLogoInsets.right + shadowSize);
                }
            } else {
                return new Insets(insets.top, insets.left, insets.bottom + shadowSize, insets.right + shadowSize);
            }
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets ins = getBorderInsets(c);
            borderInsets.left = ins.left;
            borderInsets.top = ins.top;
            borderInsets.right = ins.right;
            borderInsets.bottom = ins.bottom;
            return borderInsets;
        }

    } // class PopupMenuBorder

    public static class BasePopupMenuShadowBorder extends BasePopupMenuBorder {

        public BasePopupMenuShadowBorder() {
            shadowSize = 4;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, BaseLookAndFeel.getTheme().getMenuAlpha());
            g2D.setComposite(alpha);
            Color logoColor = BaseLookAndFeel.getTheme().getMenuSelectionBackgroundColor();
            Color borderColorLo = BaseLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(BaseLookAndFeel.getMenuSelectionBackgroundColor(), 40);
            g.setColor(logoColor);
            if (Utilities.isLeftToRight(c)) {
                int dx = getBorderInsets(c).left;
                g.fillRect(x, y, dx - 1, h - 1 - shadowSize);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(BaseLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + dx, y + 1, x + w - shadowSize - 2, y + 1);
                g.setColor(borderColorHi);
                g.drawLine(x + 1, y, x + 1, y + h - shadowSize - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x + dx - 1, y, x + w - shadowSize - 1, y);
                    // left
                    g.drawLine(x, y, x, y + h - shadowSize - 1);
                    // bottom
                    g.drawLine(x, y + h - shadowSize - 1, x + w - shadowSize - 1, y + h - shadowSize - 1);
                    // right
                    g.drawLine(x + w - shadowSize - 1, y + 1, x + w - shadowSize - 1, y + h - shadowSize - 1);
                } else {
                    g.drawRect(x, y, w - shadowSize - 1, h - shadowSize - 1);
                }
                // - logo separator
                g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - shadowSize - 1);
            } else {
                int dx = getBorderInsets(c).right - shadowSize;
                g.fillRect(x + w - dx - shadowSize, y, dx - 1, h - 1 - shadowSize);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(BaseLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + 1, y + 1, x + w - dx - shadowSize - 1, y + 1);
                g.drawLine(x + 1, y + 1, x + 1, y + h - shadowSize - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x, y, x + w - dx - shadowSize, y);
                    // left
                    g.drawLine(x, y, x, y + h - shadowSize - 1);
                    // bottom
                    g.drawLine(x, y + h - shadowSize - 1, x + w - shadowSize - 1, y + h - shadowSize - 1);
                    // right
                    g.drawLine(x + w - shadowSize - 1, y, x + w - shadowSize - 1, y + h - shadowSize - 1);
                } else {
                    g.drawRect(x, y, w - shadowSize - 1, h - shadowSize - 1);
                }
                // - logo separator
                g.drawLine(x + w - dx - shadowSize, y + 1, x + w - dx - shadowSize, y + h - shadowSize - 1);
            }

            // paint the shadow
            g2D.setColor(BaseLookAndFeel.getTheme().getShadowColor());
            float alphaValue = 0.4f;
            for (int i = 0; i < shadowSize; i++) {
                alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
                g2D.setComposite(alpha);
                g.drawLine(x + w - shadowSize + i, y + shadowSize, x + w - shadowSize + i, y + h - shadowSize - 1 + i);
                g.drawLine(x + shadowSize, y + h - shadowSize + i, x + w - shadowSize + i, y + h - shadowSize + i);
                alphaValue -= (alphaValue / 2);
            }

            g2D.setComposite(savedComposite);
        }
        
    } // class PopupMenuShadowBorder
    public static class RolloverToolButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            Color c1 = null;
            Color c2 = null;
            if (Utilities.isActive((JComponent) c)) {
                c1 = ColorHelper.brighter(BaseLookAndFeel.getFrameColor(), 60);
                c2 = BaseLookAndFeel.getFrameColor();
            } else {
                c1 = BaseLookAndFeel.getFrameColor();
                c2 = ColorHelper.darker(BaseLookAndFeel.getFrameColor(), 20);
            }
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2D.setComposite(alpha);
            Utilities.draw3DBorder(g, c1, c2, 0, 0, w, h);
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            g2D.setComposite(alpha);
            Utilities.draw3DBorder(g, c2, c1, 1, 1, w - 2, h - 2);
            g2D.setComposite(composite);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    }
        public static class TabbedPaneBorder implements Border, UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color frameColor = BaseLookAndFeel.getTheme().getFrameColor();
            Utilities.draw3DBorder(g, frameColor, ColorHelper.brighter(frameColor, 10), x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    }
    public static class MenuItemBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JMenuItem b = (JMenuItem) c;
            ButtonModel model = b.getModel();
            Color borderColorLo = BaseLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(BaseLookAndFeel.getMenuSelectionBackgroundColor(), 40);
            if (c.getParent() instanceof JMenuBar) {
                if (model.isArmed() || model.isSelected()) {
                    g.setColor(borderColorLo);
                    g.drawLine(x, y, x + w - 1, y);
                    g.drawLine(x, y, x, y + h - 1);
                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
                    g.setColor(borderColorHi);
                    g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
                    g.drawLine(x + 1, y + 1, x + 1, y + h - 1);
                }
            } else {
                if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                    g.setColor(borderColorLo);
                    g.drawLine(x, y, x + w - 1, y);
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                    g.setColor(borderColorHi);
                    g.drawLine(x, y + 1, x + w - 2, y + 1);
                }
            }
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class MenuItemBorder

    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {

        private static final LazyImageIcon HOR_RUBBER_ICON = new LazyImageIcon("hifi/icons/HorRubber.gif");
        private static final LazyImageIcon VER_RUBBER_ICON = new LazyImageIcon("hifi/icons/VerRubber.gif");

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (((JToolBar) c).isFloatable()) {
                if (((JToolBar) c).getOrientation() == HORIZONTAL) {
                    int x1 = 4;
                    int y1 = (h - HOR_RUBBER_ICON.getIconHeight()) / 2;
                    HOR_RUBBER_ICON.paintIcon(c, g, x1, y1);
                } else {
                    int x1 = (w - VER_RUBBER_ICON.getIconWidth()) / 2 + 2;
                    int y1 = 4;
                    VER_RUBBER_ICON.paintIcon(c, g, x1, y1);
                }
            }
        }

        public Insets getBorderInsets(Component c) {
            Insets insets = new Insets(2, 2, 2, 2);
            if (((JToolBar) c).isFloatable()) {
                if (((JToolBar) c).getOrientation() == HORIZONTAL) {
                    if (Utilities.isLeftToRight(c)) {
                        insets.left = 15;
                    } else {
                        insets.right = 15;
                    }
                } else {
                    insets.top = 15;
                }
            }
            Insets margin = ((JToolBar) c).getMargin();
            if (margin != null) {
                insets.left += margin.left;
                insets.top += margin.top;
                insets.right += margin.right;
                insets.bottom += margin.bottom;
            }
            return insets;
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets insets = getBorderInsets(c);
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }
    } // class ToolBarBorder
    public static class ButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(4, 8, 4, 8);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            g.translate(x, y);
            
            Color hiFrameColor = ColorHelper.brighter(BaseLookAndFeel.getTheme().getButtonBackgroundColor(), 14);
            Color frameColor = ColorHelper.brighter(BaseLookAndFeel.getTheme().getButtonBackgroundColor(), 6);
            Color loFrameColor = ColorHelper.darker(BaseLookAndFeel.getTheme().getButtonBackgroundColor(), 50);
            
            g.setColor(hiFrameColor);
            g.drawLine(1, 0, w - 3, 0);
            g.drawLine(0, 1, 0, h - 3);
            g.setColor(frameColor);
            g.drawLine(w - 2, 0, w - 2, h - 2);
            g.drawLine(1, h - 2, w - 3, h - 2);

            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2D.setComposite(alpha);
            g2D.setColor(loFrameColor);
            g.drawLine(1, 1, w - 3, 1);
            g.drawLine(1, 2, 1, h - 3);
            g.setColor(Color.black);
            g.drawLine(w - 1, 1, w - 1, h - 1);
            g.drawLine(1, h - 1, w - 1, h - 1);
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            g2D.setComposite(alpha);
            g.drawLine(1, h - 2, 2, h - 1);
            g2D.setComposite(composite);

            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    }
    
    public static class ToolButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Color frameColor = BaseLookAndFeel.getToolbarBackgroundColor();
            Color frameHiColor = ColorHelper.brighter(frameColor, 10);
            Color frameLoColor = ColorHelper.darker(frameColor, 30);
            Utilities.draw3DBorder(g, frameHiColor, frameLoColor, x, y, w, h);
            if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                Utilities.draw3DBorder(g, frameLoColor, frameHiColor, x, y, w, h);
            } else {
                Utilities.draw3DBorder(g, frameLoColor, frameHiColor, x, y, w, h);
                Utilities.draw3DBorder(g, frameHiColor, frameLoColor, x + 1, y + 1, w - 2, h - 2);
            }
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class ToolButtonBorder

    public static class PaletteBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (Utilities.isFrameActive((JComponent) c)) {
                g.setColor(BaseLookAndFeel.getWindowBorderColor());
            } else {
                g.setColor(BaseLookAndFeel.getWindowInactiveBorderColor());
            }
            g.drawRect(x, y, w - 1, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class PaletteBorder

    public static class BaseInternalFrameBorder extends AbstractBorder implements UIResource {

        protected final int dw = 5;
        protected final int trackWidth = 22;
        protected final Insets insets = new Insets(dw, dw, dw, dw);
        protected final Insets paletteInsets = new Insets(3, 3, 3, 3);

        public BaseInternalFrameBorder() {
        }

        public boolean isResizable(Component c) {
            boolean resizable = true;
            if (c instanceof JDialog) {
                JDialog dialog = (JDialog) c;
                resizable = dialog.isResizable();
            } else if (c instanceof JInternalFrame) {
                JInternalFrame frame = (JInternalFrame) c;
                resizable = frame.isResizable();
            } else if (c instanceof JRootPane) {
                JRootPane jp = (JRootPane) c;
                if (jp.getParent() instanceof JFrame) {
                    JFrame frame = (JFrame) c.getParent();
                    resizable = frame.isResizable();
                } else if (jp.getParent() instanceof JDialog) {
                    JDialog dialog = (JDialog) c.getParent();
                    resizable = dialog.isResizable();
                }
            }
            return resizable;
        }

        public boolean isActive(Component c) {
            if (c instanceof JComponent) {
                return Utilities.isActive((JComponent)c);
            } else {
                return true;
            }
        }

        public int getTitleHeight(Component c) {
            int th = 21;
            int fh = getBorderInsets(c).top + getBorderInsets(c).bottom;
            if (c instanceof JDialog) {
                JDialog dialog = (JDialog) c;
                th = dialog.getSize().height - dialog.getContentPane().getSize().height - fh - 1;
                if (dialog.getJMenuBar() != null) {
                    th -= dialog.getJMenuBar().getSize().height;
                }
            } else if (c instanceof JInternalFrame) {
                JInternalFrame frame = (JInternalFrame) c;
                th = frame.getSize().height - frame.getRootPane().getSize().height - fh - 1;
                if (frame.getJMenuBar() != null) {
                    th -= frame.getJMenuBar().getSize().height;
                }
            } else if (c instanceof JRootPane) {
                JRootPane jp = (JRootPane) c;
                if (jp.getParent() instanceof JFrame) {
                    JFrame frame = (JFrame) c.getParent();
                    th = frame.getSize().height - frame.getContentPane().getSize().height - fh - 1;
                    if (frame.getJMenuBar() != null) {
                        th -= frame.getJMenuBar().getSize().height;
                    }
                } else if (jp.getParent() instanceof JDialog) {
                    JDialog dialog = (JDialog) c.getParent();
                    th = dialog.getSize().height - dialog.getContentPane().getSize().height - fh - 1;
                    if (dialog.getJMenuBar() != null) {
                        th -= dialog.getJMenuBar().getSize().height;
                    }
                }
            }
            return th;
        }

        public Insets getBorderInsets(Component c) {
            if (isResizable(c)) {
                return new Insets(insets.top, insets.left, insets.bottom, insets.right);
            } else {
                return new Insets(paletteInsets.top, paletteInsets.left, paletteInsets.bottom, paletteInsets.right);
            }
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets ins = getBorderInsets(c);
            borderInsets.left = ins.left;
            borderInsets.top = ins.top;
            borderInsets.right = ins.right;
            borderInsets.bottom = ins.bottom;
            return borderInsets;
        }

    } // class BaseInternalFrameBorder

    public static class Down3DBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color frameColor = BaseLookAndFeel.getTheme().getBackgroundColor();
            Utilities.draw3DBorder(g, ColorHelper.darker(frameColor, 20), ColorHelper.brighter(frameColor, 80), x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class Down3DBorder
    
} // class BaseBorders
