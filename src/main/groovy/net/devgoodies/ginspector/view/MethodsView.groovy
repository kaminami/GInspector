package net.devgoodies.ginspector.view

import groovy.swing.SwingBuilder

import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableRowSorter
import java.awt.Component
import java.awt.Font
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

import net.devgoodies.ginspector.GInspector



class MethodsView extends JPanel {
    static final String NAME = 'Name'
    static final String PARAMS = 'Params'
    static final String TYPE = 'Type'
    static final String ORIGIN = 'Origin'
    static final String MODIFIERS = 'Modifiers'
    static final String DECLARER = 'Declarer'
    static final String EXCEPTIONS = 'Exceptions'

    final GInspector inspector
    final InspectorFrame parentFrame

    JTable methodsTable

    MethodsView(GInspector inspector, InspectorFrame parentFrame) {
        this.inspector = inspector
        this.parentFrame = parentFrame
        this.buildView()
    }

    List<String> columnNames() {
        return [NAME, PARAMS, TYPE, ORIGIN, MODIFIERS, DECLARER, EXCEPTIONS]
    }

    void buildView() {
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS)
        this.setLayout(layout)

        this.methodsTable = this.buildMethodsTable()
        this.add(new JScrollPane(this.methodsTable))
    }

    JTable buildMethodsTable() {
        DefaultTableModel tm = this.buildTableModel()

        JTable table = new JTable(tm)
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        table.setRowSelectionAllowed(true)

        def sorter = new TableRowSorter<DefaultTableModel>(tm)
        table.setRowSorter(sorter)

        MouseListener mouseListener = {MouseEvent event ->
            handleMethodsTableMouseEvent(event)
        } as MouseListener
        table.addMouseListener(mouseListener)

        KeyListener keyListener = {KeyEvent event ->
            handleMethodsTableKeyEvent(event)
        } as KeyListener
        table.addKeyListener(keyListener)

        return table
    }

    DefaultTableModel buildTableModel() {
        def tm = new DefaultTableModel((this.columnNames() as Vector), 0) {
            public boolean isCellEditable(int row, int column) {
                return false
            }
        }

        this.buildFieldMaps().each {map ->
            def row = this.columnNames().inject([]) {List list, String columnName ->
                list << map[columnName]
            }
            tm.addRow(row as Vector)
        }

        return tm
    }

    List<Map> buildFieldMaps() {
        return this.inspector.allMethodInfo()
    }

    void handleMethodsTableMouseEvent(MouseEvent event) {
        if (! event.isPopupTrigger()) { return }
        if (this.methodsTable.selectedRow < 0) { return }

        def menu = new JPopupMenu()
        this.addMethodsTableMenuItemsOn(menu)
        menu.show(event.component, event.x, event.y)
    }

    void addMethodsTableMenuItemsOn(JPopupMenu menu) {
        SwingBuilder sb = new SwingBuilder()

        def copyMethodNameAction = sb.action(
                name: 'Copy Method Name',
                closure: {event -> this.copySelectedMethodName()}
        )

        menu.add(copyMethodNameAction)
    }

    void copySelectedMethodName() {
        int selectedRowIndex = this.methodsTable.selectedRow
        if (selectedRowIndex < 0) { return }

        int columnIndex = this.indexOfColumnNamed(NAME)
        String methodName = this.methodsTable.getValueAt(selectedRowIndex, columnIndex)
        if (methodName.isEmpty()) { return }

        Toolkit kit = Toolkit.getDefaultToolkit()
        Clipboard clip = kit.getSystemClipboard()
        StringSelection ss = new StringSelection(methodName)
        clip.setContents(ss, ss);
    }

    int indexOfColumnNamed(String columnName) {
        def tb = this.methodsTable.getModel()
        for (int i = 0; i < tb.getColumnCount(); i++) {
            String nm = tb.getColumnName(i)
            if (nm == columnName) { return i }
        }

        return -1;
    }

    void handleMethodsTableKeyEvent(KeyEvent event) {
        if (event.getID() != KeyEvent.KEY_RELEASED) { return; }

        String prefix = event.keyChar.toString().toUpperCase()
        this.searchRowFor(prefix)
    }

    void searchRowFor(String prefix) {
        def table = this.methodsTable
        int from = Math.max(table.selectedRow, -1)

        for (int i = from + 1; i < table.model.size(); i++) {
            def value = table.getValueAt(i, 0)
            if (value.toString().toUpperCase().startsWith(prefix)) {
                this.selectThenScrollTo(table, i)
                return
            }
        }

        for (int i = 0; i < from; i++) {
            def value = table.getValueAt(i, 0)
            if (value.toString().toUpperCase().startsWith(prefix)) {
                this.selectThenScrollTo(table, i)
                return
            }
        }
    }

    void selectThenScrollTo(JTable table, int rowIndex) {
        table.setRowSelectionInterval(rowIndex, rowIndex)
        table.scrollRectToVisible(table.getCellRect(rowIndex, 0, true))
    }

    void refresh() {
        // noop
    }

    void applyCurrentFont() {
        Font newFont = this.parentFrame.currentFont

        def table = this.methodsTable
        table.tableHeader.font = newFont
        table.font = newFont

        for (int row = 0; row < table.getRowCount(); row++)
        {
            int rowHeight = table.getRowHeight();
            Component component = table.prepareRenderer(table.getCellRenderer(row, 0), row, 0);
            rowHeight = Math.max(rowHeight, component.getPreferredSize().height);

            table.setRowHeight(row, rowHeight);
        }
    }
}
