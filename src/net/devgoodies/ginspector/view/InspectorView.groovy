package net.devgoodies.ginspector.view

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import org.eclipse.swt.widgets.Shell

import net.devgoodies.ginspector.GInspector
import org.eclipse.swt.custom.CTabItem

@Mixin(MenuUtil)
class InspectorView {
	
	GInspector inspector
	
	Display display
	Shell shell
	SashForm frameSash

	BasicView basicView
	MapView mapView
	MethodsView methodsView
	ListView listView
	CollectionView collectionView
	TextAreaView textAreaView

    List tabDependents = []

	InspectorView(Object obj) {
		this.inspector = new GInspector(obj)
		
		this.initialize()
	}
	
	def initialize() {
		this.preBuild()
		this.doBuild()
		this.postBuild()
	}
	
	def preBuild() {
		
	}
	
	def postBuild() {
		this.frameSash.setWeights([70, 30] as int[])
	}
	
	def doBuild() {
		this.buildView()
	}
		
	def buildView() {
		this.buildFrame()
		this.buildMainMenu()
		this.buildTab()
		this.buildTextArea()
	}

	def buildFrame() {
		this.display = new Display()
		this.shell = new Shell(this.display)
		this.shell.setText(this.inspector.object.toString())
		this.shell.setLayout(new FillLayout())
		this.shell.setSize(800, 400)
		
		this.frameSash = new SashForm(shell, SWT.VERTICAL)
	}
	
	def buildMainMenu() {
		Menu menu = new Menu(this.shell, SWT.BAR)
		this.shell.setMenuBar(menu)

        this.buildObjectMenu(menu)
        this.buildEditMenu(menu)
        this.buildScriptMenu(menu)
	}

    def buildObjectMenu(Menu menu) {
        MenuItem rootItem = new MenuItem(menu, SWT.CASCADE)
        rootItem.setText("Object")

        Menu rootMenu = new Menu(rootItem)
        rootItem.setMenu(rootMenu)

        this.addMenuItem(rootMenu, 'Refresh\tCtrl+R', SWT.CTRL + ('R' as char), {SelectionEvent e -> this.refresh()})

        this.addSeparator(rootMenu)
        def inspectItem = this.addMenuItem(rootMenu, 'Inspect selected object\tCtrl-Q', SWT.CTRL + ('Q' as char), {SelectionEvent e -> this.basicView.inspectItem()})
        inspectItem.setEnabled(false)
        this.onTabChangeDo {tabItem -> inspectItem.setEnabled(tabItem.getText() == 'Basic')}

        this.addSeparator(rootMenu)
        ['self', '_this'].each {candidate ->
            MenuItem item = this.addRadioMenuItem(rootMenu, "Use '$candidate' instead of 'this'", {SelectionEvent e -> this.setMyName(candidate)})
                item.setSelection(candidate == this.inspector.pseudoVarName)
        }
    }

    def buildEditMenu(Menu menu) {
        MenuItem rootItem = new MenuItem(menu, SWT.CASCADE)
        rootItem.setText("Edit")

        Menu rootMenu = new Menu(rootItem)
        rootItem.setMenu(rootMenu)

        this.addMenuItem(rootMenu, 'Cut\tCtrl+X', SWT.CTRL + ('X' as char), {SelectionEvent e -> this.textAreaView.cut()})
        this.addMenuItem(rootMenu, 'Copy\tCtrl+C', SWT.CTRL + ('C' as char), {SelectionEvent e -> this.textAreaView.copy()})
        this.addMenuItem(rootMenu, 'Paste\tCtrl+V', SWT.CTRL + ('V' as char), {SelectionEvent e -> this.textAreaView.paste()})
        this.addSeparator(rootMenu)
        this.addMenuItem(rootMenu, 'Select all\tCtrl+A', SWT.CTRL + ('A' as char), {SelectionEvent e -> this.textAreaView.selectAll()})
    }

    def buildScriptMenu(Menu menu) {
        MenuItem rootItem = new MenuItem(menu, SWT.CASCADE)
        rootItem.setText("Script")

        Menu rootMenu = new Menu(rootItem)
        rootItem.setMenu(rootMenu)

        this.addMenuItem(rootMenu, 'Do it\tCtrl-D', SWT.CTRL + ('D' as char), {SelectionEvent e -> this.textAreaView.doIt()})
        this.addMenuItem(rootMenu, 'Print it\tCtrl-P', SWT.CTRL + ('P' as char), {SelectionEvent e -> this.textAreaView.printIt()})
        this.addMenuItem(rootMenu, 'Inspect it\tCtrl-I', SWT.CTRL + ('I' as char), {SelectionEvent e -> this.textAreaView.inspectIt()})
    }

 	def buildTab() {
		 def tabFolder = new CTabFolder(this.frameSash, SWT.BORDER)
         tabFolder.setTabHeight(25)
         tabFolder.setSimple(true)
		
		this.addBasicTab(tabFolder)
		
		if (this.isMap(this.inspector.object)) {
			this.addMapTab(tabFolder)
		} else if (this.isSequenceableCollection(this.inspector.object)) {
			 this.addListTab(tabFolder)
		} else if (this.isCollection(this.inspector.object)) {
			this.addCollectionTab(tabFolder)
		}
		
		this.addMethodsTab(tabFolder)
		
		if (tabFolder.getTabList().size() >= 3) {
            tabFolder.setSelection(1)
		}

        this.addTabFolderListeners(tabFolder)
	}

    def addTabFolderListeners(CTabFolder aTabFolder) {
        def listener = [:]
        listener['widgetSelected'] = {SelectionEvent e ->
            this.tabChanged(e.item)
        }
        aTabFolder.addSelectionListener(listener as SelectionAdapter)
    }

    def tabChanged(CTabItem selectedTabItem) {
        this.tabDependents.each {Closure oneArgClosure ->
            oneArgClosure.call(selectedTabItem)
        }
    }

    def onTabChangeDo(Closure oneArgClosure) {
        this.tabDependents.add(oneArgClosure)
    }

	def isMap(obj) {
		return (obj instanceof Map)
	}
	
	def isSequenceableCollection(obj) {
		if (obj.getClass().isArray()) { return true }
		
		if (obj instanceof Range) { return false } // check before List
		if (obj instanceof List) { return true }
		return false
	}
	
	def isCollection(obj) {
		if (obj instanceof Range) { return false }
		return (obj instanceof Collection)
	}
	
	def addBasicTab(tabFolder) {
		this.basicView = new BasicView(this)
		this.basicView.buildOn(tabFolder)
	}
	
	def addMapTab(tabFolder) {
		this.mapView = new MapView(this)
		this.mapView.buildOn(tabFolder)
	}
	
	def addCollectionTab(tabFolder) {
		this.collectionView = new CollectionView(this)
		this.collectionView.buildOn(tabFolder)
	}
	
	def addListTab(tabFolder) {
		this.listView = new ListView(this)
		this.listView.buildOn(tabFolder)
	}
	
	def addMethodsTab(tabFolder) {
		this.methodsView = new MethodsView(this)
		this.methodsView.buildOn(tabFolder)
	}
	
	def buildTextArea() {
		this.textAreaView = new TextAreaView(this)
		this.textAreaView.buildOn(this.frameSash)
	}

    def refresh() {
        this.basicView.refresh()
        this.mapView?.refresh()
        this.listView?.refresh()
    }

    def setMyName(newName) {
        this.inspector.pseudoVarName = newName
        this.refresh()
    }

	def openWait() {
		this.shell.open()
		while (! this.shell.isDisposed()) {
			if (! this.display.readAndDispatch())
				this.display.sleep()
		}
		this.display.dispose()
	}
}

