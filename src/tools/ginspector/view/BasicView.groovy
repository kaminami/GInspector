package tools.ginspector.view

import java.lang.reflect.Field
import java.lang.reflect.Modifier
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
class BasicView {
	InspectorView inspectorView

	Table table
	Text valueArea
	
	Menu tableMenu
	
	BasicView(inspectorView) {
		this.inspectorView = inspectorView
	}
	
	def getSelf() {
		return this.inspectorView.inspector.object
	}

    def getMyName() {
        return this.inspectorView.inspector.pseudoVarName
    }


    def getBinding() {
		return this.inspectorView.inspector.bindingForEvaluate()
	}
	
	def buildOn(component) {
		CTabItem tab = new CTabItem(component, SWT.NONE)
		tab.setText("Basic")

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
		
		['Name', 'Type', 'Modifiers', 'Declarer', 'Value'].each {label ->
			TableColumn column = new TableColumn(table, SWT.LEFT)
			column.setText(label)
			column.setWidth(130)
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
		
		def itemOfSelf = new TableItem(this.table, SWT.NONE)
		def self = this.getSelf()
		itemOfSelf.setText(0, this.getMyName())
		itemOfSelf.setText(1, (self ? self.getClass().getName() : 'n/a'))
		itemOfSelf.setText(2, '')
		itemOfSelf.setText(3, '')
		itemOfSelf.setText(4, (self ? self.toString() : 'null'))
		
		Binding binding = this.getBinding()

		def fieldMaps = []
		
		def metaFields = this.inspectorView.inspector.allMetaFields()
		metaFields.sort{it.getName()}.each {PropertyValue pv ->
			def map = [:]
			map['Name'] = pv.getName()
			map['Type'] = pv.getType().getName()
			map['Modifiers'] = 'public'
			map['Declarer'] = 'n/a'
			map['Value'] = binding.getProperty(pv.getName()).toString()
			
			fieldMaps.add(map)
		}
		
		def fields = this.inspectorView.inspector.allFields() 
		fields.sort{it.getName()}.each {Field field ->
			def map = [:]
			map['Name'] = field.getName()
			map['Type'] = field.getType().getName()
			map['Modifiers'] = Modifier.toString(field.getModifiers())
			map['Declarer'] = field.getDeclaringClass().getName()
			map['Value'] = binding.getProperty(field.getName()).toString()
			
			fieldMaps.add(map)
		}
		
		fieldMaps.sort {it['Name']}.each {Map map ->
			def item = new TableItem(this.table, SWT.NONE)
			item.setText(0, map['Name'])
			item.setText(1, map['Type'])
			item.setText(2, map['Modifiers'])
			item.setText(3, map['Declarer'])
			item.setText(4, map['Value'])
		}
	}
	
	def buildTableMenu() {
		this.tableMenu = this.createPopUpMenuOn(this.table, this.inspectorView.shell)
        this.addMenuItem(this.tableMenu, 'Inspect selected object\tCtrl-Q', SWT.CTRL + ('Q' as char),  {SelectionEvent e -> this.inspectItem()})
    }

    def inspectItem() {
        if (this.table.getSelection().size() == 0) { return }

        TableItem selectedItem = this.table.getSelection().first()
        String selector = selectedItem.getText(0)
        def selectedModel = this.getBinding()[selector]

        GInspector.openOn(selectedModel)
    }

	def addSelectedEventListener() {
        def listener = [:]
        listener['widgetSelected'] = {SelectionEvent e ->
            TableItem selectedItem = this.table.getSelection().first()
            String selector = selectedItem.getText(0)
            def selectedObj = this.getBinding()[selector]

            this.valueArea.setText(selectedObj.toString())
        }
		this.table.addSelectionListener(listener as SelectionAdapter)
	}
}
