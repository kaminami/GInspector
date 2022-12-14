package ginspector.view.menu

import groovy.swing.SwingBuilder
import javax.swing.Action
import javax.swing.JMenu
import javax.swing.JMenuBar

import ginspector.view.InspectorFrame

class DefaultMenuBarBuilder {
    SwingBuilder sb
    InspectorFrame frame

    DefaultMenuBarBuilder(InspectorFrame inspectorFrame) {
        this.frame = inspectorFrame
        this.sb = new SwingBuilder()
    }

    JMenuBar build() {
        this.useScreenMenuBar()

        JMenuBar menuBar = this.sb.menuBar()
        this.addObjectMenuOn(menuBar)
        this.addEditMenuOn(menuBar)
        this.addViewMenuOn(menuBar)
        this.addScriptMenuOn(menuBar)

        return menuBar
    }

    void addObjectMenuOn(JMenuBar menuBar) {
        JMenu menu = sb.menu('Object')
        menuBar.add(menu)

        def refreshAction = sb.action(
                name: 'Refresh',
                closure: {event -> frame.refresh()},
                accelerator: sb.shortcut('R')
        )
        menu.add(refreshAction)

        Action resumeAction = null
        resumeAction = sb.action(
                name: 'Resume',
                enabled: frame.isWaiting,
                closure: {event ->
                    frame.isWaiting = false
                    resumeAction.enabled = false
                }
        )
        menu.add(resumeAction)
    }

    void addEditMenuOn(JMenuBar menuBar) {
        JMenu menu = sb.menu('Edit')
        menuBar.add(menu)

        def copyAction = sb.action(
                name: 'Copy',
                closure: {event -> frame.copy()},
                accelerator: sb.shortcut('C')
        )
        menu.add(copyAction)

        def cutAction = sb.action(
                name: 'Cut',
                closure: {event -> frame.cut()},
                accelerator: sb.shortcut('X')
        )
        menu.add(cutAction)

        def pasteAction = sb.action(
                name: 'Paste',
                closure: {event -> frame.paste()},
                accelerator: sb.shortcut('V')
        )
        menu.add(pasteAction)
    }

    void addViewMenuOn(JMenuBar menuBar) {
        JMenu menu = sb.menu('View')
        menuBar.add(menu)

        def largerFontAction = sb.action(
                name: 'Larger Font',
                closure: {event -> frame.largerFont()},
                accelerator: sb.shortcut('shift L')
        )
        menu.add(largerFontAction)

        def smallerFontAction = sb.action(
                name: 'Smaller Font',
                closure: {event -> frame.smallerFont()},
                accelerator: sb.shortcut('shift S')
        )
        menu.add(smallerFontAction)
    }

    void addScriptMenuOn(JMenuBar menuBar) {
        JMenu menu = sb.menu('Script')
        menuBar.add(menu)

        def doItAction = sb.action(
                name: 'Do It',
                closure: {event -> frame.doIt()},
                accelerator: sb.shortcut('D')
        )
        menu.add(doItAction)

        def printItAction = sb.action(
                name: 'Print It',
                closure: {event -> frame.printIt()},
                accelerator: sb.shortcut('P')
        )
        menu.add(printItAction)

        def inspectItAction = sb.action(
                name: 'Inspect It',
                closure: {event -> frame.inspectIt()},
                accelerator: sb.shortcut('I')
        )
        menu.add(inspectItAction)
    }

    void useScreenMenuBar() {
        // noop
    }

}
