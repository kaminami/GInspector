package net.devgoodies.ginspector.view.menu

import net.devgoodies.ginspector.view.InspectorFrame


class MacOSXMenuBarBuilder extends DefaultMenuBarBuilder {

    MacOSXMenuBarBuilder(InspectorFrame inspectorFrame) {
        super(inspectorFrame)
    }

    @Override
    void useScreenMenuBar() {
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", this.frame.inspector.appName)
    }
}
