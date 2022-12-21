package ginspector.view.menu

import ginspector.view.InspectorFrame


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
