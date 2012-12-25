/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.workbench.main.impl;

import org.fourthline.cling.support.shared.CenterWindow;
import org.fourthline.cling.support.shared.log.LogView;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.browser.BrowserView;
import org.fourthline.cling.workbench.info.DevicesView;
import org.fourthline.cling.workbench.main.WorkbenchToolbarView;
import org.fourthline.cling.workbench.main.WorkbenchView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author Christian Bauer
 */
@Singleton
public class WorkbenchViewImpl extends JFrame implements WorkbenchView {

    @Inject
    protected WorkbenchToolbarView toolbarView;

    @Inject
    protected BrowserView browserView;

    @Inject
    protected DevicesView deviceInfosView;

    @Inject
    protected LogView logView;

    final protected JSplitPane northSouthSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    final protected JSplitPane eastWestSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    final protected JPanel browserPanel = new JPanel(new BorderLayout());
    final protected JPanel infoPanel = new JPanel(new BorderLayout());

    protected Presenter presenter;
    protected WindowListener windowListener;

    public WorkbenchViewImpl() throws HeadlessException {
        super(Workbench.APPNAME);
    }

    @PostConstruct
    public void init() {

        browserPanel.add(browserView.asUIComponent(), BorderLayout.CENTER);
        browserPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
        browserPanel.setPreferredSize(new Dimension(250, 250));
        browserPanel.setMinimumSize(new Dimension(250, 250));

        infoPanel.add(deviceInfosView.asUIComponent(), BorderLayout.CENTER);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        infoPanel.setPreferredSize(new Dimension(675, 200));
        infoPanel.setMinimumSize(new Dimension(650, 200));

        eastWestSplitPane.setBorder(BorderFactory.createEmptyBorder());
        eastWestSplitPane.setResizeWeight(0);
        eastWestSplitPane.setLeftComponent(browserPanel);
        eastWestSplitPane.setRightComponent(infoPanel);

        northSouthSplitPane.setBorder(BorderFactory.createEmptyBorder());
        northSouthSplitPane.setResizeWeight(0.8);
        northSouthSplitPane.setTopComponent(eastWestSplitPane);
        northSouthSplitPane.setBottomComponent(logView.asUIComponent());

        add(toolbarView.asUIComponent(), BorderLayout.NORTH);
        add(northSouthSplitPane, BorderLayout.CENTER);

        setSize(new Dimension(975, 700));
        setMinimumSize(new Dimension(975, 450));
        setResizable(true);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;

        if (windowListener != null)
            removeWindowListener(windowListener);
        this.windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                dispose();
                if (presenter != null)
                    presenter.onViewDisposed();
            }
        };
        addWindowListener(windowListener);
    }

    @Override
    public void dispose() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WorkbenchViewImpl.super.dispose();
            }
        });
    }

    @Override
    public void setVisible() {
        Application.center(this);
        setVisible(true);
    }

    public void onCenterWindow(@Observes CenterWindow centerWindow) {
        Application.center(centerWindow.getWindow(), this);
    }

}
