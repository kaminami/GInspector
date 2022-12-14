package ginspector.view.menu

import javax.swing.JMenuBar
import javax.swing.UIManager

import ginspector.view.InspectorFrame


class MainMenuBar {
    static JMenuBar buildFor(InspectorFrame frame) {
        def builder = this.isMac() ? new MacOSXMenuBarBuilder(frame)
                                   : new DefaultMenuBarBuilder(frame)

        return builder.build()
    }

    static boolean isMac() {
        switch (UIManager.getSystemLookAndFeelClassName()) {
            case 'apple.laf.AquaLookAndFeel':
            case 'com.apple.laf.AquaLookAndFeel':
                return true

            default:
                break
        }
        return false
    }
}
