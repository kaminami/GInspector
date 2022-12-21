package ginspector.view

import javax.swing.JFrame
import javax.swing.JMenuBar
import javax.swing.JSplitPane
import javax.swing.JTabbedPane
import javax.swing.UIManager
import java.awt.Font

import ginspector.GInspector
import ginspector.view.menu.MainMenuBar


class InspectorFrame extends JFrame {
    static final int DEFAULT_WIDTH = 600
    static final int DEFAULT_HEIGHT = 480

    Font currentFont = UIManager.defaults.getFont("TextField.font")

    final GInspector inspector

    def methodsView
    def basicValueView
    def extraValueView

    EditorView editorView

    boolean isWaiting = false

    InspectorFrame(Object obj, boolean waiting = false) {
        this.inspector = new GInspector(obj)
        this.isWaiting = waiting

        this.initialize()
    }

    void initialize() {
        this.preBuild()
        this.doBuild()
        this.postBuild()
    }

    void preBuild() {
    }

    void postBuild() {
        this.updateTitle()
        this.refresh()
    }

    void doBuild() {
        this.buildView()
    }

    void buildView() {
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)

        this.buildMainMenu()
        this.buildToolBar()

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT)
        splitPane.setDividerLocation((DEFAULT_HEIGHT * 0.7) as int)
        this.add(splitPane)

        JTabbedPane tabView = this.buildTab()
        this.editorView = this.buildEditorView()

        splitPane.setTopComponent(tabView)
        splitPane.setBottomComponent(this.editorView)
    }

    void buildMainMenu() {
        JMenuBar menuBar = MainMenuBar.buildFor(this)
        this.setJMenuBar(menuBar)
    }

    void refresh() {
        this.basicValueView?.refresh()
        this.extraValueView?.refresh()
        this.methodsView?.refresh()
    }

    void copy() {
        this.editorView.copy()
    }

    void cut() {
        this.editorView.cut()
    }

    void paste() {
        this.editorView.paste()
    }

    void largerFont() {
        this.resizeFont(2)
    }

    void smallerFont() {
        this.resizeFont(-2)
    }

    void resizeFont(int delta) {
        int currentSize = currentFont.size
        if (currentSize <= 8) { return }

        currentFont = new Font(currentFont.family, Font.PLAIN, currentSize + delta)

        this.basicValueView?.applyCurrentFont()
        this.extraValueView?.applyCurrentFont()
        this.methodsView?.applyCurrentFont()
        this.editorView?.applyCurrentFont()
    }

    void doIt() {
        this.editorView.doIt()
    }

    void printIt() {
        this.editorView.printIt()
    }

    void inspectIt() {
        this.editorView.inspectIt()
    }

    void buildToolBar() {

    }

    JTabbedPane buildTab() {
        JTabbedPane tabbedPane = new JTabbedPane()

        this.addBasicTab(tabbedPane)

        if (this.inspector.object != null) {
            this.addExtraTab(tabbedPane)
            this.addMethodsTab(tabbedPane)
        }

        if (tabbedPane.tabCount >= 3) { // has collection tab
            tabbedPane.setSelectedIndex(1) // set collection tab current
        }

        return tabbedPane
    }

    EditorView buildEditorView() {
        return new EditorView(this.inspector, this)
    }

    void addBasicTab(JTabbedPane tabbedPane) {
        def obj = this.inspector.object
        def view = (obj == null) ? new NullValueView(this.inspector, this)
                : new BasicValueView(this.inspector, this)

        tabbedPane.addTab('Basic', view)
        this.basicValueView = view
    }

    void addMethodsTab(JTabbedPane tabbedPane) {
        def view = new MethodsView(this.inspector, this)
        tabbedPane.addTab('Methods', view)
        this.methodsView = view
    }

    def addExtraTab(JTabbedPane tabbedPane) {
        Object targetObj = this.target

        if (this.isMap(targetObj)) {
            this.addMapTab(tabbedPane)
        } else if (this.isSequenceableCollection(targetObj)) {
            this.addListTab(tabbedPane)
        } else if (this.isCollection(targetObj)) {
            this.addGenericCollectionTab(tabbedPane)
        }
    }

    void addMapTab(JTabbedPane tabbedPane) {
        MapValueView view = new MapValueView(this.inspector, this)
        tabbedPane.addTab('Map', view)
        this.extraValueView = view
    }

    void addListTab(JTabbedPane tabbedPane) {
        ListValueView view = new ListValueView(this.inspector, this)
        tabbedPane.addTab('List', view)
        this.extraValueView = view
    }

    void addGenericCollectionTab(JTabbedPane tabbedPane) {
        CollectionValueView view = new CollectionValueView(this.inspector, this)
        tabbedPane.addTab('Collection', view)
        this.extraValueView = view
    }

    void open() {
        this.setVisible(true)
    }

    void updateTitle() {
        def obj = this.target
        def str = (obj == null)
                            ? "null :${this.inspector.appName}"
                            : "${obj} (${obj.class}) :${this.inspector.appName}"

        this.setTitle(str)
    }

    Object getTarget() {
        return this.inspector.object
    }

    Boolean isMap(Object obj) {
        return (obj instanceof Map)
    }

    Boolean isSequenceableCollection(Object obj) {
        if (obj.class.isArray()) { return true }

        if (obj instanceof Range) { return false } // check before List
        if (obj instanceof List) { return true }

        return false
    }

    Boolean isCollection(Object obj) {
        if (obj instanceof Range) { return false }

        return (obj instanceof Collection)
    }
}
