package skin;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class BaseTabbedPaneUI extends TabbedPaneUI implements SwingConstants {

    protected static final Insets NULL_BORDER_INSETS = new Insets(0, 0, 0, 0);
    protected static final int GAP = 5;

    // Instance variables initialized at installation
    protected JTabbedPane tabPane;
    protected Color tabAreaBackground;
    protected Color selectedColor;
    protected int textIconGap;
    protected int tabRunOverlay;
    protected Insets tabInsets;
    protected Insets selectedTabPadInsets;
    protected Insets tabAreaInsets;
    protected Insets contentBorderInsets;
    protected int tabRuns[] = new int[10];
    protected int runCount = 0;
    protected int selectedRun = -1;
    protected Rectangle rects[] = new Rectangle[0];
    protected int maxTabHeight;
    protected int maxTabWidth;
    protected ChangeListener tabChangeListener;
    protected ComponentListener tabComponentListener;
    protected PropertyChangeListener propertyChangeListener;
    protected MouseListener mouseListener;
    protected MouseMotionListener mouseMotionListener;
    protected FocusListener focusListener;
    private ContainerListener containerListener;
    private final Insets currentPadInsets = new Insets(0, 0, 0, 0);
    private final Insets currentTabAreaInsets = new Insets(0, 0, 0, 0);
    private Component visibleComponent;
    private ArrayList htmlViews;
    private HashMap mnemonicToIndexMap;
    private InputMap mnemonicInputMap;
    private ScrollableTabSupport tabScroller;
    private TabContainer tabContainer;
    protected transient Rectangle calcRect = new Rectangle(0, 0, 0, 0);
    private int tabCount;
    protected int oldRolloverIndex = -1;
    protected int rolloverIndex = -1;
    protected boolean roundedTabs = true;
    protected boolean simpleButtonBorder = false;

    public static ComponentUI createUI(JComponent c) {
        return new BaseTabbedPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        this.tabPane = (JTabbedPane) c;
        c.setLayout(createLayoutManager());
        installComponents();
        installDefaults();
        installListeners();
        installKeyboardActions();
    }

    @Override
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
        uninstallComponents();
        c.setLayout(null);

        this.tabPane = null;
    }

    protected LayoutManager createLayoutManager() {
        if (Utilities.getJavaVersion() >= 1.4) {
            if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
                return new TabbedPaneScrollLayout();
            }
        }
        return new TabbedPaneLayout();
    }

    protected boolean scrollableTabLayoutEnabled() {
        return (tabPane.getLayout() instanceof TabbedPaneScrollLayout);
    }

    protected void installComponents() {
        if (scrollableTabLayoutEnabled()) {
            if (tabScroller == null) {
                tabScroller = new ScrollableTabSupport(tabPane.getTabPlacement());
                tabPane.add(tabScroller.viewport);
                tabPane.add(tabScroller.scrollForwardButton);
                tabPane.add(tabScroller.scrollBackwardButton);
                tabPane.add(tabScroller.popupMenuButton);
                tabScroller.tabPanel.setBackground(tabAreaBackground);
            }
        }
        installTabContainer();
    }

    private Component getTabComponentAt(int index) {
        if (Utilities.getJavaVersion() >= 1.6) {
            return tabPane.getTabComponentAt(index);
        }
        return null;
    }

    private void installTabContainer() {
        if (Utilities.getJavaVersion() >= 1.6) {
            for (int i = 0; i < tabPane.getTabCount(); i++) {
                Component tabComponent = getTabComponentAt(i);
                if (tabComponent != null) {
                    if (tabContainer == null) {
                        tabContainer = new TabContainer();
                    }
                    tabContainer.add(tabComponent);
                    addMyPropertyChangeListeners(tabComponent);
                }
            }
            if (tabContainer == null) {
                return;
            }
            if (scrollableTabLayoutEnabled()) {
                tabScroller.tabPanel.add(tabContainer);
            } else {
                tabPane.add(tabContainer);
            }
        }
    }

    protected void uninstallComponents() {
        uninstallTabContainer();
        if (scrollableTabLayoutEnabled()) {
            tabPane.remove(tabScroller.viewport);
            tabPane.remove(tabScroller.scrollForwardButton);
            tabPane.remove(tabScroller.scrollBackwardButton);
            tabPane.remove(tabScroller.popupMenuButton);
            tabScroller = null;
        }
    }

    private void addMyPropertyChangeListeners(Component component) {
        component.addPropertyChangeListener(new MyTabComponentListener());
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component c = container.getComponent(i);
                addMyPropertyChangeListeners(c);
            }
        }
    }

    private void removeMyPropertyChangeListeners(Component component) {
        PropertyChangeListener[] listeners = component.getPropertyChangeListeners();
        for (PropertyChangeListener listener : listeners) {
            if (listener instanceof MyTabComponentListener) {
                component.removePropertyChangeListener(listener);
            }
        }
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component c = container.getComponent(i);
                removeMyPropertyChangeListeners(c);
            }
        }
    }

    private void uninstallTabContainer() {
        if (Utilities.getJavaVersion() >= 1.6) {
            if (tabContainer == null) {
                return;
            }
            tabContainer.notifyTabbedPane = false;
            for (int i = 0; i < tabContainer.getComponentCount(); i++) {
                Component c = tabContainer.getComponent(i);
                removeMyPropertyChangeListeners(c);
            }
            tabContainer.removeAll();
            if (scrollableTabLayoutEnabled()) {
                tabScroller.tabPanel.remove(tabContainer);
            } else {
                tabPane.remove(tabContainer);
            }
            tabContainer = null;
        }
    }

    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background", "TabbedPane.foreground", "TabbedPane.font");
        tabAreaBackground = UIManager.getColor("TabbedPane.tabAreaBackground");
        selectedColor = UIManager.getColor("TabbedPane.selected");
        textIconGap = UIManager.getInt("TabbedPane.textIconGap");
        tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
        selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
        tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
        contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
        tabPane.setBorder(UIManager.getBorder("TabbedPane.boder"));
    }

    protected void uninstallDefaults() {
        tabInsets = null;
        selectedTabPadInsets = null;
        tabAreaInsets = null;
        contentBorderInsets = null;
    }

    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            tabPane.addPropertyChangeListener(propertyChangeListener);
        }
        if ((tabChangeListener = createChangeListener()) != null) {
            tabPane.addChangeListener(tabChangeListener);
        }
        if ((tabComponentListener = createComponentListener()) != null) {
            tabPane.addComponentListener(tabComponentListener);
        }
        if ((mouseListener = createMouseListener()) != null) {
            if (scrollableTabLayoutEnabled()) {
                tabScroller.tabPanel.addMouseListener(mouseListener);

            } else {
                tabPane.addMouseListener(mouseListener);
            }
        }
        if ((mouseMotionListener = createMouseMotionListener()) != null) {
            if (scrollableTabLayoutEnabled()) {
                tabScroller.tabPanel.addMouseMotionListener(mouseMotionListener);

            } else {
                tabPane.addMouseMotionListener(mouseMotionListener);
            }
        }
        if ((focusListener = createFocusListener()) != null) {
            tabPane.addFocusListener(focusListener);
        }
        containerListener = new ContainerHandler();
        tabPane.addContainerListener(containerListener);
        if (tabPane.getTabCount() > 0) {
            htmlViews = createHTMLViewList();
        }
    }

    protected void uninstallListeners() {
        if (mouseListener != null) {
            if (scrollableTabLayoutEnabled()) {
                tabScroller.tabPanel.removeMouseListener(mouseListener);
            } else {
                tabPane.removeMouseListener(mouseListener);
            }
            mouseListener = null;
        }
        if (mouseMotionListener != null) {
            if (scrollableTabLayoutEnabled()) {
                tabScroller.tabPanel.removeMouseMotionListener(mouseMotionListener);
            } else {
                tabPane.removeMouseMotionListener(mouseMotionListener);
            }
            mouseMotionListener = null;
        }
        if (focusListener != null) {
            tabPane.removeFocusListener(focusListener);
            focusListener = null;
        }

        if (containerListener != null) {
            tabPane.removeContainerListener(containerListener);
            containerListener = null;
            if (htmlViews != null) {
                htmlViews.clear();
                htmlViews = null;
            }
        }
        if (tabChangeListener != null) {
            tabPane.removeChangeListener(tabChangeListener);
            tabChangeListener = null;
        }
        if (tabComponentListener != null) {
            tabPane.removeComponentListener(tabComponentListener);
            tabChangeListener = null;
        }
        if (propertyChangeListener != null) {
            tabPane.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
    }

    protected MouseListener createMouseListener() {
        return new MouseHandler();
    }

    protected MouseMotionListener createMouseMotionListener() {
        return new MouseMotionHandler();
    }

    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }

    protected ChangeListener createChangeListener() {
        return new TabSelectionHandler();
    }

    protected ComponentListener createComponentListener() {
        return new TabComponentHandler();
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected void installKeyboardActions() {
        InputMap km = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, km);
        km = getInputMap(JComponent.WHEN_FOCUSED);
        SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_FOCUSED, km);
        ActionMap am = getActionMap();
        SwingUtilities.replaceUIActionMap(tabPane, am);
        if (scrollableTabLayoutEnabled()) {
            tabScroller.scrollForwardButton.setAction(am.get("scrollTabsForwardAction"));
            tabScroller.scrollBackwardButton.setAction(am.get("scrollTabsBackwardAction"));
            tabScroller.popupMenuButton.setAction(am.get("scrollTabsPopupMenuAction"));
        }
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            return (InputMap) UIManager.get("TabbedPane.ancestorInputMap");
        } else if (condition == JComponent.WHEN_FOCUSED) {
            return (InputMap) UIManager.get("TabbedPane.focusInputMap");
        }
        return null;
    }

    ActionMap getActionMap() {
        ActionMap map = (ActionMap) UIManager.get("TabbedPane.actionMap");

        if (map == null) {
            map = createActionMap();
            if (map != null) {
                UIManager.getLookAndFeelDefaults().put("TabbedPane.actionMap", map);
            }
        }
        return map;
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();
        map.put("navigateNext", new NextAction());
        map.put("navigatePrevious", new PreviousAction());
        map.put("navigateRight", new RightAction());
        map.put("navigateLeft", new LeftAction());
        map.put("navigateUp", new UpAction());
        map.put("navigateDown", new DownAction());
        map.put("navigatePageUp", new PageUpAction());
        map.put("navigatePageDown", new PageDownAction());
        map.put("requestFocus", new RequestFocusAction());
        map.put("requestFocusForVisibleComponent", new RequestFocusForVisibleAction());
        map.put("setSelectedIndex", new SetSelectedIndexAction());
        map.put("scrollTabsForwardAction", new ScrollTabsForwardAction());
        map.put("scrollTabsBackwardAction", new ScrollTabsBackwardAction());
        map.put("scrollTabsPopupMenuAction", new ScrollTabsPopupMenuAction());
        return map;
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(tabPane, null);
        SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
        SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_FOCUSED, null);
        SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_IN_FOCUSED_WINDOW, null);
    }

    private void updateMnemonics() {
        if (Utilities.getJavaVersion() >= 1.4) {
            resetMnemonics();
            for (int counter = tabPane.getTabCount() - 1; counter >= 0; counter--) {
                int mnemonic = tabPane.getMnemonicAt(counter);
                if (mnemonic > 0) {
                    addMnemonic(counter, mnemonic);
                }
            }
        }
    }

    private void resetMnemonics() {
        if (mnemonicToIndexMap != null) {
            mnemonicToIndexMap.clear();
            mnemonicInputMap.clear();
        }
    }

    private void addMnemonic(int index, int mnemonic) {
        if (mnemonicToIndexMap == null) {
            initMnemonics();
        }
        mnemonicInputMap.put(KeyStroke.getKeyStroke(mnemonic, Event.ALT_MASK), "setSelectedIndex");
        mnemonicToIndexMap.put(mnemonic, index);
    }

    private void initMnemonics() {
        mnemonicToIndexMap = new HashMap();
        mnemonicInputMap = new ComponentInputMapUIResource(tabPane);
        mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(tabPane, JComponent.WHEN_IN_FOCUSED_WINDOW));
        SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_IN_FOCUSED_WINDOW, mnemonicInputMap);
    }

    protected boolean isContentOpaque() {
        if (!tabPane.isOpaque()) {
            if (UIManager.get("TabbedPane.contentOpaque") != null) {
                return UIManager.getBoolean("TabbedPane.contentOpaque");
            }
        }
        return true;
    }

    protected boolean isTabOpaque() {
        if (!tabPane.isOpaque()) {
            if (UIManager.get("TabbedPane.tabsOpaque") != null) {
                return UIManager.getBoolean("TabbedPane.tabsOpaque");
            }
        }
        return true;
    }

    protected boolean hasInnerBorder() {
        return false;
    }

    protected Color[] getTabColors(int tabIndex, boolean isSelected, boolean isRollover) {
        Color colorArr[] = BaseLookAndFeel.getTheme().getTabColors();
        if ((tabIndex >= 0) && (tabIndex < tabPane.getTabCount())) {
            boolean isEnabled = tabPane.isEnabledAt(tabIndex);
            Color backColor = tabPane.getBackgroundAt(tabIndex);
            if (backColor instanceof UIResource) {
                if (isSelected) {
                    colorArr = BaseLookAndFeel.getTheme().getSelectedColors();
                } else if (isRollover && isEnabled) {
                    colorArr = BaseLookAndFeel.getTheme().getRolloverColors();
                } else if (Utilities.isFrameActive(tabPane)) {
                    colorArr = BaseLookAndFeel.getTheme().getTabColors();
                } else {
                    colorArr = BaseLookAndFeel.getTheme().getInActiveColors();
                }
            } else if (backColor != null) {
                if (isSelected) {
                    colorArr = ColorHelper.createColorArr(ColorHelper.brighter(backColor, 60), backColor, 20);
                } else if (isRollover && isEnabled) {
                    colorArr = ColorHelper.createColorArr(ColorHelper.brighter(backColor, 80), ColorHelper.brighter(backColor, 20), 20);
                } else {
                    colorArr = ColorHelper.createColorArr(ColorHelper.brighter(backColor, 40), ColorHelper.darker(backColor, 10), 20);
                }
            }
        }
        return colorArr;
    }

    protected Color getLoBorderColor(int tabIndex) {
        return BaseLookAndFeel.getControlDarkShadow();
    }

    protected Color getHiBorderColor(int tabIndex) {
        Color backColor = tabPane.getBackgroundAt(tabIndex);
        if (tabIndex == tabPane.getSelectedIndex()) {
            if (backColor instanceof UIResource) {
                return BaseLookAndFeel.getControlHighlight();
            } else {
                return ColorHelper.brighter(backColor, 40);
            }
        }
        if (tabIndex >= 0 && tabIndex <= tabCount) {
            if (!isTabOpaque() || backColor instanceof UIResource) {
                return BaseLookAndFeel.getControlHighlight();
            } else {
                return ColorHelper.brighter(backColor, 40);
            }
        }
        return BaseLookAndFeel.getControlHighlight();
    }

    protected Color[] getContentBorderColors(int tabPlacement) {
        Color SEP_COLORS[] = {
            ColorHelper.darker(BaseLookAndFeel.getBackgroundColor(), 40),
            ColorHelper.brighter(BaseLookAndFeel.getBackgroundColor(), 20),
            ColorHelper.darker(BaseLookAndFeel.getBackgroundColor(), 20),
            ColorHelper.darker(BaseLookAndFeel.getBackgroundColor(), 40),
            ColorHelper.darker(BaseLookAndFeel.getBackgroundColor(), 60),};
        return SEP_COLORS;
    }

    protected Color getContentBorderColor() {
        return BaseLookAndFeel.getFrameColor();
    }

    protected Color getGapColor(int tabIndex) {
        if (isTabOpaque() || tabIndex == tabPane.getSelectedIndex()) {
            if ((tabIndex >= 0) && (tabIndex < tabCount)) {
                Color tabColors[] = getTabColors(tabIndex, tabIndex == tabPane.getSelectedIndex(), false);
                if (tabColors != null && tabColors.length > 0) {
                    return tabColors[tabColors.length - 1];
                } else {
                    return tabPane.getBackgroundAt(tabIndex);
                }
            }
        }
        if (!tabPane.isOpaque()) {
            Container parent = tabPane.getParent();
            while (parent != null) {
                if (parent.isOpaque()) {
                    return parent.getBackground();
                }
                parent = parent.getParent();
            }
        }
        return tabAreaBackground;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return null;
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return null;
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        int tc = tabPane.getTabCount();
        if (tabCount != tc) {
            tabCount = tc;
            updateMnemonics();
        }

        int selectedIndex = tabPane.getSelectedIndex();
        int tabPlacement = tabPane.getTabPlacement();

        ensureCurrentLayout();

        paintContentBorder(g, tabPlacement, selectedIndex, 0, 0, c.getWidth(), c.getHeight());

        if (!scrollableTabLayoutEnabled()) {
            paintTabArea(g, tabPlacement, selectedIndex);
        }
    }

    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        int tc = tabPane.getTabCount();
        Rectangle iconRect = new Rectangle(), textRect = new Rectangle();
        Shape savedClip = g.getClip();
        Rectangle clipRect = g.getClipBounds();
        if (scrollableTabLayoutEnabled() && tabScroller.scrollBackwardButton.isVisible()) {
            if ((tabPlacement == TOP) || (tabPlacement == BOTTOM)) {
                g.setClip(clipRect.x, clipRect.y, clipRect.width + 1, clipRect.height);
            } else {
                g.setClip(clipRect.x, clipRect.y, clipRect.width, clipRect.height + 1);
            }
        }
        for (int i = runCount - 1; i >= 0; i--) {
            int start = tabRuns[i];
            int next = tabRuns[(i == runCount - 1) ? 0 : i + 1];
            int end = (next != 0 ? next - 1 : tc - 1);
            for (int j = start; j <= end; j++) {
                if (rects[j].intersects(clipRect)) {
                    paintTab(g, tabPlacement, rects, j, iconRect, textRect);
                }
            }
        }

        if ((selectedIndex >= 0) && (selectedIndex < rects.length) && getRunForTab(tc, selectedIndex) == 0) {
            if (rects[selectedIndex].intersects(clipRect)) {
                paintTab(g, tabPlacement, rects, selectedIndex, iconRect, textRect);
            }
        }
        g.setClip(savedClip);
    }

    protected Font getTabFont(boolean isSelected) {
        return tabPane.getFont();
    }

    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;
        Graphics2D g2D = null;
        Polygon cropShape = null;
        Shape savedClip = null;
        int cropx = 0;
        int cropy = 0;

        if (scrollableTabLayoutEnabled()) {
            if (g instanceof Graphics2D) {
                g2D = (Graphics2D) g;

                Rectangle viewRect = tabScroller.viewport.getViewRect();
                int cropline;
                switch (tabPlacement) {
                    case LEFT:
                    case RIGHT:
                        cropline = viewRect.y + viewRect.height;
                        if ((tabRect.y < cropline) && (tabRect.y + tabRect.height > cropline)) {
                            cropShape = createCroppedTabClip(tabPlacement, tabRect, cropline);
                            cropx = tabRect.x;
                            cropy = cropline - 1;
                        }
                        break;
                    case TOP:
                    case BOTTOM:
                    default:
                        cropline = viewRect.x + viewRect.width;
                        if ((tabRect.x < cropline) && (tabRect.x + tabRect.width > cropline)) {
                            cropShape = createCroppedTabClip(tabPlacement, tabRect, cropline);
                            cropx = cropline - 1;
                            cropy = tabRect.y;
                        }
                }
                if (cropShape != null) {
                    savedClip = g2D.getClip();
                    g2D.clip(cropShape);
                }
            }
        }

        paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, isSelected);
        paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, isSelected);

        try {
            boolean doPaintContent = getTabComponentAt(tabIndex) == null;
            if (doPaintContent) {
                String title = tabPane.getTitleAt(tabIndex);
                Font font = getTabFont(isSelected);
                FontMetrics fm = Utilities.getFontMetrics(tabPane, g, font);
                Icon icon = getIconForTab(tabIndex);

                layoutLabel(tabPlacement, fm, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
                paintText(g, tabPlacement, font, fm, tabIndex, title, textRect, isSelected);
                paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
            }
            paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect, isSelected);
        } catch (Exception ex) {
        }

        if (cropShape != null) {
            paintCroppedTabEdge(g, tabPlacement, tabIndex, cropx, cropy);
            if (g2D != null && savedClip != null) {
                g2D.setClip(savedClip);
            }
        }
    }
    private final int xCropLen[] = {1, 1, 0, 0, 1, 1, 2, 2};
    private final int yCropLen[] = {0, 3, 3, 6, 6, 9, 9, 12};
    private static final int CROP_SEGMENT = 12;

    private Polygon createCroppedTabClip(int tabPlacement, Rectangle tabRect, int cropline) {
        int rlen;
        int start;
        int end;
        int ostart;

        switch (tabPlacement) {
            case LEFT:
            case RIGHT:
                rlen = tabRect.width;
                start = tabRect.x;
                end = tabRect.x + tabRect.width;
                ostart = tabRect.y;
                break;
            case TOP:
            case BOTTOM:
            default:
                rlen = tabRect.height;
                start = tabRect.y;
                end = tabRect.y + tabRect.height;
                ostart = tabRect.x;
        }
        int rcnt = rlen / CROP_SEGMENT;
        if (rlen % CROP_SEGMENT > 0) {
            rcnt++;
        }
        int npts = 2 + (rcnt * 8);
        int xp[] = new int[npts];
        int yp[] = new int[npts];
        int pcnt = 0;

        xp[pcnt] = ostart;
        yp[pcnt++] = end;
        xp[pcnt] = ostart;
        yp[pcnt++] = start;
        for (int i = 0; i < rcnt; i++) {
            for (int j = 0; j < xCropLen.length; j++) {
                xp[pcnt] = cropline - xCropLen[j];
                yp[pcnt] = start + (i * CROP_SEGMENT) + yCropLen[j];
                if (yp[pcnt] >= end) {
                    yp[pcnt] = end;
                    pcnt++;
                    break;
                }
                pcnt++;
            }
        }
        if (tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM) {
            return new Polygon(xp, yp, pcnt);
        } else {
            return new Polygon(yp, xp, pcnt);
        }
    }

    private void paintCroppedTabEdge(Graphics g, int tabPlacement, int tabIndex, int x, int y) {
        g.setColor(Color.gray);
        switch (tabPlacement) {
            case LEFT:
            case RIGHT:
                int xx = x;
                while (xx <= x + rects[tabIndex].width) {
                    for (int i = 0; i < xCropLen.length; i += 2) {
                        g.drawLine(xx + yCropLen[i], y - xCropLen[i], xx + yCropLen[i + 1] - 1, y - xCropLen[i + 1]);
                    }
                    xx += CROP_SEGMENT;
                }
                break;
            case TOP:
            case BOTTOM:
            default:
                int yy = y;
                while (yy <= y + rects[tabIndex].height) {
                    for (int i = 0; i < xCropLen.length; i += 2) {
                        g.drawLine(x - xCropLen[i], yy + yCropLen[i], x - xCropLen[i + 1], yy + yCropLen[i + 1] - 1);
                    }
                    yy += CROP_SEGMENT;
                }
        }
    }

    protected void layoutLabel(int tabPlacement, FontMetrics metrics,
            int tabIndex, String title, Icon icon, Rectangle tabRect,
            Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            tabPane.putClientProperty("html", v);
        }

        SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
                metrics, title, icon,
                SwingUtilities.CENTER,
                SwingUtilities.CENTER,
                SwingUtilities.CENTER,
                SwingUtilities.TRAILING,
                tabRect,
                iconRect,
                textRect,
                textIconGap);

        tabPane.putClientProperty("html", null);

        int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
        int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
        iconRect.x += xNudge;
        iconRect.y += yNudge;
        textRect.x += xNudge;
        textRect.y += yNudge;
    }

    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        if (!isSelected) {
            if (tabPlacement == TOP) {
                return 1;
            } else if (tabPlacement == BOTTOM) {
                return -1;
            }
        }
        return 0;
    }

    protected void paintIcon(Graphics g, int tabPlacement, int tabIndex, Icon icon, Rectangle iconRect, boolean isSelected) {
        if (icon != null) {
            icon.paintIcon(tabPane, g, iconRect.x, iconRect.y);
        }
    }

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        g.setFont(font);
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            Graphics2D g2D = (Graphics2D) g;
            Object savedRenderingHint = null;
            if (BaseLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, BaseLookAndFeel.getTheme().getTextAntiAliasingHint());
            }
            v.paint(g, textRect);
            if (BaseLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
            }
        } else {
            int mnemIndex = -1;
            if (Utilities.getJavaVersion() >= 1.4) {
                mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
            }

            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            Color fc = tabPane.getForegroundAt(tabIndex);
            if (isSelected) {
                fc = BaseLookAndFeel.getTheme().getTabSelectionForegroundColor();
            }
            if (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) {
                fc = BaseLookAndFeel.getTheme().getDisabledForegroundColor();
            }
            if (ColorHelper.getGrayValue(fc) > 128) {
                g2D.setColor(Color.black);
            } else {
                g2D.setColor(Color.white);
            }
            Utilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x + 1, textRect.y + 1 + metrics.getAscent());
            g2D.setComposite(composite);
            g2D.setColor(fc);
            Utilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
        }
    }

    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        if (tabPane.isRequestFocusEnabled() && tabPane.hasFocus() && isSelected && tabIndex >= 0 && textRect.width > 8) {
            g.setColor(BaseLookAndFeel.getTheme().getFocusColor());
            BasicGraphicsUtils.drawDashedRect(g, textRect.x - 4, textRect.y + 1, textRect.width + 8, textRect.height);
        }
    }


    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        int x2 = x + (w);
        int y2 = y + (h);
        switch (tabPlacement) {
            case LEFT:
                paintLeftTabBorder(tabIndex, g, x, y, x2, y2, isSelected);
                break;
            case RIGHT:
                paintRightTabBorder(tabIndex, g, x, y, x2, y2, isSelected);
                break;
            case BOTTOM:
                if (roundedTabs) {
                    paintRoundedBottomTabBorder(tabIndex, g, x, y, x2, y2 - 1, isSelected);
                } else {
                    paintBottomTabBorder(tabIndex, g, x, y, x2, y2 - 1, isSelected);
                }
                break;
            case TOP:
            default:
                if (roundedTabs) {
                    paintRoundedTopTabBorder(tabIndex, g, x, y, x2, y2, isSelected);
                } else {
                    paintTopTabBorder(tabIndex, g, x, y, x2, y2, isSelected);
                }
        }
    }

    protected void paintRoundedTopTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
        Graphics2D g2D = (Graphics2D) g;
        Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color borderColor = getLoBorderColor(tabIndex);
        g.setColor(borderColor);
        int d = 2 * GAP;
        if (isSelected) {
            g.drawLine(x1 + GAP, y1, x2 - GAP, y1);
            g.drawArc(x1, y1, d, d, 90, 90);
            g.drawArc(x2 - d, y1, d, d, 0, 90);
            g.drawLine(x1, y1 + GAP + 1, x1, y2);
            g.drawLine(x2, y1 + GAP + 1, x2, y2);
        } else {
            g.drawLine(x1 + GAP, y1, x2 - GAP, y1);
            g.drawArc(x1, y1, d, d, 90, 90);
            g.drawArc(x2 - d, y1, d, d, 0, 90);
            g.drawLine(x1, y1 + GAP + 1, x1, y2 - 1);
            g.drawLine(x2, y1 + GAP + 1, x2, y2 - 1);
        }
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
    }

    protected void paintTopTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
        int tc = tabPane.getTabCount();
        int currentRun = getRunForTab(tc, tabIndex);
        int lastIndex = lastTabInRun(tc, currentRun);
        int firstIndex = tabRuns[currentRun];
        boolean leftToRight = Utilities.isLeftToRight(tabPane);

        Color loColor = getLoBorderColor(tabIndex);
        Color hiColor = getHiBorderColor(tabIndex);

        g.setColor(loColor);
        g.drawLine(x1 + GAP, y1, x2, y1);
        g.drawLine(x1 + GAP, y1, x1, y1 + GAP);
        g.drawLine(x1, y1 + GAP + 1, x1, y2);
        g.drawLine(x2, y1, x2, y2);
        g.setColor(hiColor);
        g.drawLine(x1 + GAP + 1, y1 + 1, x2 - 1, y1 + 1);
        g.drawLine(x1 + GAP + 1, y1 + 1, x1 + 1, y1 + GAP + 1);
        g.drawLine(x1 + 1, y1 + GAP + 1, x1 + 1, y2 - 1);

        int gapTabIndex = getTabAtLocation(x1 + 2, y1 - 2);
        Color gapColor = getGapColor(gapTabIndex);
        g.setColor(gapColor);
        for (int i = 0; i < GAP; i++) {
            g.drawLine(x1, y1 + i, x1 + GAP - i - 1, y1 + i);
        }

        if (leftToRight) {
            if ((tabIndex != firstIndex) || (currentRun != (runCount - 1))) {
                g.setColor(loColor);
                g.drawLine(x1, y1, x1, y1 + GAP);
            }
            if (!isSelected && (tabIndex == firstIndex) && (currentRun != (runCount - 1))) {
                g.setColor(hiColor);
                g.drawLine(x1 + 1, y1, x1 + 1, y1 + GAP - 2);
            }
        } else if ((tabIndex != lastIndex) || (currentRun != (runCount - 1))) {
            g.setColor(loColor);
            g.drawLine(x1, y1, x1, y1 + GAP);
        }
    }

    protected void paintLeftTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
        Graphics2D g2D = (Graphics2D) g;

        int tc = tabPane.getTabCount();
        int currentRun = getRunForTab(tc, tabIndex);
        int lastIndex = lastTabInRun(tc, currentRun);
        int firstIndex = tabRuns[currentRun];

        Color loColor = getLoBorderColor(tabIndex);
        Color hiColor = getHiBorderColor(tabIndex);

        g.setColor(hiColor);
        Composite savedComposite = g2D.getComposite();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2D.setComposite(alpha);
        g.drawLine(x1 + GAP + 1, y1 + 1, x2 - 1, y1 + 1);
        g.drawLine(x1 + GAP, y1 + 1, x1 + 1, y1 + GAP);
        g.drawLine(x1 + 1, y1 + GAP + 1, x1 + 1, y2 - 1);
        g2D.setComposite(savedComposite);

        g.setColor(loColor);
        g.drawLine(x1 + GAP, y1, x2 - 1, y1);
        g.drawLine(x1 + GAP, y1, x1, y1 + GAP);
        g.drawLine(x1, y1 + GAP, x1, y2);
        g.drawLine(x1 + GAP, y2, x2 - 1, y2);
        if (tabIndex == lastIndex) {
            g.drawLine(x1, y2, x1 + GAP, y2);
        }

        int gapTabIndex = getTabAtLocation(x1 + 2, y1 - 2);
        Color gapColor = getGapColor(gapTabIndex);
        g.setColor(gapColor);
        for (int i = 0; i < GAP; i++) {
            g.drawLine(x1, y1 + i, x1 + GAP - i - 1, y1 + i);
        }

        if ((tabIndex != firstIndex) || (currentRun != (runCount - 1))) {
            loColor = getLoBorderColor(gapTabIndex);
            g.setColor(loColor);
            g.drawLine(x1, y1, x1, y1 + GAP - 1);
            if (tabIndex != firstIndex) {
                g2D.setComposite(alpha);
                hiColor = getHiBorderColor(gapTabIndex);
                g.setColor(hiColor);
                g.drawLine(x1 + 1, y1, x1 + 1, y1 + GAP - 2);
                g2D.setComposite(savedComposite);
            }
        }
    }

    protected void paintRoundedBottomTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
        Graphics2D g2D = (Graphics2D) g;
        Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color loColor = getLoBorderColor(tabIndex);
        int d = 2 * GAP;
        g.setColor(loColor);
        g.drawLine(x1 + GAP, y2, x2 - GAP, y2);
        g.drawArc(x1, y2 - d, d, d, 180, 90);
        g.drawArc(x2 - d, y2 - d, d, d, -90, 90);
        g.drawLine(x1, y1, x1, y2 - GAP - 1);
        g.drawLine(x2, y1, x2, y2 - GAP - 1);

        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
    }

    protected void paintBottomTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
        int tc = tabPane.getTabCount();
        int currentRun = getRunForTab(tc, tabIndex);
        int lastIndex = lastTabInRun(tc, currentRun);
        int firstIndex = tabRuns[currentRun];
        boolean leftToRight = Utilities.isLeftToRight(tabPane);

        Color loColor = getLoBorderColor(tabIndex);
        Color hiColor = getHiBorderColor(tabIndex);

        g.setColor(loColor);
        g.drawLine(x1, y1, x1, y2 - GAP);
        g.drawLine(x1, y2 - GAP, x1 + GAP, y2);
        g.drawLine(x1 + GAP, y2, x2, y2);
        g.drawLine(x2, y2, x2, y1);
        g.setColor(hiColor);
        g.drawLine(x1 + 1, y1, x1 + 1, y2 - GAP - 1);
        g.drawLine(x1 + 1, y2 - GAP, x1 + GAP, y2 - 1);

        int gapTabIndex = getTabAtLocation(x1 + 2, y2 + 2);
        Color gapColor = getGapColor(gapTabIndex);

        g.setColor(gapColor);
        for (int i = 0; i < GAP; i++) {
            g.drawLine(x1, y2 - i, x1 + GAP - i - 1, y2 - i);
        }
        if (leftToRight) {
            if ((tabIndex != firstIndex) || (currentRun != (runCount - 1))) {
                g.setColor(loColor);
                g.drawLine(x1, y2 - GAP, x1, y2);
            }
        } else if ((tabIndex != lastIndex) || (currentRun != (runCount - 1))) {
            g.setColor(loColor);
            g.drawLine(x1, y2 - GAP, x1, y2);
        }
    }

    protected void paintRightTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
        Graphics2D g2D = (Graphics2D) g;

        int tc = tabPane.getTabCount();
        int currentRun = getRunForTab(tc, tabIndex);
        int lastIndex = lastTabInRun(tc, currentRun);
        int firstIndex = tabRuns[currentRun];

        Color loColor = getLoBorderColor(tabIndex);
        Color hiColor = getHiBorderColor(tabIndex);

        Composite savedComposite = g2D.getComposite();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2D.setComposite(alpha);
        g.setColor(hiColor);
        g.drawLine(x1, y1 + 1, x2 - GAP - 1, y1 + 1);
        g.drawLine(x2 - GAP, y1 + 1, x2 - 1, y1 + GAP);
        g2D.setComposite(savedComposite);

        g.setColor(loColor);
        g.drawLine(x1, y1, x2 - GAP, y1);
        g.drawLine(x2 - GAP, y1, x2, y1 + GAP);
        g.drawLine(x2, y1 + GAP, x2, y2);
        if (tabIndex == lastIndex) {
            g.drawLine(x2, y2, x1, y2);
        }

        int gapTabIndex = getTabAtLocation(x1 + 2, y1 - 2);
        Color gapColor = getGapColor(gapTabIndex);
        g.setColor(gapColor);
        for (int i = 0; i < GAP; i++) {
            g.drawLine(x2 - GAP + i + 1, y1 + i, x2, y1 + i);
        }

        if ((tabIndex != firstIndex) || (currentRun != (runCount - 1))) {
            loColor = getLoBorderColor(gapTabIndex);
            g.setColor(loColor);
            g.drawLine(x2, y1, x2, y1 + GAP - 1);
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (isTabOpaque() || isSelected) {
            Graphics2D g2D = (Graphics2D) g;
            Shape savedClip = g.getClip();
            Area orgClipArea = new Area(new Rectangle2D.Double(x, y, w, h));
            if (savedClip != null) {
                orgClipArea = new Area(savedClip);
            }
            Color colorArr[] = getTabColors(tabIndex, isSelected, tabIndex == rolloverIndex);
            int d = 2 * GAP;
            switch (tabPlacement) {
                case TOP:
                default:
                    if (isSelected) {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y, w, h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y, w, h + 2));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        Utilities.fillHorGradient(g, colorArr, x, y, w, h + 4);
                        g2D.setClip(savedClip);
                    } else {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y, w, h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y, w, h));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        Utilities.fillHorGradient(g, colorArr, x, y, w, h + 4);
                        g2D.setClip(savedClip);
                    }
                    break;
                case LEFT:
                    if (isSelected) {
                        Utilities.fillHorGradient(g, colorArr, x + 1, y + 1, w + 1, h - 1);
                    } else {
                        Utilities.fillHorGradient(g, colorArr, x + 1, y + 1, w - 1, h - 1);
                    }
                    break;
                case BOTTOM:
                    if (isSelected) {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y - 4, w, h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y - 2, w, h + 1));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        Utilities.fillHorGradient(g, colorArr, x, y - 4, w, h + 4);
                        g2D.setClip(savedClip);
                    } else {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y - 4, w, h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y, w, h));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        Utilities.fillHorGradient(g, colorArr, x, y - 4, w, h + 4);
                        g2D.setClip(savedClip);
                    }
                    break;
                case RIGHT:
                    if (isSelected) {
                        Utilities.fillHorGradient(g, colorArr, x - 2, y + 1, w + 2, h - 1);
                    } else {
                        Utilities.fillHorGradient(g, colorArr, x, y + 1, w + 1, h - 1);
                    }
                    break;
            }
        }
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);

        if (tabPane.isOpaque()) {
            int xt = tabPlacement == RIGHT ? w - tabAreaWidth : 0;
            int yt = tabPlacement == BOTTOM ? h - tabAreaHeight : 0;
            int wt = tabPlacement == TOP || tabPlacement == BOTTOM ? w : tabAreaWidth;
            int ht = tabPlacement == LEFT || tabPlacement == RIGHT ? h : tabAreaHeight;
            g.setColor(tabAreaBackground);
            g.fillRect(xt, yt, wt, ht);
        }
        if (isContentOpaque()) {
            int xt = tabPlacement == LEFT ? tabAreaWidth : 0;
            int yt = tabPlacement == TOP ? tabAreaHeight : 0;
            int wt = tabPlacement == LEFT || tabPlacement == RIGHT ? w - tabAreaWidth : w;
            int ht = tabPlacement == TOP || tabPlacement == BOTTOM ? h - tabAreaHeight : h;
            g.setColor(tabPane.getBackground());
            g.fillRect(xt, yt, wt, ht);
        }

        Insets bi = new Insets(0, 0, 0, 0);
        if (tabPane.getBorder() != null) {
            bi = tabPane.getBorder().getBorderInsets(tabPane);
        }
        if (hasInnerBorder()) {
            Color loColor = BaseLookAndFeel.getControlDarkShadow();
            Color hiColor = BaseLookAndFeel.getControlHighlight();
            g.setColor(loColor);
            switch (tabPlacement) {
                case TOP: {
                    int x1 = x + bi.left - 1;
                    int y1 = y + tabAreaHeight + bi.top - 2;
                    int x2 = x1 + w - bi.left - bi.right + 1;
                    int y2 = h - bi.bottom;
                    int ws = w - bi.left - bi.right + 1;
                    int hs = h - tabAreaHeight - bi.top - bi.bottom + 2;

                    if (tabPane.getBorder() == null) {
                        g.drawLine(x1, y1, x2, y1);
                        g.setColor(hiColor);
                        g.drawLine(x1, y1 + 1, x2, y1 + 1);
                    } else {
                        g.drawRect(x1, y1, ws, hs);
                        g.setColor(hiColor);
                        g.drawLine(x1 + 1, y1 + 1, x2 - 1, y1 + 1);
                    }
                    break;
                }
                case LEFT: {
                    int x1 = x + tabAreaWidth + bi.left - 2;
                    int y1 = y + bi.top - 1;
                    //int x2 = w - bi.right;
                    int y2 = y1 + h - bi.top - bi.bottom + 1;
                    int ws = w - tabAreaWidth - bi.left - bi.right + 2;
                    int hs = h - bi.top - bi.bottom + 1;

                    if (tabPane.getBorder() == null) {
                        g.drawLine(x1, y1, x1, y2);
                        g.setColor(hiColor);
                        g.drawLine(x1 + 1, y1, x1 + 1, y2);
                    } else {
                        g.drawRect(x1, y1, ws, hs);
                        g.setColor(hiColor);
                        g.drawLine(x1 + 1, y1 + 1, x1 + 1, y2 - 1);
                    }
                    break;
                }
                case BOTTOM: {
                    int x1 = x + bi.left - 1;
                    int y1 = y + bi.top - 1;
                    int x2 = x1 + w - bi.left - bi.right + 1;
                    int y2 = h - tabAreaHeight - bi.bottom;
                    int ws = w - bi.left - bi.right + 1;
                    int hs = h - tabAreaHeight - bi.top - bi.bottom + 2;

                    if (tabPane.getBorder() == null) {
                        g.drawLine(x1, y2, x2, y2);
                    } else {
                        g.drawRect(x1, y1, ws, hs);
                    }
                    break;
                }
                case RIGHT: {
                    int x1 = x + bi.left - 1;
                    int y1 = y + bi.top - 1;
                    int x2 = w - tabAreaWidth - bi.right + 1;
                    int y2 = y1 + h - bi.top - bi.bottom + 1;
                    int ws = w - tabAreaWidth - bi.left - bi.right + 2;
                    int hs = h - bi.top - bi.bottom + 1;

                    if (tabPane.getBorder() == null) {
                        g.drawLine(x2, y1, x2, y2);
                    } else {
                        g.drawRect(x1, y1, ws, hs);
                    }
                    break;
                }
            }
        } else {
            int sepHeight = tabAreaInsets.bottom;
            if (sepHeight > 0) {
                switch (tabPlacement) {
                    case TOP: {
                        Color colors[] = getContentBorderColors(tabPlacement);
                        int ys = y + tabAreaHeight - sepHeight + bi.top;
                        for (int i = 0; i < colors.length; i++) {
                            g.setColor(colors[i]);
                            g.drawLine(x, ys + i, x + w, ys + i);
                        }
                        break;
                    }
                    case LEFT: {
                        Color colors[] = getContentBorderColors(tabPlacement);
                        int xs = x + tabAreaWidth - sepHeight + bi.left;
                        for (int i = 0; i < colors.length; i++) {
                            g.setColor(colors[i]);
                            g.drawLine(xs + i, y, xs + i, y + h);
                        }
                        break;
                    }
                    case BOTTOM: {
                        Color colors[] = getContentBorderColors(tabPlacement);
                        int ys = y + h - tabAreaHeight - bi.bottom;
                        for (int i = 0; i < colors.length; i++) {
                            g.setColor(colors[i]);
                            g.drawLine(x, ys + i, x + w, ys + i);
                        }
                        break;
                    }
                    case RIGHT: {
                        Color colors[] = getContentBorderColors(tabPlacement);
                        int xs = x + w - tabAreaWidth - bi.right;
                        for (int i = 0; i < colors.length; i++) {
                            g.setColor(colors[i]);
                            g.drawLine(xs + i, y, xs + i, y + h);
                        }
                        break;
                    }
                }
            }
        }
    }

    protected void paintScrollContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Insets bi = new Insets(0, 0, 0, 0);
        if (tabPane.getBorder() != null) {
            bi = tabPane.getBorder().getBorderInsets(tabPane);
        }
        switch (tabPane.getTabPlacement()) {
            case TOP:
                paintContentBorder(g, tabPane.getTabPlacement(), tabPane.getSelectedIndex(), x, y - bi.top, w, h);
                break;
            case BOTTOM:
                paintContentBorder(g, tabPane.getTabPlacement(), tabPane.getSelectedIndex(), x, y + bi.bottom, w, h);
                break;
            case LEFT:
                paintContentBorder(g, tabPane.getTabPlacement(), tabPane.getSelectedIndex(), x - bi.left, y, w, h);
                break;
            case RIGHT:
                paintContentBorder(g, tabPane.getTabPlacement(), tabPane.getSelectedIndex(), x + bi.right, y, w, h);
                break;
            default:
                break;
        }
    }

    private void ensureCurrentLayout() {
        ((TabbedPaneLayout) tabPane.getLayout()).calculateLayoutInfo();
    }


    @Override
    public Rectangle getTabBounds(JTabbedPane pane, int i) {
        ensureCurrentLayout();
        Rectangle tabRect = new Rectangle();
        return getTabBounds(i, tabRect);
    }

    @Override
    public int getTabRunCount(JTabbedPane pane) {
        ensureCurrentLayout();
        return runCount;
    }

    @Override
    public int tabForCoordinate(JTabbedPane pane, int x, int y) {
        ensureCurrentLayout();
        Point p = new Point(x, y);

        if (scrollableTabLayoutEnabled()) {
            translatePointToTabPanel(x, y, p);
        }
        int tc = tabPane.getTabCount();
        for (int i = 0; i < tc; i++) {
            if (rects[i].contains(p.x, p.y)) {
                return i;
            }
        }
        return -1;
    }

    protected Rectangle getTabBounds(int tabIndex, Rectangle dest) {
        dest.width = rects[tabIndex].width;
        dest.height = rects[tabIndex].height;

        if (scrollableTabLayoutEnabled()) { // SCROLL_TAB_LAYOUT
            // Need to translate coordinates based on viewport location &
            // view position
            Point vpp = tabScroller.viewport.getLocation();
            Point viewp = tabScroller.viewport.getViewPosition();
            dest.x = rects[tabIndex].x + vpp.x - viewp.x;
            dest.y = rects[tabIndex].y + vpp.y - viewp.y;

        } else {
            dest.x = rects[tabIndex].x;
            dest.y = rects[tabIndex].y;
        }
        return dest;
    }

    protected int getTabAtLocation(int x, int y) {
        ensureCurrentLayout();
        int tc = tabPane.getTabCount();
        for (int i = 0; i < tc; i++) {
            if (rects[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    protected int getClosestTab(int x, int y) {
        int min = 0;
        int tc = Math.min(rects.length, tabPane.getTabCount());
        int max = tc;
        int tabPlacement = tabPane.getTabPlacement();
        boolean useX = (tabPlacement == TOP || tabPlacement == BOTTOM);
        int want = (useX) ? x : y;

        while (min != max) {
            int current = (max + min) / 2;
            int minLoc;
            int maxLoc;

            if (useX) {
                minLoc = rects[current].x;
                maxLoc = minLoc + rects[current].width;
            } else {
                minLoc = rects[current].y;
                maxLoc = minLoc + rects[current].height;
            }
            if (want < minLoc) {
                max = current;
                if (min == max) {
                    return Math.max(0, current - 1);
                }
            } else if (want >= maxLoc) {
                min = current;
                if (max - min <= 1) {
                    return Math.max(current + 1, tc - 1);
                }
            } else {
                return current;
            }
        }
        return min;
    }

    private Point translatePointToTabPanel(int srcx, int srcy, Point dest) {
        Point vpp = tabScroller.viewport.getLocation();
        Point viewp = tabScroller.viewport.getViewPosition();
        dest.x = srcx - vpp.x + viewp.x;
        dest.y = srcy - vpp.y + viewp.y;
        return dest;
    }

    protected Component getVisibleComponent() {
        return visibleComponent;
    }

    protected void setVisibleComponent(Component component) {
        if (visibleComponent != null && visibleComponent != component && visibleComponent.getParent() == tabPane) {
            visibleComponent.setVisible(false);
        }
        if (component != null && !component.isVisible()) {
            component.setVisible(true);
        }
        visibleComponent = component;
    }

    protected void assureRectsCreated(int tabCount) {
        int rectArrayLen = rects.length;
        if (tabCount != rectArrayLen) {
            Rectangle[] tempRectArray = new Rectangle[tabCount];
            System.arraycopy(rects, 0, tempRectArray, 0, Math.min(rectArrayLen, tabCount));
            rects = tempRectArray;
            for (int rectIndex = rectArrayLen; rectIndex < tabCount; rectIndex++) {
                rects[rectIndex] = new Rectangle();
            }
        }
    }

    protected void expandTabRunsArray() {
        int rectLen = tabRuns.length;
        int[] newArray = new int[rectLen + 10];
        System.arraycopy(tabRuns, 0, newArray, 0, runCount);
        tabRuns = newArray;
    }

    protected int getRunForTab(int tabCount, int tabIndex) {
        for (int i = 0; i < runCount; i++) {
            int first = tabRuns[i];
            int last = lastTabInRun(tabCount, i);
            if (tabIndex >= first && tabIndex <= last) {
                return i;
            }
        }
        return 0;
    }

    protected int lastTabInRun(int tabCount, int run) {
        if (runCount == 1) {
            return tabCount - 1;
        }
        int nextRun = (run == runCount - 1 ? 0 : run + 1);
        if (tabRuns[nextRun] == 0) {
            return tabCount - 1;
        }
        return tabRuns[nextRun] - 1;
    }

    protected int getTabRunOverlay(int tabPlacement) {
        return tabRunOverlay;
    }

    protected int getTabRunIndent(int tabPlacement, int run) {
        return 0;
    }

    protected boolean shouldPadTabRun(int tabPlacement, int run) {
        return runCount > 1;
    }

    protected boolean shouldRotateTabRuns(int tabPlacement) {
        return true;
    }

    protected Icon getIconForTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabCount) {
            return (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) ? tabPane.getDisabledIconAt(tabIndex) : tabPane.getIconAt(tabIndex);
        }
        return null;
    }

    protected View getTextViewForTab(int tabIndex) {
        if (htmlViews != null && htmlViews.size() > tabIndex) {
            return (View) htmlViews.get(tabIndex);
        }
        return null;
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        int height = 0;
        Component tabComponent = getTabComponentAt(tabIndex);
        if (tabComponent != null) {
            height = tabComponent.getPreferredSize().height;
        } else {
            View v = getTextViewForTab(tabIndex);
            if (v != null) {
                height += (int) v.getPreferredSpan(View.Y_AXIS);
            } else {

                height += fontHeight;
            }
            Icon icon = getIconForTab(tabIndex);
            if (icon != null) {
                height = Math.max(height, icon.getIconHeight());
            }
        }
        Insets ti = getTabInsets(tabPlacement, tabIndex);
        height += ti.top + ti.bottom + 2;
        return height;
    }

    protected int calculateMaxTabHeight(int tabPlacement) {
        int tc = tabPane.getTabCount();
        int result = 0;
        int fontHeight = getFontMetrics().getHeight();
        for (int i = 0; i < tc; i++) {
            result = Math.max(calculateTabHeight(tabPlacement, i, fontHeight), result);
        }
        return result;
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        Insets insets = getTabInsets(tabPlacement, tabIndex);
        int width = insets.left + insets.right + 3;
        Component tabComponent = getTabComponentAt(tabIndex);
        if (tabComponent != null) {
            width += tabComponent.getPreferredSize().width;
        } else {
            Icon icon = getIconForTab(tabIndex);
            if (icon != null) {
                width += icon.getIconWidth() + textIconGap;
            }
            View v = getTextViewForTab(tabIndex);
            if (v != null) {
                width += (int) v.getPreferredSpan(View.X_AXIS);
            } else {
                String title = tabPane.getTitleAt(tabIndex);
                width += SwingUtilities.computeStringWidth(metrics, title);
            }
        }

        return width;
    }

    protected int calculateMaxTabWidth(int tabPlacement) {
        int tc = tabPane.getTabCount();
        int result = 0;
        for (int i = 0; i < tc; i++) {
            result = Math.max(calculateTabWidth(tabPlacement, i, getFontMetrics()), result);
        }
        return result;
    }

    protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
        if (tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM) {
            Insets insets = getTabAreaInsets(tabPlacement);
            int overlay = getTabRunOverlay(tabPlacement);
            return (horizRunCount > 0 ? horizRunCount * (maxTabHeight - overlay) + overlay + insets.top + insets.bottom : 0);
        } else {
            return tabPane.getHeight();
        }
    }

    protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) {
        if (tabPlacement == JTabbedPane.LEFT || tabPlacement == JTabbedPane.RIGHT) {
            Insets insets = getTabAreaInsets(tabPlacement);
            int overlay = getTabRunOverlay(tabPlacement);
            return (vertRunCount > 0 ? vertRunCount * (maxTabWidth - overlay) + overlay + insets.left + insets.right : 0);
        } else {
            return tabPane.getWidth();
        }
    }

    protected Insets getTabInsets(int tabPlacement, int tabIndex) {
        return tabInsets;
    }

    protected Insets getSelectedTabPadInsets(int tabPlacement) {
        rotateInsets(selectedTabPadInsets, currentPadInsets, tabPlacement);
        return currentPadInsets;
    }

    protected Insets getTabAreaInsets(int tabPlacement) {
        rotateInsets(tabAreaInsets, currentTabAreaInsets, tabPlacement);
        return currentTabAreaInsets;
    }

    protected Insets getContentBorderInsets(int tabPlacement) {
        if (tabPane.getBorder() == null) {
            return NULL_BORDER_INSETS;
        }
        return contentBorderInsets;
    }

    protected FontMetrics getFontMetrics() {
        Font font = tabPane.getFont().deriveFont(Font.BOLD);
        return Utilities.getFontMetrics(tabPane, null, font);
    }

    protected void navigateSelectedTab(int direction) {
        int tabPlacement = tabPane.getTabPlacement();
        int current = tabPane.getSelectedIndex();
        int tc = tabPane.getTabCount();
        boolean leftToRight = Utilities.isLeftToRight(tabPane);

        if (tc <= 0) {
            return;
        }

        int offset;
        switch (tabPlacement) {
            case NEXT:
                selectNextTab(current);
                break;
            case PREVIOUS:
                selectPreviousTab(current);
                break;
            case LEFT:
            case RIGHT:
                switch (direction) {
                    case NORTH:
                        selectPreviousTabInRun(current);
                        break;
                    case SOUTH:
                        selectNextTabInRun(current);
                        break;
                    case WEST:
                        offset = getTabRunOffset(tabPlacement, tc, current, false);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    case EAST:
                        offset = getTabRunOffset(tabPlacement, tc, current, true);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    default:
                }
                break;
            case BOTTOM:
            case TOP:
            default:
                switch (direction) {
                    case NORTH:
                        offset = getTabRunOffset(tabPlacement, tc, current, false);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    case SOUTH:
                        offset = getTabRunOffset(tabPlacement, tc, current, true);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    case EAST:
                        if (leftToRight) {
                            selectNextTabInRun(current);
                        } else {
                            selectPreviousTabInRun(current);
                        }
                        break;
                    case WEST:
                        if (leftToRight) {
                            selectPreviousTabInRun(current);
                        } else {
                            selectNextTabInRun(current);
                        }
                        break;
                    default:
                }
        }
    }

    protected void selectNextTabInRun(int current) {
        int tc = tabPane.getTabCount();
        int tabIndex = getNextTabIndexInRun(tc, current);
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getNextTabIndexInRun(tc, tabIndex);
        }
        tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectPreviousTabInRun(int current) {
        int tc = tabPane.getTabCount();
        int tabIndex = getPreviousTabIndexInRun(tc, current);
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getPreviousTabIndexInRun(tc, tabIndex);
        }
        tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectNextTab(int current) {
        int tabIndex = getNextTabIndex(current);
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getNextTabIndex(tabIndex);
        }
        tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectPreviousTab(int current) {
        int tabIndex = getPreviousTabIndex(current);
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getPreviousTabIndex(tabIndex);
        }
        tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectAdjacentRunTab(int tabPlacement, int tabIndex, int offset) {
        if (runCount < 2) {
            return;
        }
        int newIndex;
        Rectangle r = rects[tabIndex];
        switch (tabPlacement) {
            case LEFT:
            case RIGHT:
                newIndex = getTabAtLocation(r.x + r.width / 2 + offset,
                        r.y + r.height / 2);
                break;
            case BOTTOM:
            case TOP:
            default:
                newIndex = getTabAtLocation(r.x + r.width / 2,
                        r.y + r.height / 2 + offset);
        }
        if (newIndex != -1) {
            while (!tabPane.isEnabledAt(newIndex) && newIndex != tabIndex) {
                newIndex = getNextTabIndex(newIndex);
            }
            tabPane.setSelectedIndex(newIndex);
        }
    }

    protected int getTabRunOffset(int tabPlacement, int tabCount, int tabIndex, boolean forward) {
        int run = getRunForTab(tabCount, tabIndex);
        int offset;
        switch (tabPlacement) {
            case LEFT: {
                if (run == 0) {
                    offset = (forward ? -(calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth) - maxTabWidth) : -maxTabWidth);

                } else if (run == runCount - 1) {
                    offset = (forward ? maxTabWidth : calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth) - maxTabWidth);
                } else {
                    offset = (forward ? maxTabWidth : -maxTabWidth);
                }
                break;
            }
            case RIGHT: {
                if (run == 0) {
                    offset = (forward ? maxTabWidth : calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth) - maxTabWidth);
                } else if (run == runCount - 1) {
                    offset = (forward ? -(calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth) - maxTabWidth) : -maxTabWidth);
                } else {
                    offset = (forward ? maxTabWidth : -maxTabWidth);
                }
                break;
            }
            case BOTTOM: {
                if (run == 0) {
                    offset = (forward ? maxTabHeight : calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight) - maxTabHeight);
                } else if (run == runCount - 1) {
                    offset = (forward ? -(calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight) - maxTabHeight) : -maxTabHeight);
                } else {
                    offset = (forward ? maxTabHeight : -maxTabHeight);
                }
                break;
            }
            case TOP:
            default: {
                if (run == 0) {
                    offset = (forward ? -(calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight) - maxTabHeight) : -maxTabHeight);
                } else if (run == runCount - 1) {
                    offset = (forward ? maxTabHeight : calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight) - maxTabHeight);
                } else {
                    offset = (forward ? maxTabHeight : -maxTabHeight);
                }
            }
        }
        return offset;
    }

    protected int getPreviousTabIndex(int base) {
        int tabIndex = (base - 1 >= 0 ? base - 1 : tabPane.getTabCount() - 1);
        return (tabIndex >= 0 ? tabIndex : 0);
    }

    protected int getNextTabIndex(int base) {
        return (base + 1) % tabPane.getTabCount();
    }

    protected int getNextTabIndexInRun(int tabCount, int base) {
        if (runCount < 2) {
            return getNextTabIndex(base);
        }
        int currentRun = getRunForTab(tabCount, base);
        int next = getNextTabIndex(base);
        if (next == tabRuns[getNextTabRun(currentRun)]) {
            return tabRuns[currentRun];
        }
        return next;
    }

    protected int getPreviousTabIndexInRun(int tabCount, int base) {
        if (runCount < 2) {
            return getPreviousTabIndex(base);
        }
        int currentRun = getRunForTab(tabCount, base);
        if (base == tabRuns[currentRun]) {
            int previous = tabRuns[getNextTabRun(currentRun)] - 1;
            return (previous != -1 ? previous : tabCount - 1);
        }
        return getPreviousTabIndex(base);
    }

    protected int getPreviousTabRun(int baseRun) {
        int runIndex = (baseRun - 1 >= 0 ? baseRun - 1 : runCount - 1);
        return (runIndex >= 0 ? runIndex : 0);
    }

    protected int getNextTabRun(int baseRun) {
        return (baseRun + 1) % runCount;
    }

    protected static void rotateInsets(Insets topInsets, Insets targetInsets, int targetPlacement) {
        switch (targetPlacement) {
            case LEFT:
                targetInsets.top = topInsets.left;
                targetInsets.left = topInsets.top;
                targetInsets.bottom = topInsets.right;
                targetInsets.right = topInsets.bottom;
                break;
            case BOTTOM:
                targetInsets.top = topInsets.bottom;
                targetInsets.left = topInsets.left;
                targetInsets.bottom = topInsets.top;
                targetInsets.right = topInsets.right;
                break;
            case RIGHT:
                targetInsets.top = topInsets.left;
                targetInsets.left = topInsets.bottom;
                targetInsets.bottom = topInsets.right;
                targetInsets.right = topInsets.top;
                break;
            case TOP:
            default:
                targetInsets.top = topInsets.top;
                targetInsets.left = topInsets.left;
                targetInsets.bottom = topInsets.bottom;
                targetInsets.right = topInsets.right;
        }
    }

    protected boolean requestFocusForVisibleComponent() {
        Component vc = getVisibleComponent();
        if (vc.isFocusTraversable()) {
            vc.requestFocus();
            return true;
        } else if (vc instanceof JComponent) {
            if (((JComponent) vc).requestDefaultFocus()) {
                return true;
            }
        }
        return false;
    }

    private static class RightAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(EAST);
        }
    };

    private static class LeftAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(WEST);
        }
    };

    private static class UpAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(NORTH);
        }
    };

    private static class DownAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(SOUTH);
        }
    };

    private static class NextAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(NEXT);
        }
    };

    private static class PreviousAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(PREVIOUS);
        }
    };

    private static class PageUpAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            int tabPlacement = pane.getTabPlacement();
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                ui.navigateSelectedTab(WEST);
            } else {
                ui.navigateSelectedTab(NORTH);
            }
        }
    };

    private static class PageDownAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            int tabPlacement = pane.getTabPlacement();
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                ui.navigateSelectedTab(EAST);
            } else {
                ui.navigateSelectedTab(SOUTH);
            }
        }
    };

    private static class RequestFocusAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            pane.requestFocus();
        }
    };

    private static class RequestFocusForVisibleAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
            ui.requestFocusForVisibleComponent();
        }
    };

    private static class SetSelectedIndexAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();

            if (pane != null && (pane.getUI() instanceof BaseTabbedPaneUI)) {
                BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();
                String command = e.getActionCommand();

                if (command != null && command.length() > 0) {
                    int mnemonic = (int) e.getActionCommand().charAt(0);
                    if (mnemonic >= 'a' && mnemonic <= 'z') {
                        mnemonic -= ('a' - 'A');
                    }
                    Integer index = (Integer) ui.mnemonicToIndexMap.get(mnemonic);
                    if (index != null && pane.isEnabledAt(index)) {
                        pane.setSelectedIndex(index);
                    }
                }
            }
        }
    };

    private static class ScrollTabsForwardAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane;
            Object src = e.getSource();
            if (src instanceof JTabbedPane) {
                pane = (JTabbedPane) src;
            } else if (src instanceof ScrollableTabButton) {
                pane = (JTabbedPane) ((ScrollableTabButton) src).getParent();
            } else {
                return; // shouldn't happen
            }
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();

            if (ui.scrollableTabLayoutEnabled()) {
                ui.tabScroller.scrollForward(pane.getTabPlacement());
            }
        }
    }

    private static class ScrollTabsBackwardAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane;
            Object src = e.getSource();
            if (src instanceof JTabbedPane) {
                pane = (JTabbedPane) src;
            } else if (src instanceof ScrollableTabButton) {
                pane = (JTabbedPane) ((ScrollableTabButton) src).getParent();
            } else {
                return;
            }
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) pane.getUI();

            if (ui.scrollableTabLayoutEnabled()) {
                ui.tabScroller.scrollBackward(pane.getTabPlacement());
            }
        }
    }

    private static class ScrollTabsPopupMenuItemAction extends AbstractAction {

        private JTabbedPane tabbedPane = null;
        private int selectIndex = 0;

        public ScrollTabsPopupMenuItemAction(JTabbedPane pane, int index) {
            tabbedPane = pane;
            selectIndex = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tabbedPane.setSelectedIndex(selectIndex);
        }
    }

    private static class ScrollTabsPopupMenuAction extends AbstractAction {

        private JTabbedPane tabbedPane = null;

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src instanceof JTabbedPane) {
                tabbedPane = (JTabbedPane) src;
            } else if (src instanceof ScrollablePopupMenuTabButton) {
                tabbedPane = (JTabbedPane) ((ScrollablePopupMenuTabButton) src).getParent();
            } else {
                return;
            }
            BaseTabbedPaneUI ui = (BaseTabbedPaneUI) tabbedPane.getUI();
            if (ui.scrollableTabLayoutEnabled()) {
                JPopupMenu popup = new JPopupMenu();
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    JMenuItem item = new JMenuItem(tabbedPane.getTitleAt(i));
                    item.addActionListener(new ScrollTabsPopupMenuItemAction(tabbedPane, i));
                    item.setEnabled(tabbedPane.isEnabledAt(i));
                    popup.add(item);
                }
                popup.show(ui.tabScroller.popupMenuButton, 0, 0);
                Point pt = ui.tabScroller.popupMenuButton.getLocationOnScreen();
                int x = -popup.getWidth() + ui.tabScroller.popupMenuButton.getWidth();
                int y = ui.tabScroller.popupMenuButton.getHeight() - 1;
                popup.setLocation(pt.x + x, pt.y + y);
            }
        }
    }

    public class TabbedPaneLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return calculateSize(false);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return calculateSize(true);
        }

        protected Dimension calculateSize(boolean minimum) {
            int tabPlacement = tabPane.getTabPlacement();
            Insets insets = tabPane.getInsets();
            Insets contentInsets = getContentBorderInsets(tabPlacement);
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);

            Dimension zeroSize = new Dimension(0, 0);
            int height = contentInsets.top + contentInsets.bottom;
            int width = contentInsets.left + contentInsets.right;
            int cWidth = 0;
            int cHeight = 0;

            for (int i = 0; i < tabPane.getTabCount(); i++) {
                Component component = tabPane.getComponentAt(i);
                if (component != null) {
                    Dimension size = minimum ? component.getMinimumSize() : component.getPreferredSize();
                    if (size != null) {
                        cHeight = Math.max(size.height, cHeight);
                        cWidth = Math.max(size.width, cWidth);
                    }
                }
            }
            width += cWidth;
            height += cHeight;
            int tabExtent;

            switch (tabPlacement) {
                case LEFT:
                case RIGHT:
                    height = Math.max(height, calculateMaxTabHeight(tabPlacement)
                            + tabAreaInsets.top + tabAreaInsets.bottom);
                    tabExtent = preferredTabAreaWidth(tabPlacement, height);
                    width += tabExtent;
                    break;
                case TOP:
                case BOTTOM:
                default:
                    width = Math.max(width, calculateMaxTabWidth(tabPlacement)
                            + tabAreaInsets.left + tabAreaInsets.right);
                    tabExtent = preferredTabAreaHeight(tabPlacement, width);
                    height += tabExtent;
            }
            return new Dimension(width + insets.left + insets.right, height + insets.bottom + insets.top);
        }

        protected int preferredTabAreaHeight(int tabPlacement, int width) {
            FontMetrics fm = getFontMetrics();
            int tc = tabPane.getTabCount();
            int total = 0;
            if (tc > 0) {
                int rows = 1;
                int x = 0;
                int maxTabHeight = calculateMaxTabHeight(tabPlacement);

                for (int i = 0; i < tc; i++) {
                    int tabWidth = calculateTabWidth(tabPlacement, i, fm);

                    if (x != 0 && x + tabWidth > width) {
                        rows++;
                        x = 0;
                    }
                    x += tabWidth;
                }
                total = calculateTabAreaHeight(tabPlacement, rows, maxTabHeight);
            }
            return total;
        }

        protected int preferredTabAreaWidth(int tabPlacement, int height) {
            FontMetrics fm = getFontMetrics();
            int tc = tabPane.getTabCount();
            int total = 0;
            if (tc > 0) {
                int columns = 1;
                int y = 0;
                int fontHeight = fm.getHeight();

                maxTabWidth = calculateMaxTabWidth(tabPlacement);

                for (int i = 0; i < tc; i++) {
                    int tabHeight = calculateTabHeight(tabPlacement, i, fontHeight);

                    if (y != 0 && y + tabHeight > height) {
                        columns++;
                        y = 0;
                    }
                    y += tabHeight;
                }
                total = calculateTabAreaWidth(tabPlacement, columns, maxTabWidth);
            }
            return total;
        }

        @Override
        public void layoutContainer(Container parent) {

            int tabPlacement = tabPane.getTabPlacement();
            Insets insets = tabPane.getInsets();
            int selectedIndex = tabPane.getSelectedIndex();
            Component visibleComponent = getVisibleComponent();

            calculateLayoutInfo();

            Component selectedComponent = null;
            if (selectedIndex < 0) {
                if (visibleComponent != null) {
                    setVisibleComponent(null);
                }
            } else {
                try {
                    selectedComponent = tabPane.getComponentAt(selectedIndex);
                } catch (Exception ex) {
                }
            }
            int cx, cy, cw, ch;
            int totalTabWidth = 0;
            int totalTabHeight = 0;
            Insets contentInsets = getContentBorderInsets(tabPlacement);

            boolean shouldChangeFocus = false;

            if (selectedComponent != null) {
                if (selectedComponent != visibleComponent && visibleComponent != null) {
                    if (SwingUtilities.findFocusOwner(visibleComponent) != null) {
                        shouldChangeFocus = true;
                    }
                }
                setVisibleComponent(selectedComponent);
            }

            Rectangle bounds = tabPane.getBounds();
            int numChildren = tabPane.getComponentCount();

            if (numChildren > 0) {

                switch (tabPlacement) {
                    case LEFT:
                        totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        cx = insets.left + totalTabWidth + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        break;
                    case RIGHT:
                        totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        break;
                    case BOTTOM:
                        totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        break;
                    case TOP:
                    default:
                        totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        cx = insets.left + contentInsets.left;
                        cy = insets.top + totalTabHeight + contentInsets.top;
                }

                cw = bounds.width - totalTabWidth
                        - insets.left - insets.right
                        - contentInsets.left - contentInsets.right;
                ch = bounds.height - totalTabHeight
                        - insets.top - insets.bottom
                        - contentInsets.top - contentInsets.bottom;

                for (int i = 0; i < numChildren; i++) {
                    Component child = tabPane.getComponent(i);
                    if (child == tabContainer) {

                        int tabContainerWidth = totalTabWidth == 0 ? cw : totalTabWidth;
                        int tabContainerHeight = totalTabHeight == 0 ? ch : totalTabHeight;

                        int tabContainerX = 0;
                        int tabContainerY = 0;
                        if (tabPlacement == BOTTOM) {
                            tabContainerY = bounds.height - tabContainerHeight;
                        } else if (tabPlacement == RIGHT) {
                            tabContainerX = bounds.width - tabContainerWidth;
                        }
                        child.setBounds(tabContainerX, tabContainerY, tabContainerWidth, tabContainerHeight);
                    } else {
                        child.setBounds(cx, cy, cw, ch);
                    }
                }
            }
            layoutTabComponents();
            if (shouldChangeFocus) {
                if (!requestFocusForVisibleComponent()) {
                    tabPane.requestFocus();
                }
            }
        }

        public void calculateLayoutInfo() {
            int tc = tabPane.getTabCount();
            assureRectsCreated(tc);
            calculateTabRects(tabPane.getTabPlacement(), tc);
        }

        private void layoutTabComponents() {
            if (Utilities.getJavaVersion() >= 1.6) {
                if (tabContainer == null) {
                    return;
                }
                Rectangle rect = new Rectangle();
                Point delta = new Point(-tabContainer.getX(), -tabContainer.getY());
                if (scrollableTabLayoutEnabled()) {
                    translatePointToTabPanel(0, 0, delta);
                }
                for (int i = 0; i < tabPane.getTabCount(); i++) {
                    Component tabComponent = getTabComponentAt(i);
                    if (tabComponent == null) {
                        continue;
                    }
                    getTabBounds(i, rect);
                    Dimension preferredSize = tabComponent.getPreferredSize();
                    Insets insets = getTabInsets(tabPane.getTabPlacement(), i);
                    int outerX = rect.x + insets.left + delta.x;
                    int outerY = rect.y + insets.top + delta.y;
                    int outerWidth = rect.width - insets.left - insets.right;
                    int outerHeight = rect.height - insets.top - insets.bottom;
                    int x = outerX + (outerWidth - preferredSize.width) / 2;
                    int y = outerY + (outerHeight - preferredSize.height) / 2;
                    int tabPlacement = tabPane.getTabPlacement();
                    boolean isSeleceted = i == tabPane.getSelectedIndex();
                    tabComponent.setBounds(x + getTabLabelShiftX(tabPlacement, i, isSeleceted), y + getTabLabelShiftY(tabPlacement, i, isSeleceted), preferredSize.width, preferredSize.height);
                }
            }
        }

        protected void calculateTabRects(int tabPlacement, int tabCount) {
            FontMetrics fm = getFontMetrics();
            Dimension size = tabPane.getSize();
            Insets insets = tabPane.getInsets();
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
            int fontHeight = fm.getHeight();
            int selectedIndex = tabPane.getSelectedIndex();
            int tabRunOverlay;
            int i, j;
            int x, y;
            int returnAt;
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
            boolean leftToRight = Utilities.isLeftToRight(tabPane);

            switch (tabPlacement) {
                case LEFT:
                    maxTabWidth = calculateMaxTabWidth(tabPlacement);
                    x = insets.left + tabAreaInsets.left;
                    y = insets.top + tabAreaInsets.top;
                    returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                    break;
                case RIGHT:
                    maxTabWidth = calculateMaxTabWidth(tabPlacement);
                    x = size.width - insets.right - tabAreaInsets.right - maxTabWidth;
                    y = insets.top + tabAreaInsets.top;
                    returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                    break;
                case BOTTOM:
                    maxTabHeight = calculateMaxTabHeight(tabPlacement);
                    x = insets.left + tabAreaInsets.left;
                    y = size.height - insets.bottom - tabAreaInsets.bottom - maxTabHeight;
                    returnAt = size.width - (insets.right + tabAreaInsets.right);
                    break;
                case TOP:
                default:
                    maxTabHeight = calculateMaxTabHeight(tabPlacement);
                    x = insets.left + tabAreaInsets.left;
                    y = insets.top + tabAreaInsets.top;
                    returnAt = size.width - (insets.right + tabAreaInsets.right);
                    break;
            }

            tabRunOverlay = getTabRunOverlay(tabPlacement);

            runCount = 0;
            selectedRun = -1;

            if (tabCount == 0) {
                return;
            }

            Rectangle rect;
            for (i = 0; i < tabCount; i++) {
                rect = rects[i];

                if (!verticalTabRuns) {
                    if (i > 0) {
                        rect.x = rects[i - 1].x + rects[i - 1].width;
                    } else {
                        tabRuns[0] = 0;
                        runCount = 1;
                        maxTabWidth = 0;
                        rect.x = x;
                    }
                    rect.width = calculateTabWidth(tabPlacement, i, fm);
                    maxTabWidth = Math.max(maxTabWidth, rect.width);

                    if (rect.x != 2 + insets.left && rect.x + rect.width > returnAt) {
                        if (runCount > tabRuns.length - 1) {
                            expandTabRunsArray();
                        }
                        tabRuns[runCount] = i;
                        runCount++;
                        rect.x = x;
                    }
                    rect.y = y;
                    rect.height = maxTabHeight;

                } else {
                    if (i > 0) {
                        rect.y = rects[i - 1].y + rects[i - 1].height;
                    } else {
                        tabRuns[0] = 0;
                        runCount = 1;
                        maxTabHeight = 0;
                        rect.y = y;
                    }
                    rect.height = calculateTabHeight(tabPlacement, i, fontHeight);
                    maxTabHeight = Math.max(maxTabHeight, rect.height);

                    if (rect.y != 2 + insets.top && rect.y + rect.height > returnAt) {
                        if (runCount > tabRuns.length - 1) {
                            expandTabRunsArray();
                        }
                        tabRuns[runCount] = i;
                        runCount++;
                        rect.y = y;
                    }
                    rect.x = x;
                    rect.width = maxTabWidth;

                }
                if (i == selectedIndex) {
                    selectedRun = runCount - 1;
                }
            }

            if (runCount > 1) {
                normalizeTabRuns(tabPlacement, tabCount, verticalTabRuns ? y : x, returnAt);

                selectedRun = getRunForTab(tabCount, selectedIndex);

                if (shouldRotateTabRuns(tabPlacement)) {
                    rotateTabRuns(tabPlacement, selectedRun);
                }
            }

            for (i = runCount - 1; i >= 0; i--) {
                int start = tabRuns[i];
                int next = tabRuns[i == (runCount - 1) ? 0 : i + 1];
                int end = (next != 0 ? next - 1 : tabCount - 1);
                if (!verticalTabRuns) {
                    for (j = start; j <= end; j++) {
                        rect = rects[j];
                        rect.y = y;
                        rect.x += getTabRunIndent(tabPlacement, i);
                    }
                    if (shouldPadTabRun(tabPlacement, i)) {
                        padTabRun(tabPlacement, start, end, returnAt);
                    }
                    if (tabPlacement == BOTTOM) {
                        y -= (maxTabHeight - tabRunOverlay);
                    } else {
                        y += (maxTabHeight - tabRunOverlay);
                    }
                } else {
                    for (j = start; j <= end; j++) {
                        rect = rects[j];
                        rect.x = x;
                        rect.y += getTabRunIndent(tabPlacement, i);
                    }
                    if (shouldPadTabRun(tabPlacement, i)) {
                        padTabRun(tabPlacement, start, end, returnAt);
                    }
                    if (tabPlacement == RIGHT) {
                        x -= (maxTabWidth - tabRunOverlay);
                    } else {
                        x += (maxTabWidth - tabRunOverlay);
                    }
                }
            }


            padSelectedTab(tabPlacement, selectedIndex);


            if (!leftToRight && !verticalTabRuns) {
                int rightMargin = size.width - (insets.right + tabAreaInsets.right);
                for (i = 0; i < tabCount; i++) {
                    rects[i].x = rightMargin - rects[i].x - rects[i].width;
                }
            }
        }

        protected void rotateTabRuns(int tabPlacement, int selectedRun) {
            for (int i = 0; i < selectedRun; i++) {
                int save = tabRuns[0];
                for (int j = 1; j < runCount; j++) {
                    tabRuns[j - 1] = tabRuns[j];
                }
                tabRuns[runCount - 1] = save;
            }
        }

        protected void normalizeTabRuns(int tabPlacement, int tabCount, int start, int max) {
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                int run = runCount - 1;
                boolean keepAdjusting = true;
                double weight = 1.25;

                while (keepAdjusting) {
                    int last = lastTabInRun(tabCount, run);
                    int prevLast = lastTabInRun(tabCount, run - 1);
                    int end;
                    int prevLastLen;

                    end = rects[last].x + rects[last].width;
                    prevLastLen = (int) (maxTabWidth * weight);

                    if (max - end > prevLastLen) {

                        tabRuns[run] = prevLast;
                        rects[prevLast].x = start;
                        for (int i = prevLast + 1; i <= last; i++) {
                            rects[i].x = rects[i - 1].x + rects[i - 1].width;
                        }

                    } else if (run == runCount - 1) {
                        keepAdjusting = false;
                    }
                    if (run - 1 > 0) {
                        run -= 1;
                    } else {
                        run = runCount - 1;
                        weight += .25;
                    }
                }
            }
        }

        protected void padTabRun(int tabPlacement, int start, int end, int max) {
            Rectangle lastRect = rects[end];
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                int runWidth = (lastRect.x + lastRect.width) - rects[start].x;
                int deltaWidth = max - (lastRect.x + lastRect.width);
                float factor = (float) deltaWidth / (float) runWidth;

                for (int j = start; j <= end; j++) {
                    Rectangle pastRect = rects[j];
                    if (j > start) {
                        pastRect.x = rects[j - 1].x + rects[j - 1].width;
                    }
                    pastRect.width += Math.round((float) pastRect.width * factor);
                }
                lastRect.width = max - lastRect.x;
            } else {
                int runHeight = (lastRect.y + lastRect.height) - rects[start].y;
                int deltaHeight = max - (lastRect.y + lastRect.height);
                float factor = (float) deltaHeight / (float) runHeight;

                for (int j = start; j <= end; j++) {
                    Rectangle pastRect = rects[j];
                    if (j > start) {
                        pastRect.y = rects[j - 1].y + rects[j - 1].height;
                    }
                    pastRect.height += Math.round((float) pastRect.height * factor);
                }
                lastRect.height = max - lastRect.y;
            }
        }

        protected void padSelectedTab(int tabPlacement, int selectedIndex) {

        }
    }

    private class TabbedPaneScrollLayout extends TabbedPaneLayout {

        @Override
        protected int preferredTabAreaHeight(int tabPlacement, int width) {
            return calculateMaxTabHeight(tabPlacement);
        }

        @Override
        protected int preferredTabAreaWidth(int tabPlacement, int height) {
            return calculateMaxTabWidth(tabPlacement);
        }

        @Override
        public void layoutContainer(Container parent) {
            int tabPlacement = tabPane.getTabPlacement();
            int tc = tabPane.getTabCount();
            Insets insets = tabPane.getInsets();
            int selectedIndex = tabPane.getSelectedIndex();
            Component visibleComponent = getVisibleComponent();

            calculateLayoutInfo();

            Component selectedComponent = null;
            if (selectedIndex < 0) {
                if (visibleComponent != null) {

                    setVisibleComponent(null);
                }
            } else {
                try {
                    selectedComponent = tabPane.getComponentAt(selectedIndex);
                } catch (Exception ex) {

                }
            }
            boolean shouldChangeFocus = false;


            if (selectedComponent != null) {
                if (selectedComponent != visibleComponent && visibleComponent != null) {
                    if (SwingUtilities.findFocusOwner(visibleComponent) != null) {
                        shouldChangeFocus = true;
                    }
                }
                setVisibleComponent(selectedComponent);
            }
            int tx, ty, tw, th; 
            int cx, cy, cw, ch; 
            Insets contentInsets = getContentBorderInsets(tabPlacement);
            Rectangle bounds = tabPane.getBounds();
            int numChildren = tabPane.getComponentCount();

            int space = 60;
            if ((numChildren > 0) && (tc > 0)) {
                switch (tabPlacement) {
                    case LEFT:
                        tw = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        th = bounds.height - insets.top - insets.bottom;
                        tx = insets.left;
                        ty = insets.top;

                        cx = tx + tw + contentInsets.left;
                        cy = ty + contentInsets.top;
                        cw = bounds.width - insets.left - insets.right - tw - contentInsets.left - contentInsets.right;
                        ch = bounds.height - insets.top - insets.bottom - contentInsets.top - contentInsets.bottom;
                        break;
                    case RIGHT:
                        tw = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        th = bounds.height - insets.top - insets.bottom;
                        tx = bounds.width - insets.right - tw;
                        ty = insets.top;

                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        cw = bounds.width - insets.left - insets.right - tw - contentInsets.left - contentInsets.right;
                        ch = bounds.height - insets.top - insets.bottom - contentInsets.top - contentInsets.bottom;
                        break;
                    case BOTTOM:
                        tw = bounds.width - insets.left - insets.right;
                        th = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        tx = insets.left;
                        ty = bounds.height - insets.bottom - th;

                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        cw = bounds.width - insets.left - insets.right - contentInsets.left - contentInsets.right;
                        ch = bounds.height - insets.top - insets.bottom - th - contentInsets.top - contentInsets.bottom;
                        break;
                    case TOP:
                    default:
                        tw = bounds.width - insets.left - insets.right;
                        th = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        tx = insets.left;
                        ty = insets.top;

                        cx = tx + contentInsets.left;
                        cy = ty + th + contentInsets.top;
                        cw = bounds.width - insets.left - insets.right - contentInsets.left - contentInsets.right;
                        ch = bounds.height - insets.top - insets.bottom - th - contentInsets.top - contentInsets.bottom;
                }
                for (int i = 0; i < numChildren; i++) {
                    Component child = tabPane.getComponent(i);
                    if (child instanceof ScrollableTabViewport) {
                        JViewport viewport = (JViewport) child;
                        Rectangle viewRect = viewport.getViewRect();
                        int vw = tw;
                        int vh = th;
                        switch (tabPlacement) {
                            case LEFT:
                            case RIGHT:
                                int totalTabHeight = rects[tc - 1].y + rects[tc - 1].height;
                                if (totalTabHeight > th) {
                                    vh = Math.max(th - space, space);
                                    if (totalTabHeight - viewRect.y <= vh) {
                                        vh = totalTabHeight - viewRect.y;
                                    }
                                }
                                break;
                            case BOTTOM:
                            case TOP:
                            default:
                                int totalTabWidth = rects[tc - 1].x + rects[tc - 1].width;
                                if (totalTabWidth > tw) {
                                    vw = Math.max(tw - space, space);
                                    if (totalTabWidth - viewRect.x <= vw) {
                                        vw = totalTabWidth - viewRect.x;
                                    }
                                }
                        }

                        child.setBounds(tx, ty, vw, vh);

                    } else if (child instanceof ScrollableTabButton) {
                        ScrollableTabButton scrollbutton = (ScrollableTabButton) child;
                        Dimension bsize = scrollbutton.getPreferredSize();
                        int bx = 0;
                        int by = 0;
                        int bw = bsize.width;
                        int bh = bsize.height;
                        boolean visible = false;

                        switch (tabPlacement) {
                            case LEFT:
                            case RIGHT:
                                int totalTabHeight = rects[tc - 1].y + rects[tc - 1].height;
                                if (totalTabHeight > th) {
                                    int dir = scrollbutton.scrollsForward() ? SOUTH : NORTH;
                                    scrollbutton.setDirection(dir);
                                    visible = true;
                                    bx = tabPlacement == LEFT ? tw - insets.left - tabAreaInsets.bottom - bsize.width : bounds.width - insets.left - bsize.width;
                                    by = dir == SOUTH ? bounds.height - insets.bottom - 2 * bsize.height - 2 : bounds.height - insets.bottom - 3 * bsize.height - 2;
                                }
                                break;

                            case BOTTOM:
                            case TOP:
                            default:
                                int totalTabWidth = rects[tc - 1].x + rects[tc - 1].width;
                                if (totalTabWidth > tw) {
                                    int dir = scrollbutton.scrollsForward() ? EAST : WEST;
                                    scrollbutton.setDirection(dir);
                                    visible = true;
                                    bx = dir == EAST ? bounds.width - insets.left - 2 * bsize.width - 2 : bounds.width - insets.left - 3 * bsize.width - 2;
                                    by = ty + (th - bsize.height - tabAreaInsets.bottom) / 2;
                                    if (tabPlacement == BOTTOM) {
                                        by += tabAreaInsets.bottom;
                                    } else {
                                        by++;
                                    }
                                }
                        }

                        child.setVisible(visible);
                        if (visible) {
                            child.setBounds(bx, by, bw, bh);
                        }

                    } else if (child instanceof ScrollablePopupMenuTabButton) {
                        ScrollablePopupMenuTabButton button = (ScrollablePopupMenuTabButton) child;
                        Dimension bsize = button.getPreferredSize();
                        int bx = 0;
                        int by = 0;
                        int bw = bsize.width;
                        int bh = bsize.height;
                        boolean visible = false;

                        switch (tabPlacement) {
                            case LEFT:
                            case RIGHT:
                                int totalTabHeight = rects[tc - 1].y + rects[tc - 1].height;
                                if (totalTabHeight > th) {
                                    visible = true;
                                    bx = tabPlacement == LEFT ? tw - insets.left - tabAreaInsets.bottom - bsize.width : bounds.width - insets.left - bsize.width;
                                    by = bounds.height - insets.bottom - bsize.height;
                                }
                                break;

                            case BOTTOM:
                            case TOP:
                            default:
                                int totalTabWidth = rects[tc - 1].x + rects[tc - 1].width;
                                if (totalTabWidth > tw) {
                                    visible = true;
                                    bx = bounds.width - insets.left - bsize.width;
                                    by = ty + (th - bsize.height - tabAreaInsets.bottom) / 2;
                                    if (tabPlacement == BOTTOM) {
                                        by += tabAreaInsets.bottom;
                                    } else {
                                        by++;
                                    }
                                }
                        }

                        child.setVisible(visible);
                        if (visible) {
                            child.setBounds(bx, by, bw, bh);
                        }
                    } else {
                        child.setBounds(cx, cy, cw, ch);
                    }
                }
                super.layoutTabComponents();
                if (shouldChangeFocus) {
                    if (!requestFocusForVisibleComponent()) {
                        tabPane.requestFocus();
                    }
                }
            }
        }

        @Override
        protected void calculateTabRects(int tabPlacement, int tabCount) {
            FontMetrics fm = getFontMetrics();
            Dimension size = tabPane.getSize();
            Insets insets = tabPane.getInsets();
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
            int fontHeight = fm.getHeight();
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
            boolean leftToRight = Utilities.isLeftToRight(tabPane);
            int x = tabAreaInsets.left;
            int y = tabAreaInsets.top;
            int totalWidth = 0;
            int totalHeight = 0;

            switch (tabPlacement) {
                case LEFT:
                case RIGHT:
                    maxTabWidth = calculateMaxTabWidth(tabPlacement);
                    break;
                case BOTTOM:
                case TOP:
                default:
                    maxTabHeight = calculateMaxTabHeight(tabPlacement);
            }

            runCount = 0;
            selectedRun = -1;

            if (tabCount == 0) {
                return;
            }

            selectedRun = 0;
            runCount = 1;

            Rectangle rect;
            for (int i = 0; i < tabCount; i++) {
                rect = rects[i];

                if (!verticalTabRuns) {
                    if (i > 0) {
                        rect.x = rects[i - 1].x + rects[i - 1].width;
                    } else {
                        tabRuns[0] = 0;
                        maxTabWidth = 0;
                        totalHeight += maxTabHeight;
                        rect.x = x;
                    }
                    rect.width = calculateTabWidth(tabPlacement, i, fm);
                    totalWidth = rect.x + rect.width;
                    maxTabWidth = Math.max(maxTabWidth, rect.width);

                    rect.y = y;
                    rect.height = maxTabHeight/* - 2*/;

                } else {
                    if (i > 0) {
                        rect.y = rects[i - 1].y + rects[i - 1].height;
                    } else {
                        tabRuns[0] = 0;
                        maxTabHeight = 0;
                        totalWidth = maxTabWidth;
                        rect.y = y;
                    }
                    rect.height = calculateTabHeight(tabPlacement, i, fontHeight);
                    totalHeight = rect.y + rect.height;
                    maxTabHeight = Math.max(maxTabHeight, rect.height);

                    rect.x = x;
                    rect.width = maxTabWidth/* - 2*/;

                }
            }

            if (!leftToRight && !verticalTabRuns) {
                int rightMargin = size.width - (insets.right + tabAreaInsets.right);
                for (int i = 0; i < tabCount; i++) {
                    rects[i].x = rightMargin - rects[i].x - rects[i].width;
                }
            }
            tabScroller.tabPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
        }
    }

    private class ScrollableTabSupport implements ChangeListener {

        public ScrollableTabViewport viewport;
        public ScrollableTabPanel tabPanel;
        public ScrollableTabButton scrollForwardButton;
        public ScrollableTabButton scrollBackwardButton;
        public ScrollablePopupMenuTabButton popupMenuButton;
        public int leadingTabIndex;
        private final Point tabViewPosition = new Point(0, 0);

        ScrollableTabSupport(int tabPlacement) {
            viewport = new ScrollableTabViewport();
            tabPanel = new ScrollableTabPanel();

            viewport.setView(tabPanel);
            viewport.addChangeListener(this);

            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                scrollForwardButton = new ScrollableTabButton(EAST);
                scrollBackwardButton = new ScrollableTabButton(WEST);

            } else { // tabPlacement = LEFT || RIGHT
                scrollForwardButton = new ScrollableTabButton(SOUTH);
                scrollBackwardButton = new ScrollableTabButton(NORTH);
            }
            popupMenuButton = new ScrollablePopupMenuTabButton();
        }

        public void scrollForward(int tabPlacement) {
            Dimension viewSize = viewport.getViewSize();
            Rectangle viewRect = viewport.getViewRect();

            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                if (viewRect.width >= viewSize.width - viewRect.x) {
                    return; 
                }
            } else 
            if (viewRect.height >= viewSize.height - viewRect.y) {
                return;
            }
            setLeadingTabIndex(tabPlacement, leadingTabIndex + 1);
            if (tabPane != null) {
                tabPane.doLayout();
            }
        }

        public void scrollBackward(int tabPlacement) {
            if (leadingTabIndex == 0) {
                return; 
            }
            setLeadingTabIndex(tabPlacement, leadingTabIndex - 1);
            if (tabPane != null) {
                tabPane.doLayout();
            }
        }

        public void scrollTabToVisible(int tabPlacement, int index) {
            if (index <= leadingTabIndex) {
                setLeadingTabIndex(tabPlacement, index);
            } else {
                Rectangle viewRect = viewport.getViewRect();
                switch (tabPlacement) {
                    case TOP:
                    case BOTTOM: {
                        int i = index;
                        int x = viewRect.width - rects[index].width;
                        while ((i > 0) && (x - rects[i - 1].width >= 0)) {
                            i--;
                            x -= rects[i].width;
                        }
                        if (leadingTabIndex < i) {
                            setLeadingTabIndex(tabPlacement, i);
                        }
                        break;

                    }
                    case LEFT:
                    case RIGHT: {
                        int i = index;
                        int y = viewRect.height - rects[index].height;
                        while ((i > 0) && (y - rects[i - 1].height > 0)) {
                            i--;
                            y -= rects[i].height;
                        }
                        if (leadingTabIndex < i) {
                            setLeadingTabIndex(tabPlacement, i);
                        }
                        break;
                    }
                }
            }
        }

        public void setLeadingTabIndex(int tabPlacement, int index) {
            leadingTabIndex = index;
            Dimension viewSize = viewport.getViewSize();
            Rectangle viewRect = viewport.getViewRect();

            switch (tabPlacement) {
                case TOP:
                case BOTTOM:
                    tabViewPosition.x = leadingTabIndex == 0 ? 0 : rects[leadingTabIndex].x;

                    if ((viewSize.width - tabViewPosition.x) < viewRect.width) {

                        Dimension extentSize = new Dimension(viewSize.width - tabViewPosition.x, viewRect.height);
                        viewport.setExtentSize(extentSize);
                    }
                    break;
                case LEFT:
                case RIGHT:
                    tabViewPosition.y = leadingTabIndex == 0 ? 0 : rects[leadingTabIndex].y;

                    if ((viewSize.height - tabViewPosition.y) < viewRect.height) {
                        Dimension extentSize = new Dimension(viewRect.width, viewSize.height - tabViewPosition.y);
                        viewport.setExtentSize(extentSize);
                    }
            }
            viewport.setViewPosition(tabViewPosition);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JViewport vp = (JViewport) e.getSource();
            int tabPlacement = tabPane.getTabPlacement();
            int tc = tabPane.getTabCount();
            Rectangle vpRect = vp.getBounds();
            Dimension viewSize = vp.getViewSize();
            Rectangle viewRect = vp.getViewRect();

            leadingTabIndex = getClosestTab(viewRect.x, viewRect.y);
            if (leadingTabIndex >= rects.length) {
                return;
            }

            if (leadingTabIndex + 1 < tc) {
                switch (tabPlacement) {
                    case TOP:
                    case BOTTOM:
                        if (rects[leadingTabIndex].x < viewRect.x) {
                            leadingTabIndex++;
                        }
                        break;
                    case LEFT:
                    case RIGHT:
                        if (rects[leadingTabIndex].y < viewRect.y) {
                            leadingTabIndex++;
                        }
                        break;
                }
            }
            Insets contentInsets = getContentBorderInsets(tabPlacement);
            switch (tabPlacement) {
                case LEFT:
                    tabPane.repaint(vpRect.x + vpRect.width, vpRect.y, contentInsets.left, vpRect.height);
                    scrollBackwardButton.setEnabled(viewRect.y > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tc - 1 && viewSize.height - viewRect.y > viewRect.height);
                    break;
                case RIGHT:
                    tabPane.repaint(vpRect.x - contentInsets.right, vpRect.y, contentInsets.right, vpRect.height);
                    scrollBackwardButton.setEnabled(viewRect.y > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tc - 1 && viewSize.height - viewRect.y > viewRect.height);
                    break;
                case BOTTOM:
                    tabPane.repaint(vpRect.x, vpRect.y - contentInsets.bottom, vpRect.width, contentInsets.bottom);
                    scrollBackwardButton.setEnabled(viewRect.x > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tc - 1 && viewSize.width - viewRect.x > viewRect.width);
                    break;
                case TOP:
                default:
                    tabPane.repaint(vpRect.x, vpRect.y + vpRect.height, vpRect.width, contentInsets.top);
                    scrollBackwardButton.setEnabled(viewRect.x > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tc - 1 && viewSize.width - viewRect.x > viewRect.width);
            }
        }

    }

    private class ScrollableTabViewport extends JViewport implements UIResource {

        public ScrollableTabViewport() {
            setScrollMode(SIMPLE_SCROLL_MODE);
            setOpaque(false);
        }

    }

    private class ScrollableTabPanel extends JPanel implements UIResource {

        public ScrollableTabPanel() {
            setLayout(null);
            setOpaque(false);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintScrollContentBorder(g, tabPane.getTabPlacement(), tabPane.getSelectedIndex(), 0, 0, getWidth(), getHeight());
            paintTabArea(g, tabPane.getTabPlacement(), tabPane.getSelectedIndex());
        }

        @Override
        public void doLayout() {
            if (getComponentCount() > 0) {
                Component child = getComponent(0);
                child.setBounds(0, 0, getWidth(), getHeight());
            }
        }
    }

    public class ArrowButton extends JButton implements SwingConstants {

        protected int direction;
        
        public ArrowButton(int direction) {
            super();
            this.direction = direction;
            this.setRequestFocusEnabled(false);
            if (simpleButtonBorder) {
                Color cLo = getLoBorderColor(0);
                Color cHi = BaseLookAndFeel.getTheme().getControlHighlight();
                setBorder(BorderFactory.createEtchedBorder(cHi, cLo));
            }
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int dir) {
            direction = dir;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int w = getSize().width;
            int h = getSize().height;
            int size = Math.min((h - 4) / 3, (w - 4) / 3);
            size = Math.max(size, 2);
            paintTriangle(g, (w - size) / 2 + 1, (h - size) / 2 + 1, size);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(17, 17);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public boolean isFocusTraversable() {
            return false;
        }

        public void paintTriangle(Graphics g, int x, int y, int size) {
            Color oldColor = g.getColor();
            int mid, i, j;
            size = Math.max(size, 2);
            mid = (size / 2) - 1;

            Color enabledColor = BaseLookAndFeel.getTheme().getButtonForegroundColor();
            Color disabledColor = BaseLookAndFeel.getTheme().getDisabledForegroundColor();

            g.translate(x, y);
            if (isEnabled()) {
                g.setColor(enabledColor);
            } else {
                g.setColor(disabledColor);
            }

            switch (direction) {
                case NORTH:
                    for (i = 0; i < size; i++) {
                        g.drawLine(mid - i, i, mid + i, i);
                    }
                    break;
                case SOUTH:
                    j = 0;
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(mid - i, j, mid + i, j);
                        j++;
                    }
                    break;
                case WEST:
                    for (i = 0; i < size; i++) {
                        g.drawLine(i, mid - i, i, mid + i);
                    }
                    break;
                case EAST:
                    j = 0;
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(j, mid - i, j, mid + i);
                        j++;
                    }
                    break;
            }
            g.translate(-x, -y);
            g.setColor(oldColor);
        }
    }

    private class ScrollableTabButton extends ArrowButton implements UIResource, SwingConstants {

        public ScrollableTabButton(int direction) {
            super(direction);
        }

        public boolean scrollsForward() {
            return direction == EAST || direction == SOUTH;
        }
    }

    private class ScrollablePopupMenuTabButton extends ArrowButton implements UIResource, SwingConstants {

        public ScrollablePopupMenuTabButton() {
            super(SOUTH);
        }
    }

    public class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            String name = e.getPropertyName();
            boolean isScrollLayout = scrollableTabLayoutEnabled();
            if ("mnemonicAt".equals(name)) {
                updateMnemonics();
                pane.repaint();
            } else if ("displayedMnemonicIndexAt".equals(name)) {
                pane.repaint();
            } else if ("indexForTitle".equals(name)) {
                int index = ((Integer) e.getNewValue());
                String title = tabPane.getTitleAt(index);
                if (BasicHTML.isHTMLString(title)) {
                    if (htmlViews == null) {  
                        htmlViews = createHTMLViewList();
                    } else {                  
                        View v = BasicHTML.createHTMLView(tabPane, title);
                        htmlViews.set(index, v);
                    }
                } else if (htmlViews != null && htmlViews.get(index) != null) {
                    htmlViews.set(index, null);
                }
                updateMnemonics();
            } else if ("tabLayoutPolicy".equals(name)) {
                BaseTabbedPaneUI.this.uninstallUI(pane);
                BaseTabbedPaneUI.this.installUI(pane);
            } else if ("background".equals(name) && isScrollLayout) {
                Color newVal = (Color) e.getNewValue();
                tabScroller.tabPanel.setBackground(newVal);
                tabScroller.viewport.setBackground(newVal);
                Color newColor = selectedColor == null ? newVal : selectedColor;
                tabScroller.scrollForwardButton.setBackground(newColor);
                tabScroller.scrollBackwardButton.setBackground(newColor);
            } else if ("indexForTabComponent".equals(name)) {
                if (tabContainer != null) {
                    tabContainer.removeUnusedTabComponents();
                }
                try {
                    Component tabComponent = getTabComponentAt(((Integer) e.getNewValue()));
                    if (tabComponent != null) {
                        if (tabContainer == null) {
                            installTabContainer();
                        } else {
                            addMyPropertyChangeListeners(tabComponent);
                            tabContainer.add(tabComponent);
                        }
                    }
                } catch (Exception ex) {
                }
                tabPane.revalidate();
                tabPane.repaint();
            } else if ("componentOrientation".equals(name)) {
                pane.revalidate();
                pane.repaint();
            } else if ("tabAreaBackground".equals(name)) {
                pane.revalidate();
                pane.repaint();
            }
        }
    }

    public class MyTabComponentListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("font".equals(evt.getPropertyName()) || "text".equals(evt.getPropertyName())) {
                tabPane.revalidate();
                tabPane.repaint();
            }
        }
    }

    public class TabSelectionHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JTabbedPane tabPane = (JTabbedPane) e.getSource();
            if (Utilities.getJavaVersion() >= 1.4) {
                if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
                    int index = tabPane.getSelectedIndex();
                    if (index >= 0) {
                        BaseTabbedPaneUI ui = (BaseTabbedPaneUI) tabPane.getUI();
                        ui.tabScroller.scrollTabToVisible(tabPane.getTabPlacement(), index);
                    }
                }
            }
            tabPane.revalidate();
            tabPane.repaint();
        }
    }

    public class TabComponentHandler implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent ce) {
            SwingUtilities.invokeLater(() -> {
                if (tabPane != null) {
                    tabPane.doLayout();
                }
            });
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
        }

        @Override
        public void componentShown(ComponentEvent ce) {

        }

        @Override
        public void componentHidden(ComponentEvent ce) {
        }
    }

    public class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (scrollableTabLayoutEnabled()) {
                MouseListener[] ml = tabPane.getMouseListeners();
                for (MouseListener ml1 : ml) {
                    ml1.mouseClicked(e);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (scrollableTabLayoutEnabled()) {
                MouseListener[] ml = tabPane.getMouseListeners();
                for (MouseListener ml1 : ml) {
                    ml1.mousePressed(e);
                }
            }
            if (!tabPane.isEnabled()) {
                return;
            }
            int tabIndex = getTabAtLocation(e.getX(), e.getY());
            if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
                if (tabIndex == tabPane.getSelectedIndex()) {
                    if (tabPane.isRequestFocusEnabled()) {
                        tabPane.requestFocus();
                        tabPane.repaint(getTabBounds(tabPane, tabIndex));
                    }
                } else {
                    tabPane.setSelectedIndex(tabIndex);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (scrollableTabLayoutEnabled()) {
                MouseListener[] ml = tabPane.getMouseListeners();
                for (MouseListener ml1 : ml) {
                    ml1.mouseReleased(e);
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (scrollableTabLayoutEnabled()) {
                MouseListener[] ml = tabPane.getMouseListeners();
                for (MouseListener ml1 : ml) {
                    ml1.mouseEntered(e);
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (scrollableTabLayoutEnabled()) {
                MouseListener[] ml = tabPane.getMouseListeners();
                for (MouseListener ml1 : ml) {
                    ml1.mouseExited(e);
                }
            }
            rolloverIndex = -1;
            if (rolloverIndex != oldRolloverIndex) {
                if ((oldRolloverIndex >= 0) && (oldRolloverIndex < tabPane.getTabCount())) {
                    tabPane.repaint(getTabBounds(tabPane, oldRolloverIndex));
                }
                if ((rolloverIndex >= 0) && (rolloverIndex < tabPane.getTabCount())) {
                    tabPane.repaint(getTabBounds(tabPane, rolloverIndex));
                }
                oldRolloverIndex = rolloverIndex;
            }
        }
    }

    public class MouseMotionHandler extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (scrollableTabLayoutEnabled()) {
                MouseMotionListener[] mml = tabPane.getMouseMotionListeners();
                for (MouseMotionListener mml1 : mml) {
                    mml1.mouseDragged(e);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (scrollableTabLayoutEnabled()) {
                MouseMotionListener[] mml = tabPane.getMouseMotionListeners();
                for (MouseMotionListener mml1 : mml) {
                    mml1.mouseMoved(e);
                }
            }
            rolloverIndex = getTabAtLocation(e.getX(), e.getY());
            if (rolloverIndex != oldRolloverIndex) {
                if ((oldRolloverIndex >= 0) && (oldRolloverIndex < tabPane.getTabCount())) {
                    tabPane.repaint(getTabBounds(tabPane, oldRolloverIndex));
                }
                if ((rolloverIndex >= 0) && (rolloverIndex < tabPane.getTabCount())) {
                    tabPane.repaint(getTabBounds(tabPane, rolloverIndex));
                }
                oldRolloverIndex = rolloverIndex;
            }
        }
    }

    public class FocusHandler extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            JTabbedPane tabPane = (JTabbedPane) e.getSource();
            int tabCount = tabPane.getTabCount();
            int selectedIndex = tabPane.getSelectedIndex();
            if (selectedIndex != -1 && tabCount > 0 && tabCount == rects.length) {
                tabPane.repaint(getTabBounds(tabPane, selectedIndex));
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTabbedPane tabPane = (JTabbedPane) e.getSource();
            int tabCount = tabPane.getTabCount();
            int selectedIndex = tabPane.getSelectedIndex();
            if (selectedIndex != -1 && tabCount > 0 && tabCount == rects.length) {
                tabPane.repaint(getTabBounds(tabPane, selectedIndex));
            }
        }
    }

    private class ContainerHandler implements ContainerListener {

        @Override
        public void componentAdded(ContainerEvent e) {
            JTabbedPane tp = (JTabbedPane) e.getContainer();
            TabbedPaneLayout layout = (TabbedPaneLayout) tp.getLayout();
            layout.layoutContainer(tp);

            Component child = e.getChild();
            if (child instanceof UIResource) {
                return;
            }
            int index = tp.indexOfComponent(child);
            String title = tp.getTitleAt(index);
            boolean isHTML = BasicHTML.isHTMLString(title);
            if (isHTML) {
                if (htmlViews == null) {
                    htmlViews = createHTMLViewList();
                } else {
                    View v = BasicHTML.createHTMLView(tp, title);
                    htmlViews.add(index, v);
                }
            } else 
            if (htmlViews != null) {
                htmlViews.add(index, null);
            } 
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
            JTabbedPane tp = (JTabbedPane) e.getContainer();
            Component child = e.getChild();
            if (child instanceof UIResource) {
                return;
            }

            Integer indexObj = (Integer) tp.getClientProperty("__index_to_remove__");
            if (indexObj != null) {
                int index = indexObj;
                if (htmlViews != null && htmlViews.size() >= index) {
                    htmlViews.remove(index);
                }
            }
        }
    }

    private ArrayList createHTMLViewList() {
        ArrayList viewList = new ArrayList();
        int count = tabPane.getTabCount();
        for (int i = 0; i < count; i++) {
            String title = tabPane.getTitleAt(i);
            if (BasicHTML.isHTMLString(title)) {
                viewList.add(BasicHTML.createHTMLView(tabPane, title));
            } else {
                viewList.add(null);
            }
        }
        return viewList;
    }

    private class TabContainer extends JPanel implements UIResource {

        private boolean notifyTabbedPane = true;

        public TabContainer() {
            super(null);
            setOpaque(false);
        }

        @Override
        public void remove(Component comp) {
            int index = tabPane.indexOfTabComponent(comp);
            PropertyChangeListener[] listeners = comp.getPropertyChangeListeners();
            for (PropertyChangeListener listener : listeners) {
                if (listener instanceof MyTabComponentListener) {
                    comp.removePropertyChangeListener(listener);
                }
            }
            super.remove(comp);
            if (notifyTabbedPane && index != -1) {
                tabPane.setTabComponentAt(index, null);
            }
        }

        private void removeUnusedTabComponents() {
            for (int i = 0; i < getComponentCount(); i++) {
                Component c = getComponent(i);
                if (!(c instanceof UIResource)) {
                    int index = tabPane.indexOfTabComponent(c);
                    if (index == -1) {
                        PropertyChangeListener[] listeners = c.getPropertyChangeListeners();
                        for (PropertyChangeListener listener : listeners) {
                            if (listener instanceof MyTabComponentListener) {
                                c.removePropertyChangeListener(listener);
                            }
                        }
                        super.remove(c);
                    }
                }
            }
        }
    }
}
