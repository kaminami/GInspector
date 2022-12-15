package ginspector.view

import groovy.swing.SwingBuilder

import javax.swing.Action
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTextArea
import java.awt.Font

import ginspector.GInspector


class EditorView extends JPanel {
    final GInspector inspector
    final InspectorFrame parentFrame

    JTextArea editorArea

    EditorView(GInspector inspector, InspectorFrame parentFrame) {
        this.inspector = inspector
        this.parentFrame = parentFrame

        this.buildView()
    }

    void buildView() {
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS)
        this.setLayout(layout)

        this.editorArea = this.buildEditorArea()
        this.add(new JScrollPane(this.editorArea))
    }

    JTextArea buildEditorArea() {
        SwingBuilder sb = new SwingBuilder()

        JTextArea textArea = sb.textArea()
        textArea.setWrapStyleWord(true)
        textArea.setLineWrap(true)
        textArea.setEditable(true)

        JPopupMenu menu = sb.popupMenu()
        this.addEditorMenuItemsOn(menu)
        textArea.setComponentPopupMenu(menu)

        return textArea
    }

    void addEditorMenuItemsOn(JPopupMenu menu) {
        SwingBuilder sb = new SwingBuilder()

        Action copyAction = sb.action(
                name: 'Copy',
                closure: {event -> this.copy()}
        )

        Action cutAction = sb.action(
                name: 'Cut',
                closure: {event -> this.cut()}
        )

        Action pasteAction = sb.action(
                name: 'Paste',
                closure: {event -> this.paste()}
        )

        Action doItAction = sb.action(
                name: 'Do It',
                closure: {event -> this.doIt()}
        )

        Action printItAction = sb.action(
                name: 'Print It',
                closure: {event -> this.printIt()}
        )

        Action inspectItAction = sb.action(
                name: 'Inspect It',
                closure: {event -> this.inspectIt()}
        )

        menu.add(copyAction)
        menu.add(cutAction)
        menu.add(pasteAction)
        menu.addSeparator()
        menu.add(doItAction)
        menu.add(printItAction)
        menu.add(inspectItAction)
    }

    String selectedTextForEvaluate() {
        JTextArea ea = this.editorArea

        String st = ea.selectedText ?: ''
        if (! st.isEmpty()) { return st }

        int lineNum = ea.getLineOfOffset(ea.caretPosition)
        int start = ea.getLineStartOffset(lineNum)
        int stop = ea.getLineEndOffset(lineNum)
        List lines = ea.text.substring(start, stop).readLines()
        String extracted = lines.isEmpty() ? '' : lines.first() // remove LF, CR, CRLF

        ea.select(start, start + extracted.size())

        return ea.selectedText ?: ''
    }

    void doIt() {
        String groovyCode = this.selectedTextForEvaluate()
        if (groovyCode.isEmpty()) { return }

        try {
            this.inspector.evaluate(groovyCode)
        } catch (Exception ex) {
            this.displayThenSelectMessage(ex.getMessage())
        }
    }

    void printIt() {
        String groovyCode = this.selectedTextForEvaluate()
        if (groovyCode.isEmpty()) { return }

        try {
            def result = this.inspector.evaluate(groovyCode)
            this.displayThenSelectMessage(result.toString())
        } catch (Exception ex) {
            this.displayThenSelectMessage(ex.getMessage())
        }
    }

    void inspectIt() {
        String groovyCode = this.selectedTextForEvaluate()
        if (groovyCode.isEmpty()) { return }

        try {
            def result = this.inspector.evaluate(groovyCode)
            GInspector.openOn(result)
        } catch (Exception ex) {
            this.displayThenSelectMessage(ex.getMessage())
        }
    }

    void displayThenSelectMessage(String message) {
        int selectionEnd = this.editorArea.selectionEnd

        this.editorArea.insert(message, selectionEnd)
        this.editorArea.select(selectionEnd, selectionEnd + message.size())
    }

    void copy() {
        this.editorArea.copy()
    }

    void cut() {
        this.editorArea.cut()
    }

    void paste() {
        this.editorArea.paste()
    }

    void applyCurrentFont() {
        Font newFont = this.parentFrame.currentFont
        this.editorArea.font = newFont
    }

}
