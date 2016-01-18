
package skin;


import skin.BaseBorders;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class BaseiBorderFactory {

    private static BaseiBorderFactory instance = null;

    private BaseiBorderFactory() {
    }

    public static synchronized BaseiBorderFactory getInstance() {
        if (instance == null) {
            instance = new BaseiBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return BaseBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return BaseBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return BaseBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return BaseBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return BaseBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return BaseBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return BaseBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return BaseBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return BaseBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return BaseBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return BaseBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return BaseBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return BaseBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return BaseBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return BaseBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return BaseBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return BaseBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return BaseBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return BaseBorders.getDesktopIconBorder();
    }
}

