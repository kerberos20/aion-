
package skin;

import skin.BaseCheckBoxMenuItemUI;
import skin.BaseButtonUI;
import skin.BaseDesktopPaneUI;
import skin.BaseComboBoxUI;
import skin.BaseTheme;
import skin.BaseBorders;
import skin.BaseEditorPaneUI;
import com.jtattoo.plaf.hifi.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 * @author Michael Hagen
 */
public class BaseLookAndFeel extends MetalLookAndFeel {

    private static HiFiDefaultTheme myTheme = null;
    private static final ArrayList themesList = new ArrayList();
    private static final HashMap themesMap = new HashMap();
    private static final Properties defaultProps = new Properties();
    private static final Properties smallFontProps = new Properties();
    private static final Properties largeFontProps = new Properties();
    private static final Properties giantFontProps = new Properties();
    static {
        smallFontProps.setProperty("controlTextFont", "Dialog bold 10");
        smallFontProps.setProperty("systemTextFont", "Dialog bold 10");
        smallFontProps.setProperty("userTextFont", "Dialog 10");
        smallFontProps.setProperty("menuTextFont", "Dialog bold 10");
        smallFontProps.setProperty("windowTitleFont", "Dialog bold 10");
        smallFontProps.setProperty("subTextFont", "Dialog 8");

        largeFontProps.setProperty("controlTextFont", "Dialog bold 14");
        largeFontProps.setProperty("systemTextFont", "Dialog bold 14");
        largeFontProps.setProperty("userTextFont", "Dialog bold 14");
        largeFontProps.setProperty("menuTextFont", "Dialog bold 14");
        largeFontProps.setProperty("windowTitleFont", "Dialog bold 14");
        largeFontProps.setProperty("subTextFont", "Dialog 12");

        giantFontProps.setProperty("controlTextFont", "Dialog 18");
        giantFontProps.setProperty("systemTextFont", "Dialog 18");
        giantFontProps.setProperty("userTextFont", "Dialog 18");
        giantFontProps.setProperty("menuTextFont", "Dialog 18");
        giantFontProps.setProperty("windowTitleFont", "Dialog 18");
        giantFontProps.setProperty("subTextFont", "Dialog 16");

        themesList.add("Default");
        themesList.add("Small-Font");
        themesList.add("Large-Font");
        themesList.add("Giant-Font");

        themesMap.put("Default", defaultProps);
        themesMap.put("Small-Font", smallFontProps);
        themesMap.put("Large-Font", largeFontProps);
        themesMap.put("Giant-Font", giantFontProps);
    }

    protected static String currentThemeName;


    public BaseiBorderFactory getBorderFactory() {
        return BaseiBorderFactory.getInstance();
    }

    public BaseIconFactory getIconFactory() {
        return BaseIconFactory.getInstance();
    }

    protected void initSystemColorDefaults(UIDefaults table) {
        Object[] systemColors = {
            "desktop", getDesktopColor(), // Color of the desktop background

            "activeCaption", getWindowTitleBackgroundColor(), // Color for captions (title bars) when they are active.
            "activeCaptionLight", getWindowTitleColorLight(),
            "activeCaptionDark", getWindowTitleColorDark(),
            "activeCaptionText", getWindowTitleForegroundColor(), // Text color for text in captions (title bars).
            "activeCaptionBorder", getWindowBorderColor(), // Border color for caption (title bar) window borders.

            "inactiveCaption", getWindowInactiveTitleBackgroundColor(), // Color for captions (title bars) when not active.
            "inactiveCaptionLight", getWindowInactiveTitleColorLight(), //
            "inactiveCaptionDark", getWindowInactiveTitleColorDark(), //
            "inactiveCaptionText", getWindowInactiveTitleForegroundColor(), // Text color for text in inactive captions (title bars).
            "inactiveCaptionBorder", getWindowInactiveBorderColor(), // Border color for inactive caption (title bar) window borders.

            "window", getInputBackgroundColor(), // Default color for the interior of windows, list, tree etc
            "windowBorder", getBackgroundColor(), // ???
            "windowText", getControlForegroundColor(), // ???

            "menu", getMenuBackgroundColor(), // Background color for menus
            "menuText", getMenuForegroundColor(), // Text color for menus
            "MenuBar.rolloverEnabled", Boolean.TRUE,
            "text", getBackgroundColor(), // Text background color
            "textText", getControlForegroundColor(), // Text foreground color
            "textHighlight", getSelectionBackgroundColor(), // Text background color when selected
            "textHighlightText", getSelectionForegroundColor(), // Text color when selected
            "textInactiveText", getDisabledForegroundColor(), // Text color when disabled

            "control", getControlBackgroundColor(), // Default color for controls (buttons, sliders, etc)
            "controlText", getControlForegroundColor(), // Default color for text in controls
            "controlHighlight", getControlHighlightColor(), // Specular highlight (opposite of the shadow)
            "controlLtHighlight", getControlHighlightColor(), // Highlight color for controls
            "controlShadow", getControlShadowColor(), // Shadow color for controls
            "controlDkShadow", getControlDarkShadowColor(), // Dark shadow color for controls

            "scrollbar", getControlBackgroundColor(), // Scrollbar background (usually the "track")
            "info", getTooltipBackgroundColor(), // ToolTip Background
            "infoText", getTooltipForegroundColor() // ToolTip Text
        };

        for (int i = 0; i < systemColors.length; i += 2) {
            table.put((String) systemColors[i], systemColors[i + 1]);
        }
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        BaseBorders.initDefaults();
        BaseIcons.initDefaults();

        Object textFieldBorder = getBorderFactory().getTextFieldBorder();
        Object comboBoxBorder = getBorderFactory().getComboBoxBorder();
        Object scrollPaneBorder = getBorderFactory().getScrollPaneBorder();
        Object tableScrollPaneBorder = getBorderFactory().getTableScrollPaneBorder();
        Object tabbedPaneBorder = getBorderFactory().getTabbedPaneBorder();
        Object buttonBorder = getBorderFactory().getButtonBorder();
        Object toggleButtonBorder = getBorderFactory().getToggleButtonBorder();
        Object titledBorderBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{getFrameColor()});
        Object menuBarBorder = getBorderFactory().getMenuBarBorder();
        Object popupMenuBorder = getBorderFactory().getPopupMenuBorder();
        Object menuItemBorder = getBorderFactory().getMenuItemBorder();
        Object toolBarBorder = getBorderFactory().getToolBarBorder();
        Object progressBarBorder = getBorderFactory().getProgressBarBorder();
        Object toolTipBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{getFrameColor()});
        Object focusCellHighlightBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{getFocusCellColor()});
        Object optionPaneBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        Object optionPaneMessageAreaBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        Object optionPaneButtonAreaBorder = BorderFactory.createEmptyBorder(0, 8, 8, 8);
        Object windowBorder = getBorderFactory().getInternalFrameBorder();

        Color c = getBackgroundColor();
        ColorUIResource progressBarBackground = new ColorUIResource(ColorHelper.brighter(c, 20));

        // DEFAULTS TABLE
        Object[] defaults = {
            "controlTextFont", getControlTextFont(),
            "systemTextFont ", getSystemTextFont(),
            "userTextFont", getUserTextFont(),
            "menuTextFont", getMenuTextFont(),
            "windowTitleFont", getWindowTitleFont(),
            "subTextFont", getSubTextFont(),
            "Label.font", getUserTextFont(),
            "Label.background", getBackgroundColor(),
            "Label.foreground", getForegroundColor(),
            "Label.disabledText", getDisabledForegroundColor(),
            "Label.disabledShadow", getWhite(),
            // Text (Note: many are inherited)
            "TextField.border", textFieldBorder,
            "TextField.foreground", getInputForegroundColor(),
            "TextField.background", getInputBackgroundColor(),
            "TextField.disabledForeground", getDisabledForegroundColor(),
            "TextField.disabledBackground", getDisabledBackgroundColor(),
            "TextField.inactiveForeground", getDisabledForegroundColor(),
            "TextField.inactiveBackground", getDisabledBackgroundColor(),
            "TextArea.foreground", getInputForegroundColor(),
            "TextArea.background", getInputBackgroundColor(),
            "TextArea.disabledForeground", getDisabledForegroundColor(),
            "TextArea.disabledBackground", getDisabledBackgroundColor(),
            "TextArea.inactiveForeground", getDisabledForegroundColor(),
            "TextArea.inactiveBackground", getDisabledBackgroundColor(),
            "EditorPane.foreground", getInputForegroundColor(),
            "EditorPane.background", getInputBackgroundColor(),
            "EditorPane.disabledForeground", getDisabledForegroundColor(),
            "EditorPane.disabledBackground", getDisabledBackgroundColor(),
            "EditorPane.inactiveForeground", getDisabledForegroundColor(),
            "EditorPane.inactiveBackground", getDisabledBackgroundColor(),
            "FormattedTextField.border", textFieldBorder,
            "FormattedTextField.foreground", getInputForegroundColor(),
            "FormattedTextField.background", getInputBackgroundColor(),
            "FormattedTextField.disabledForeground", getDisabledForegroundColor(),
            "FormattedTextField.disabledBackground", getDisabledBackgroundColor(),
            "FormattedTextField.inactiveForeground", getDisabledForegroundColor(),
            "FormattedTextField.inactiveBackground", getDisabledBackgroundColor(),
            "PasswordField.border", textFieldBorder,
            "PasswordField.foreground", getInputForegroundColor(),
            "PasswordField.background", getInputBackgroundColor(),
            "PasswordField.disabledForeground", getDisabledForegroundColor(),
            "PasswordField.disabledBackground", getDisabledBackgroundColor(),
            "PasswordField.inactiveForeground", getDisabledForegroundColor(),
            "PasswordField.inactiveBackground", getDisabledBackgroundColor(),
            // Buttons
            "Button.background", getButtonBackgroundColor(),
            "Button.foreground", getButtonForegroundColor(),
            "Button.disabledText", getDisabledForegroundColor(),
            "Button.disabledShadow", getWhite(),
            "Button.select", getSelectionBackgroundColor(),
            "Button.border", buttonBorder,
            "Button.frame", getFrameColor(),
            "Button.focus", getFocusColor(),
            "Button.rolloverColor", getTheme().getRolloverColor(),
            "Button.rolloverForeground", getTheme().getRolloverForegroundColor(),
            "CheckBox.font", getUserTextFont(),
            "CheckBox.background", getBackgroundColor(),
            "CheckBox.foreground", getForegroundColor(),
            "CheckBox.disabledText", getDisabledForegroundColor(),
            "CheckBox.disabledShadow", getWhite(),
            "Checkbox.select", getSelectionBackgroundColor(),
            "CheckBox.focus", getFocusColor(),
            "CheckBox.icon", getIconFactory().getCheckBoxIcon(),
            "RadioButton.font", getUserTextFont(),
            "RadioButton.background", getBackgroundColor(),
            "RadioButton.foreground", getForegroundColor(),
            "RadioButton.disabledText", getDisabledForegroundColor(),
            "RadioButton.disabledShadow", getWhite(),
            "RadioButton.select", getSelectionBackgroundColor(),
            "RadioButton.icon", getIconFactory().getRadioButtonIcon(),
            "RadioButton.focus", getFocusColor(),
            "ToggleButton.background", getButtonBackgroundColor(),
            "ToggleButton.foreground", getButtonForegroundColor(),
            "ToggleButton.select", getSelectionBackgroundColor(),
            "ToggleButton.text", getButtonForegroundColor(),
            "ToggleButton.disabledText", getDisabledForegroundColor(),
            "ToggleButton.disabledShadow", getWhite(),
            "ToggleButton.disabledSelectedText", getDisabledForegroundColor(),
            "ToggleButton.disabledBackground", getButtonBackgroundColor(),
            "ToggleButton.disabledSelectedBackground", getSelectionBackgroundColor(),
            "ToggleButton.focus", getFocusColor(),
            "ToggleButton.border", toggleButtonBorder,
            // ToolTip
            "ToolTip.border", toolTipBorder,
            "ToolTip.foreground", getTooltipForegroundColor(),
            "ToolTip.background", getTooltipBackgroundColor(),
            // Slider
            "Slider.border", null,
            "Slider.foreground", getFrameColor(),
            "Slider.background", getBackgroundColor(),
            "Slider.focus", getFocusColor(),
            "Slider.focusInsets", new InsetsUIResource(0, 0, 0, 0),
            "Slider.trackWidth", 7,
            "Slider.majorTickLength", 6,
            // Progress Bar
            "ProgressBar.border", progressBarBorder,
            "ProgressBar.background", progressBarBackground,
            "ProgressBar.selectionForeground", getSelectionForegroundColor(),
            "ProgressBar.selectionBackground", getForegroundColor(),
            // Combo Box
            "ComboBox.border", comboBoxBorder,
            "ComboBox.background", getInputBackgroundColor(),
            "ComboBox.foreground", getInputForegroundColor(),
            "ComboBox.selectionBackground", getSelectionBackgroundColor(),
            "ComboBox.selectionForeground", getSelectionForegroundColor(),
            "ComboBox.selectionBorderColor", getFocusColor(),
            "ComboBox.disabledBackground", getDisabledBackgroundColor(),
            "ComboBox.disabledForeground", getDisabledForegroundColor(),
            "ComboBox.listBackground", getInputBackgroundColor(),
            "ComboBox.listForeground", getInputForegroundColor(),
            "ComboBox.font", getUserTextFont(),
            // Panel
            "Panel.foreground", getForegroundColor(),
            "Panel.background", getBackgroundColor(),
            "Panel.darkBackground", getTheme().getBackgroundColorDark(),
            "Panel.lightBackground", getTheme().getBackgroundColorLight(),
            "Panel.alterBackground", getTheme().getAlterBackgroundColor(),
            "Panel.font", getUserTextFont(),
            // RootPane
            "RootPane.frameBorder", windowBorder,
            "RootPane.plainDialogBorder", windowBorder,
            "RootPane.informationDialogBorder", windowBorder,
            "RootPane.errorDialogBorder", windowBorder,
            "RootPane.colorChooserDialogBorder", windowBorder,
            "RootPane.fileChooserDialogBorder", windowBorder,
            "RootPane.questionDialogBorder", windowBorder,
            "RootPane.warningDialogBorder", windowBorder,
            // InternalFrame
            "InternalFrame.border", getBorderFactory().getInternalFrameBorder(),
            "InternalFrame.font", getWindowTitleFont(),
            "InternalFrame.paletteBorder", getBorderFactory().getPaletteBorder(),
            "InternalFrame.paletteTitleHeight", 11,
            "InternalFrame.paletteCloseIcon", getIconFactory().getPaletteCloseIcon(),
            "InternalFrame.icon", getIconFactory().getMenuIcon(),
            "InternalFrame.iconifyIcon", getIconFactory().getIconIcon(),
            "InternalFrame.maximizeIcon", getIconFactory().getMaxIcon(),
            "InternalFrame.altMaximizeIcon", getIconFactory().getMinIcon(),
            "InternalFrame.minimizeIcon", getIconFactory().getMinIcon(),
            "InternalFrame.closeIcon", getIconFactory().getCloseIcon(),
            // Titled Border
            "TitledBorder.titleColor", getForegroundColor(),
            "TitledBorder.border", titledBorderBorder,
            // List
            "List.focusCellHighlightBorder", focusCellHighlightBorder,
            "List.font", getUserTextFont(),
            "List.foreground", getInputForegroundColor(),
            "List.background", getInputBackgroundColor(),
            "List.selectionForeground", getSelectionForegroundColor(),
            "List.selectionBackground", getSelectionBackgroundColor(),
            "List.disabledForeground", getDisabledForegroundColor(),
            "List.disabledBackground", getDisabledBackgroundColor(),
            // ScrollBar
            "ScrollBar.background", getControlBackgroundColor(),
            "ScrollBar.highlight", getControlHighlightColor(),
            "ScrollBar.shadow", getControlShadowColor(),
            "ScrollBar.darkShadow", getControlDarkShadowColor(),
            "ScrollBar.thumb", getControlBackgroundColor(),
            "ScrollBar.thumbShadow", getControlShadowColor(),
            "ScrollBar.thumbHighlight", getControlHighlightColor(),
            "ScrollBar.width", new Integer(17),
            "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE,
            // ScrollPane
            "ScrollPane.border", scrollPaneBorder,
            "ScrollPane.foreground", getForegroundColor(),
            "ScrollPane.background", getBackgroundColor(),
            // Viewport
            "Viewport.foreground", getForegroundColor(),
            "Viewport.background", getBackgroundColor(),
            "Viewport.font", getUserTextFont(),
            // Tabbed Pane
            "TabbedPane.boder", tabbedPaneBorder,
            "TabbedPane.background", getBackgroundColor(),
            "TabbedPane.tabAreaBackground", getTabAreaBackgroundColor(),
            "TabbedPane.unselectedBackground", getControlColorDark(),
            "TabbedPane.foreground", getControlForegroundColor(),
            "TabbedPane.selected", getBackgroundColor(),
            "TabbedPane.selectedForeground", getTabSelectionForegroundColor(),
            "TabbedPane.tabAreaInsets", new InsetsUIResource(5, 5, 5, 5),
            "TabbedPane.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0),
            "TabbedPane.tabInsets", new InsetsUIResource(1, 6, 1, 6),
            "TabbedPane.focus", getFocusColor(),
            // TabbedPane ScrollButton
            "TabbedPane.selected", getButtonBackgroundColor(),
            "TabbedPane.shadow", new ColorUIResource(180, 180, 180),
            "TabbedPane.darkShadow", new ColorUIResource(120, 120, 120),
            "TabbedPane.highlight", new ColorUIResource(Color.white),
            // Tab Colors in Netbeans
            "tab_unsel_fill", getControlBackgroundColor(),
            "tab_sel_fill", getControlBackgroundColor(),
            // Table
            "Table.focusCellHighlightBorder", focusCellHighlightBorder,
            "Table.scrollPaneBorder", tableScrollPaneBorder,
            "Table.foreground", getInputForegroundColor(),
            "Table.background", getInputBackgroundColor(),
            "Table.gridColor", getGridColor(),
            "TableHeader.foreground", getControlForegroundColor(),
            "TableHeader.background", getBackgroundColor(),
            "TableHeader.cellBorder", getBorderFactory().getTableHeaderBorder(),
            // MenuBar
            "MenuBar.border", menuBarBorder,
            "MenuBar.foreground", getMenuForegroundColor(),
            "MenuBar.background", getMenuBackgroundColor(),
            // Menu
            "Menu.border", menuItemBorder,
            "Menu.borderPainted", Boolean.TRUE,
            "Menu.foreground", getMenuForegroundColor(),
            "Menu.background", getMenuBackgroundColor(),
            "Menu.selectionForeground", getMenuSelectionForegroundColor(),
            "Menu.selectionBackground", getMenuSelectionBackgroundColor(),
            "Menu.disabledForeground", getDisabledForegroundColor(),
            "Menu.acceleratorForeground", getMenuForegroundColor(),
            "Menu.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "Menu.arrowIcon", getIconFactory().getMenuArrowIcon(),
            // Popup Menu
            "PopupMenu.background", getMenuBackgroundColor(),
            "PopupMenu.border", popupMenuBorder,
            // Menu Item
            "MenuItem.border", menuItemBorder,
            "MenuItem.borderPainted", Boolean.TRUE,
            "MenuItem.foreground", getMenuForegroundColor(),
            "MenuItem.background", getMenuBackgroundColor(),
            "MenuItem.selectionForeground", getMenuSelectionForegroundColor(),
            "MenuItem.selectionBackground", getMenuSelectionBackgroundColor(),
            "MenuItem.disabledForeground", getDisabledForegroundColor(),
            "MenuItem.disabledShadow", getWhite(),
            "MenuItem.acceleratorForeground", getMenuForegroundColor(),
            "MenuItem.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "CheckBoxMenuItem.border", menuItemBorder,
            "CheckBoxMenuItem.borderPainted", Boolean.TRUE,
            "CheckBoxMenuItem.foreground", getMenuForegroundColor(),
            "CheckBoxMenuItem.background", getMenuBackgroundColor(),
            "CheckBoxMenuItem.selectionForeground", getMenuSelectionForegroundColor(),
            "CheckBoxMenuItem.selectionBackground", getMenuSelectionBackgroundColor(),
            "CheckBoxMenuItem.disabledForeground", getDisabledForegroundColor(),
            "CheckBoxMenuItem.disabledShadow", getWhite(),
            "CheckBoxMenuItem.acceleratorForeground", getMenuForegroundColor(),
            "CheckBoxMenuItem.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "CheckBoxMenuItem.checkIcon", getIconFactory().getMenuCheckBoxIcon(),
            "RadioButtonMenuItem.border", menuItemBorder,
            "RadioButtonMenuItem.borderPainted", Boolean.TRUE,
            "RadioButtonMenuItem.foreground", getMenuForegroundColor(),
            "RadioButtonMenuItem.background", getMenuBackgroundColor(),
            "RadioButtonMenuItem.selectionForeground", getMenuSelectionForegroundColor(),
            "RadioButtonMenuItem.selectionBackground", getMenuSelectionBackgroundColor(),
            "RadioButtonMenuItem.disabledForeground", getDisabledForegroundColor(),
            "RadioButtonMenuItem.disabledShadow", getWhite(),
            "RadioButtonMenuItem.acceleratorForeground", getMenuForegroundColor(),
            "RadioButtonMenuItem.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "RadioButtonMenuItem.checkIcon", getIconFactory().getMenuRadioButtonIcon(),
            // OptionPane.
            "OptionPane.errorIcon", getIconFactory().getOptionPaneErrorIcon(),
            "OptionPane.informationIcon", getIconFactory().getOptionPaneInformationIcon(),
            "OptionPane.warningIcon", getIconFactory().getOptionPaneWarningIcon(),
            "OptionPane.questionIcon", getIconFactory().getOptionPaneQuestionIcon(),
            "OptionPane.border", optionPaneBorder,
            "OptionPane.messageAreaBorder", optionPaneMessageAreaBorder,
            "OptionPane.buttonAreaBorder", optionPaneButtonAreaBorder,
            // File View
            "FileView.directoryIcon", getIconFactory().getTreeOpenIcon(),
            "FileView.fileIcon", getIconFactory().getTreeLeafIcon(),
            "FileView.computerIcon", getIconFactory().getFileViewComputerIcon(),
            "FileView.hardDriveIcon", getIconFactory().getFileViewHardDriveIcon(),
            "FileView.floppyDriveIcon", getIconFactory().getFileViewFloppyDriveIcon(),
            // File Chooser
            "FileChooser.upFolderIcon", getIconFactory().getFileChooserUpFolderIcon(),
            "FileChooser.homeFolderIcon", getIconFactory().getFileChooserHomeFolderIcon(),
            "FileChooser.newFolderIcon", getIconFactory().getFileChooserNewFolderIcon(),
            "FileChooser.listViewIcon", getIconFactory().getFileChooserListViewIcon(),
            "FileChooser.detailsViewIcon", getIconFactory().getFileChooserDetailViewIcon(),
            "FileChooser.viewMenuIcon", getIconFactory().getFileChooserDetailViewIcon(),
            // Separator
            "Separator.background", getBackgroundColor(),
            "Separator.foreground", getControlForegroundColor(),
            // SplitPane
            "SplitPane.centerOneTouchButtons", Boolean.TRUE,
            "SplitPane.dividerSize", new Integer(7),
            "SplitPane.border", BorderFactory.createEmptyBorder(),
            // Tree
            "Tree.background", getInputBackgroundColor(),
            "Tree.foreground", getInputForegroundColor(),
            "Tree.textForeground", getInputForegroundColor(),
            "Tree.textBackground", getInputBackgroundColor(),
            "Tree.selectionForeground", getSelectionForegroundColor(),
            "Tree.selectionBackground", getSelectionBackgroundColor(),
            "Tree.disabledForeground", getDisabledForegroundColor(),
            "Tree.disabledBackground", getDisabledBackgroundColor(),
            "Tree.openIcon", getIconFactory().getTreeOpenIcon(),
            "Tree.closedIcon", getIconFactory().getTreeCloseIcon(),
            "Tree.leafIcon", getIconFactory().getTreeLeafIcon(),
            "Tree.expandedIcon", getIconFactory().getTreeExpandedIcon(),
            "Tree.collapsedIcon", getIconFactory().getTreeCollapsedIcon(),
            "Tree.selectionBorderColor", getFocusCellColor(),
            "Tree.line", getFrameColor(), // horiz lines
            "Tree.hash", getFrameColor(), // legs

            // ToolBar
            "JToolBar.isRollover", Boolean.TRUE,
            "ToolBar.border", toolBarBorder,
            "ToolBar.background", getToolbarBackgroundColor(),
            "ToolBar.foreground", getToolbarForegroundColor(),
            "ToolBar.dockingBackground", getToolbarBackgroundColor(),
            "ToolBar.dockingForeground", getToolbarDockingColor(),
            "ToolBar.floatingBackground", getToolbarBackgroundColor(),
            "ToolBar.floatingForeground", getToolbarForegroundColor(),};
        table.putDefaults(defaults);

        if (Utilities.getJavaVersion() >= 1.5) {
            table.put("Spinner.font", getControlTextFont());
            table.put("Spinner.background", getButtonBackgroundColor());
            table.put("Spinner.foreground", getButtonForegroundColor());
            table.put("Spinner.border", getBorderFactory().getSpinnerBorder());
            table.put("Spinner.arrowButtonInsets", null);
            table.put("Spinner.arrowButtonBorder", BorderFactory.createEmptyBorder());
            table.put("Spinner.editorBorderPainted", Boolean.FALSE);
        }
        if (getTheme().isMacStyleScrollBarOn()) {
            if (getTheme().isSmallFontSize()) {
                table.put("ScrollBar.width", new Integer(8));
                table.put("SplitPane.dividerSize", new Integer(7));
            } else if (getTheme().isMediumFontSize()) {
                table.put("ScrollBar.width", new Integer(10));
                table.put("SplitPane.dividerSize", new Integer(9));
            } else {
                table.put("ScrollBar.width", new Integer(12));
                table.put("SplitPane.dividerSize", new Integer(11));
            }
        } else {
            if (getTheme().isSmallFontSize()) {
                table.put("ScrollBar.width", 17);
                table.put("SplitPane.dividerSize", new Integer(7));
            } else if (getTheme().isMediumFontSize()) {
                table.put("ScrollBar.width", new Integer(19));
                table.put("SplitPane.dividerSize", new Integer(9));
            } else {
                table.put("ScrollBar.width", 21);
                table.put("SplitPane.dividerSize", 11);
            }
        }
    }

    public static void setTheme(HiFiDefaultTheme theme) {
        if (theme == null) {
            return;
        }

        MetalLookAndFeel.setCurrentTheme(theme);
        myTheme = theme;
        if (isWindowDecorationOn()) {
            DecorationHelper.decorateWindows(Boolean.TRUE);
        } else {
            DecorationHelper.decorateWindows(Boolean.FALSE);
        }
    }

    /**
     * Set a theme by name. Allowed themes may come from the list returned by getThemes
     *
     * @param name the name of the theme
     */
    public static void setTheme(String name) {
       BaseTheme.setInternalName(name);
       setTheme((Properties) themesMap.get(name));
    }

    public static void setTheme(Properties themesProps) {
        currentThemeName = "hifiTheme";
        if (myTheme == null) {
            myTheme = new HiFiDefaultTheme();
        }
        if ((myTheme != null) && (themesProps != null)) {
            myTheme.setUpColor();
            myTheme.setProperties(themesProps);
            myTheme.setUpColorArrs();
            BaseLookAndFeel.setTheme(myTheme);
        }
    }
    
    public static void setTheme(String name, String licenseKey, String logoString) {
        Properties props = (Properties) themesMap.get(name);
        if (props != null) {
            props.put("licenseKey", licenseKey);
            props.put("logoString", logoString);
            BaseTheme.setInternalName(name);
            setTheme(props);
        }
    }
    
    public static void setCurrentTheme(Properties themesProps) {
        setTheme(themesProps);
    }
    
    public static BaseTheme getTheme() {
        return myTheme;
    }

    public static MetalTheme getCurrentTheme() {
        return myTheme;
    }

    public static List getThemes() {
        ArrayList themes = new ArrayList();
        themes.add(getTheme().getName());
        return themes;
    }

    public static Properties getThemeProperties(String name) {
        return ((Properties) themesMap.get(name));
    }
    
    public static boolean isWindowDecorationOn() {
        return getTheme().isWindowDecorationOn();
    }

    public static ColorUIResource getForegroundColor() {
        return getTheme().getForegroundColor();
    }

    public static ColorUIResource getDisabledForegroundColor() {
        return getTheme().getDisabledForegroundColor();
    }

    public static ColorUIResource getBackgroundColor() {
        return getTheme().getBackgroundColor();
    }

    public static ColorUIResource getAlterBackgroundColor() {
        return getTheme().getAlterBackgroundColor();
    }

    public static ColorUIResource getDisabledBackgroundColor() {
        return getTheme().getDisabledBackgroundColor();
    }

    public static ColorUIResource getInputForegroundColor() {
        return getTheme().getInputForegroundColor();
    }

    public static ColorUIResource getInputBackgroundColor() {
        return getTheme().getInputBackgroundColor();
    }

    public static ColorUIResource getFocusColor() {
        return getTheme().getFocusColor();
    }

    public static ColorUIResource getFocusCellColor() {
        return getTheme().getFocusCellColor();
    }

    public static ColorUIResource getFrameColor() {
        return getTheme().getFrameColor();
    }

    public static ColorUIResource getGridColor() {
        return getTheme().getGridColor();
    }

    public static ColorUIResource getSelectionForegroundColor() {
        return getTheme().getSelectionForegroundColor();
    }

    public static ColorUIResource getSelectionBackgroundColor() {
        return getTheme().getSelectionBackgroundColor();
    }

    public static ColorUIResource getButtonForegroundColor() {
        return getTheme().getButtonForegroundColor();
    }

    public static ColorUIResource getButtonBackgroundColor() {
        return getTheme().getButtonBackgroundColor();
    }

    public static ColorUIResource getButtonColorLight() {
        return getTheme().getButtonColorLight();
    }

    public static ColorUIResource getButtonColorDark() {
        return getTheme().getButtonColorDark();
    }

    public static ColorUIResource getControlForegroundColor() {
        return getTheme().getControlForegroundColor();
    }

    public static ColorUIResource getControlBackgroundColor() {
        return getTheme().getControlBackgroundColor();
    }

    public ColorUIResource getControlHighlightColor() {
        return getTheme().getControlHighlightColor();
    }

    public ColorUIResource getControlShadowColor() {
        return getTheme().getControlShadowColor();
    }

    public ColorUIResource getControlDarkShadowColor() {
        return getTheme().getControlDarkShadowColor();
    }

    public static ColorUIResource getControlColorLight() {
        return getTheme().getControlColorLight();
    }

    public static ColorUIResource getControlColorDark() {
        return getTheme().getControlColorDark();
    }

    public static ColorUIResource getWindowTitleForegroundColor() {
        return getTheme().getWindowTitleForegroundColor();
    }

    public static ColorUIResource getWindowTitleBackgroundColor() {
        return getTheme().getWindowTitleBackgroundColor();
    }

    public static ColorUIResource getWindowTitleColorLight() {
        return getTheme().getWindowTitleColorLight();
    }

    public static ColorUIResource getWindowTitleColorDark() {
        return getTheme().getWindowTitleColorDark();
    }

    public static ColorUIResource getWindowBorderColor() {
        return getTheme().getWindowBorderColor();
    }

    public static ColorUIResource getWindowInactiveTitleForegroundColor() {
        return getTheme().getWindowInactiveTitleForegroundColor();
    }

    public static ColorUIResource getWindowInactiveTitleBackgroundColor() {
        return getTheme().getWindowInactiveTitleBackgroundColor();
    }

    public static ColorUIResource getWindowInactiveTitleColorLight() {
        return getTheme().getWindowInactiveTitleColorLight();
    }

    public static ColorUIResource getWindowInactiveTitleColorDark() {
        return getTheme().getWindowInactiveTitleColorDark();
    }

    public static ColorUIResource getWindowInactiveBorderColor() {
        return getTheme().getWindowInactiveBorderColor();
    }

    public static ColorUIResource getMenuForegroundColor() {
        return getTheme().getMenuForegroundColor();
    }

    public static ColorUIResource getMenuBackgroundColor() {
        return getTheme().getMenuBackgroundColor();
    }

    public static ColorUIResource getMenuSelectionForegroundColor() {
        return getTheme().getMenuSelectionForegroundColor();
    }

    public static ColorUIResource getMenuSelectionBackgroundColor() {
        return getTheme().getMenuSelectionBackgroundColor();
    }

    public static ColorUIResource getMenuColorLight() {
        return getTheme().getMenuColorLight();
    }

    public static ColorUIResource getMenuColorDark() {
        return getTheme().getMenuColorDark();
    }

    public static ColorUIResource getToolbarForegroundColor() {
        return getTheme().getToolbarForegroundColor();
    }

    public static ColorUIResource getToolbarBackgroundColor() {
        return getTheme().getToolbarBackgroundColor();
    }

    public static ColorUIResource getToolbarColorLight() {
        return getTheme().getToolbarColorLight();
    }

    public static ColorUIResource getToolbarColorDark() {
        return getTheme().getToolbarColorDark();
    }

    public static ColorUIResource getToolbarDockingColor() {
        return getTheme().getFocusColor();
    }

    public static ColorUIResource getTabAreaBackgroundColor() {
        return getTheme().getTabAreaBackgroundColor();
    }

    public static ColorUIResource getTabSelectionForegroundColor() {
        return getTheme().getTabSelectionForegroundColor();
    }

    public static ColorUIResource getDesktopColor() {
        return getTheme().getDesktopColor();
    }

    public static ColorUIResource getTooltipForegroundColor() {
        return getTheme().getTooltipForegroundColor();
    }

    public static ColorUIResource getTooltipBackgroundColor() {
        return getTheme().getTooltipBackgroundColor();
    }

    public String getName() {
        return "HiFi";
    }

    public String getID() {
        return "HiFi";
    }

    public String getDescription() {
        return "The HiFi Look and Feel";
    }
    
    public boolean isNativeLookAndFeel() {
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }
    
    protected void createDefaultTheme() {
        if (myTheme == null) {
            myTheme = new HiFiDefaultTheme();
        }
        setTheme(myTheme);
    }
    
    
    protected void initClassDefaults(UIDefaults table) {
        if (!"hifiTheme".equals(currentThemeName)) {
            setTheme("Default");
        }
        super.initClassDefaults(table);
        Object[] uiDefaults = {
            // BaseLookAndFeel classes
            "SeparatorUI", BaseSeparatorUI.class.getName(),
            "TextFieldUI", BaseTextFieldUI.class.getName(),
            "TextAreaUI", BaseTextAreaUI.class.getName(),
            "EditorPaneUI", BaseEditorPaneUI.class.getName(),
            "PasswordFieldUI", BasePasswordFieldUI.class.getName(),
            "ComboBoxUI", BaseComboBoxUI.class.getName(),
            "ToolTipUI", BaseToolTipUI.class.getName(),
            "TreeUI", BaseTreeUI.class.getName(),
            "TableUI", BaseTableUI.class.getName(),
            "TableHeaderUI", BaseTableHeaderUI.class.getName(),
            "SplitPaneUI", BaseSplitPaneUI.class.getName(),
            "ProgressBarUI", BaseProgressBarUI.class.getName(),
            "FileChooserUI", BaseFileChooserUI.class.getName(),
            "ScrollBarUI", BaseScrollBarUI.class.getName(),
            "MenuUI", BaseMenuUI.class.getName(),
            "PopupMenuUI", BasePopupMenuUI.class.getName(),
            "MenuItemUI", BaseMenuItemUI.class.getName(),
            "CheckBoxMenuItemUI", BaseCheckBoxMenuItemUI.class.getName(),
            "RadioButtonMenuItemUI", BaseRadioButtonMenuItemUI.class.getName(),
            "PopupMenuSeparatorUI", BaseSeparatorUI.class.getName(),
            "DesktopPaneUI", BaseDesktopPaneUI.class.getName(),
            
            "LabelUI", HiFiLabelUI.class.getName(),
            "CheckBoxUI", HiFiCheckBoxUI.class.getName(),
            "RadioButtonUI", HiFiRadioButtonUI.class.getName(),
            "ButtonUI", BaseButtonUI.class.getName(),
            "ToggleButtonUI", HiFiToggleButtonUI.class.getName(),
            "SliderUI", HiFiSliderUI.class.getName(),
            "PanelUI", HiFiPanelUI.class.getName(),
            "ScrollPaneUI", HiFiScrollPaneUI.class.getName(),
            "TabbedPaneUI", BaseTabbedPaneUI.class.getName(),
            "ToolBarUI", HiFiToolBarUI.class.getName(),
            "MenuBarUI", HiFiMenuBarUI.class.getName(),
            "InternalFrameUI", HiFiInternalFrameUI.class.getName(),
            "RootPaneUI", HiFiRootPaneUI.class.getName(),};
        table.putDefaults(uiDefaults);
        if (Utilities.getJavaVersion() >= 1.5) {
            table.put("FormattedTextFieldUI", BaseFormattedTextFieldUI.class.getName());
            table.put("SpinnerUI", BaseSpinnerUI.class.getName());
        }
    }
}
