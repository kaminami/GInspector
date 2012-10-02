package tools.ginspector.view

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.widgets.TableColumn
import org.eclipse.swt.widgets.TableItem
import org.eclipse.swt.widgets.Text

import tools.ginspector.GInspector

@Mixin(MenuUtil)
class CollectionView {
	InspectorView inspectorView

	Table table
	Text valueArea
	Menu tableMenu	
	Map tableCache = [:]
	
	CollectionView(inspectorView) {
		this.inspectorView = inspectorView
	}
	
	def getSelf() {
		return this.inspectorView.inspector.object
	}
	
	def buildOn(component) {
		CTabItem tab = new CTabItem(component, SWT.NONE)
		tab.setText("Collection")

		SashForm sash = new SashForm(component, SWT.HORIZONTAL)
		tab.setControl(sash);

		this.buildTable(sash)
		this.buildValueArea(sash)
		sash.setWeights([80, 20] as int[])
		
		this.buildTableMenu()
		
		this.refresh()
	}
	
	def buildTable(parent) {
		this.table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION)
		this.table.setHeaderVisible(true)
		
		['Index', 'Value', 'Type'].each {label ->
			TableColumn column = new TableColumn(table, SWT.LEFT)
			column.setText(label)
			column.setWidth(200)
		}
		
		this.addSelectedEventListener()
	}
	
	def buildValueArea(parent) {
		this.valueArea = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL)
		GridData gridData = new GridData()
		gridData.horizontalAlignment = GridData.FILL
		gridData.verticalAlignment = GridData.FILL
		gridData.grabExcessHorizontalSpace = true
		gridData.grabExcessVerticalSpace = true
		this.valueArea.setLayoutData(gridData)
	}

	def refresh() {
		this.table.removeAll()
		this.tableCache = [:]
		
		List list = this.getSelf().toList()
		list.each {value ->
			def item = new TableItem(this.table, SWT.NONE)
			item.setText(0, value.toString())
			item.setText(1, value.getClass().getName())
			
			this.tableCache[item] = value
		}
	}
	
	def buildTableMenu() {
		this.tableMenu = this.createPopUpMenuOn(this.table, this.inspectorView.shell)
		this.addMenuItem(this.tableMenu, 'Inspect',  {SelectionEvent e -> this.inspectItem()})
	}

	def inspectItem() {
		if (this.table.getSelection().size() == 0) { return }

		TableItem selectedItem = this.table.getSelection().first()
		def value = this.tableCache[selectedItem]
		GInspector.openOn(value)
	}

	def addSelectedEventListener() {
		def listener = [:]
		listener['widgetSelected'] = {SelectionEvent e ->
			TableItem selectedItem = this.table.getSelection().first()
			def value = this.tableCache[selectedItem]
			this.valueArea.setText(value.toString())
		}
		this.table.addSelectionListener(listener as SelectionAdapter)
	}
}


