package tools.ginspector.view

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.dnd.Clipboard
import org.eclipse.swt.dnd.Transfer
import org.eclipse.swt.dnd.TextTransfer
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.widgets.TableColumn
import org.eclipse.swt.widgets.TableItem
import org.eclipse.swt.custom.TableEditor
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Event
import java.text.Collator


@Mixin(MenuUtil)
class MethodsView {
	InspectorView inspectorView

	Table table
	Menu tableMenu

    List columnLabels = ['Name', 'Params', 'Type', 'Origin', 'Modifiers', 'Declarer', 'Exceptions']
	
	MethodsView(inspectorView) {
		this.inspectorView = inspectorView
	}
	
	def getSelf() {
		return this.inspectorView.inspector.object
	}
	
	def buildOn(component) {
		CTabItem tab = new CTabItem(component, SWT.NONE)
		tab.setText("Methods")
		
		SashForm sash = new SashForm(component, SWT.HORIZONTAL)
		tab.setControl(sash);

		this.buildTable(sash)
		this.buildTableMenu()

		this.updateValues()
	}
	
	def buildTable(parent) {
		this.table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION)
		this.table.setHeaderVisible(true)
		
		this.columnLabels.each {label ->
			TableColumn column = new TableColumn(table, SWT.LEFT)
			column.setText(label)
			column.setWidth(110)
		}

        this.addSortListeners(this.table)
	}

    def addSortListeners(Table aTable) {
        def columnSize = aTable.getColumns().size()
        aTable.getColumns().eachWithIndex {column, columnIndex ->
            def listener = [:]
            listener['handleEvent'] = {Event e ->
                TableColumn sortColumn = aTable.getSortColumn()
                TableColumn currentColumn = e.widget
                int direction = aTable.getSortDirection()

                if (sortColumn == currentColumn) {
                    direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP
                } else {
                    aTable.setSortColumn(currentColumn)
                    direction = SWT.UP
                }

                def items = aTable.getItems()
                for (int i = 1; i < items.size(); i++) {
                    def value1 = items[i].getText(columnIndex)
                    for (int j = 0; j < i; j++) {
                        def value2 = items[j].getText(columnIndex)
                        def compareResult = (direction == SWT.UP) ? (value1.compareTo(value2) < 0) : (value1.compareTo(value2) >= 0)
                        if (compareResult) {
                            def values = ((0 .. columnSize).collect {idx -> items[i].getText(idx)}) as String[]
                            items[i].dispose()
                            TableItem item = new TableItem(table, SWT.NONE, j)
                            item.setText(values)
                            items = table.getItems()
                            break
                        }
                    }
                }
                aTable.setSortDirection(direction)
            }
            column.addListener(SWT.Selection, listener as Listener)
        }

        aTable.setSortColumn(aTable.getColumn(0))
        aTable.setSortDirection(SWT.UP)
    }
	
	def updateValues() {
		def methodInfoList = this.inspectorView.inspector.allMethodInfo()
		
		methodInfoList.each {map ->
			def item = new TableItem(this.table, SWT.NONE)
            this.columnLabels.eachWithIndex {label, idx ->
                item.setText(idx, map[label])
            }
		}
	}
	
	def buildTableMenu() {
        this.tableMenu = this.createPopUpMenuOn(this.table, this.inspectorView.shell)
        this.addMenuItem(this.tableMenu, 'Copy method name',  {SelectionEvent e -> this.copyMethodName()})
	}

    def copyMethodName() {
        if (this.table.getSelection().size() == 0) { return }

        TableItem selectedItem = this.table.getSelection().first()
        String selector = selectedItem.getText(0)

        Clipboard clipboard = new Clipboard(this.inspectorView.display)
        clipboard.setContents([selector] as Object[], [TextTransfer.getInstance()] as Transfer[]);
    }
}
