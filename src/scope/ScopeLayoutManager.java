package scope;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JLayeredPane;

public final class ScopeLayoutManager implements LayoutManager {

    private final ScopeFrame scopeFrame;

    public ScopeLayoutManager(ScopeFrame val) {
        scopeFrame = val;
    }

    @Override
    public void layoutContainer(Container parent) {
        if (parent instanceof JLayeredPane) {
            ScopePanel scopePanel = scopeFrame.getScopePanel();


            if (scopePanel != null) {
                scopePanel.setBounds(0, 0, parent.getWidth(), parent.getHeight());
            }

            DisplayConfigPanel displayConfigPanel = scopeFrame.getDisplayConfigPanel();

            if (displayConfigPanel != null) {
                // put it in the top left corner
                displayConfigPanel.setBounds(20, 10,
                        displayConfigPanel.getWidth(), displayConfigPanel.getHeight());
            }
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return null;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return null;
    }
}
