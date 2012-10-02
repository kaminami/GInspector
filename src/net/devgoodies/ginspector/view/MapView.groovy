package net.devgoodies.ginspector.view

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

import net.devgoodies.ginspector.GInspector

@Mixin(MenuUtil)
class MapView {
	InspectorView inspectorView

	Table table
	Text valueArea
	Menu tableMenu
	Map tableCache = [:]
	
	MapView(inspectorView) {
		this.inspectorView = inspectorView
	}
	
	Map getSelf() {
		return this.inspectorView.inspector.object as Map
	}
	
	def buildOn(component) {
		CTabItem tab = new CTabItem(component, SWT.NONE)
		tab.setText("Map")

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
		
		['Key', 'Key type', 'Value', 'Value type'].each {label ->
			TableColumn column = new TableColumn(table, SWT.LEFT)
			column.setText(label)
			column.setWidth(160)
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
		
		Map map = this.getSelf()
		map.each {key, value ->
			def item = new TableItem(this.table, SWT.NONE)
			item.setText(0, key.toString())
			item.setText(1, key.getClass().getName())
			item.setText(2, value.toString())
			item.setText(3, value.getClass().getName())
			
			this.tableCache[item] = [key, value]
		}
	}
	
	def buildTableMenu() {
        this.tableMenu = this.createPopUpMenuOn(this.table, this.inspectorView.shell)
        this.addMenuItem(this.tableMenu, 'Inspect key',  {SelectionEvent e -> this.inspectKey()})
        this.addMenuItem(this.tableMenu, 'Inspect value',  {SelectionEvent e -> this.inspectValue()})
	}

    def inspectKey() {
        if (this.table.getSelection().size() == 0) { return }

        TableItem selectedItem = this.table.getSelection().first()
        def key = (this.tableCache[selectedItem]).first()
        GInspector.openOn(key)
    }

    def inspectValue() {
        if (this.table.getSelection().size() == 0) { return }

        TableItem selectedItem = this.table.getSelection().first()
        def value = (this.tableCache[selectedItem]).last()
        GInspector.openOn(value)
    }

	def addSelectedEventListener() {
        def listener = [:]
        listener['widgetSelected'] = {SelectionEvent e ->
            TableItem selectedItem = this.table.getSelection().first()
            def key = (this.tableCache[selectedItem]).first()
            this.valueArea.setText(this.getSelf()[key].toString())
        }
		this.table.addSelectionListener(listener as SelectionAdapter)
	}
}
