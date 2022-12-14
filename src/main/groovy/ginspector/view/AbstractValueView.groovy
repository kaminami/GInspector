package ginspector.view

import groovy.swing.SwingBuilder

import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.JTextArea
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableRowSorter
import java.awt.Component
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

import ginspector.GInspector


abstract class AbstractValueView extends JPanel {
    final GInspector inspector
    final InspectorFrame parentFrame

    JTable valueTable
    JTextArea valueTextArea

    AbstractValueView(GInspector inspector, InspectorFrame parentFrame) {
        this.inspector = inspector
        this.parentFrame = parentFrame

        this.buildView()
    }

    abstract Object selectedObject()
    abstract List<String> columnNames()
    abstract List<Map> buildFieldMaps()


    void buildView() {
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS)
        this.setLayout(layout)

        this.valueTable = this.buildValueTable()
        this.valueTextArea = this.buildValueTextArea()


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        splitPane.setDividerLocation((InspectorFrame.DEFAULT_WIDTH * 0.7) as int)
        this.add(splitPane)

        splitPane.setLeftComponent(new JScrollPane(this.valueTable))
        splitPane.setRightComponent(new JScrollPane(this.valueTextArea))
    }

    DefaultTableModel buildTableModel() {
        def tm = new DefaultTableModel((this.columnNames() as Vector), 0) {
            public boolean isCellEditable(int row, int column) {
                return false
            }
        }

        return tm
    }

    JTable buildValueTable() {
        def tm = this.buildTableModel()

        JTable table = new JTable(tm)
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        table.setRowSelectionAllowed(true)

        def sorter = new TableRowSorter<AbstractTableModel>(tm)
        table.setRowSorter(sorter)

        ListSelectionListener selectionListener = {ListSelectionEvent event ->
            handleValueTableSelectionEvent(event)
        } as ListSelectionListener
        table.getSelectionModel().addListSelectionListener(selectionListener)

        MouseListener mouseListener = {MouseEvent event ->
            handleValueTableMouseEvent(event)
        } as MouseListener
        table.addMouseListener(mouseListener)

        KeyListener keyListener = {KeyEvent event ->
            handleValueTableKeyEvent(event)
        } as KeyListener
        table.addKeyListener(keyListener)

        return table
    }

    void handleValueTableSelectionEvent(ListSelectionEvent event) {
        int selectedIndex = this.valueTable.getSelectedRow()
        if (selectedIndex < 0) {
            this.valueTextArea.text = ''
            return
        }

        int valueColumnIndex = this.indexOfColumnNamed("Value")
        if (valueColumnIndex < 0) {
            this.valueTextArea.text = ''
            return
        }

        def val = this.valueTable.getValueAt(selectedIndex, valueColumnIndex)
        this.valueTextArea.text = val as String
    }

    int indexOfColumnNamed(String columnName) {
        def tb = this.valueTable.getModel()
        for (int i = 0; i < tb.getColumnCount(); i++) {
            String nm = tb.getColumnName(i)
            if (nm == columnName) { return i }
        }

        return -1;
    }

    void handleValueTableMouseEvent(MouseEvent event) {
        if (! event.isPopupTrigger()) { return }
        if (this.valueTable.selectedRow < 0) { return }

        def menu = new JPopupMenu()
        this.addValueTableMenuItemsOn(menu)
        menu.show(event.component, event.x, event.y)
    }

    void handleValueTableKeyEvent(KeyEvent event) {
        if (event.getID() != KeyEvent.KEY_RELEASED) { return; }

        String prefix = event.keyChar.toString().toUpperCase()
        this.searchRowFor(prefix)
    }

    void searchRowFor(String prefix) {
        def table = this.valueTable
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

    void addValueTableMenuItemsOn(JPopupMenu menu) {
        SwingBuilder sb = new SwingBuilder()

        def inspectAction = sb.action(
                name: 'Inspect',
                shortDescription: 'Inspect selected object',
                closure: {event -> inspectSelectedObject()}
        )

        menu.add(inspectAction)
    }

    JTextArea buildValueTextArea() {
        SwingBuilder sb = new SwingBuilder()

        JTextArea ta = sb.textArea()
        ta.setLineWrap(true)
        ta.setEditable(false)

        JPopupMenu menu = sb.popupMenu()
        this.addValueTextAreaMenuItemsOn(menu)
        ta.setComponentPopupMenu(menu)

        return ta
    }

    void addValueTextAreaMenuItemsOn(JPopupMenu menu) {
        SwingBuilder sb = new SwingBuilder()

        def copyAction = sb.action(
                name: 'Copy',
                closure: {event -> this.valueTextArea.copy()}
        )

        menu.add(copyAction)
    }

    void inspectSelectedObject() {
        GInspector.openOn(this.selectedObject())
    }

    void refresh() {
        this.refreshValueTable()
        this.refreshTextArea()

        this.applyCurrentFont()
    }

    void refreshValueTable() {
        DefaultTableModel tm = this.valueTable.model

        tm.size().times {
            tm.setRowCount(0)
        }

        this.buildFieldMaps().each {map ->
            def row = this.columnNames().inject([]) {List list, String columnName ->
                list << map[columnName]
            }
            tm.addRow(row as Vector)
        }
    }

    void refreshTextArea() {
        this.valueTextArea.clear()
    }

    void applyCurrentFont() {
        Font newFont = this.parentFrame.currentFont

        this.valueTextArea.font = newFont

        def table = this.valueTable
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
