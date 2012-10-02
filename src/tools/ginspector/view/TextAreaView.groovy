package tools.ginspector.view

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.Text

import tools.ginspector.GInspector

@Mixin(MenuUtil)
class TextAreaView {
	InspectorView inspectorView
	
	Text textArea
	Menu textAreaMenu
	
	TextAreaView(inspectorView) {
		this.inspectorView = inspectorView
	}
	
	def getSelf() {
		return this.inspectorView.inspector.object
	}
	
	Binding getBinding() {
		return this.inspectorView.inspector.bindingForEvaluate()
	}
	
	def buildOn(component) {
		this.textArea = new Text(component, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL)
		GridData gridData = new GridData()
		gridData.horizontalAlignment = GridData.FILL
		gridData.verticalAlignment = GridData.FILL
		gridData.grabExcessHorizontalSpace = true
		gridData.grabExcessVerticalSpace = true
		this.textArea.setLayoutData(gridData)
		
		this.buildTexAreaMenu()
	}
	
	def buildTexAreaMenu() {
        this.textAreaMenu = this.createPopUpMenuOn(this.textArea, this.inspectorView.shell)

        // Script menu
        this.addMenuItem(this.textAreaMenu, 'Do it\tCtrl-D',  {SelectionEvent e -> this.doIt()})
        this.addMenuItem(this.textAreaMenu, 'Print it\tCtrl-P',  {SelectionEvent e -> this.printIt()})
        this.addMenuItem(this.textAreaMenu, 'Inspect it\tCtrl-I',  {SelectionEvent e -> this.inspectIt()})

        this.addSeparator(this.textAreaMenu)

        // Edit menu
        this.addMenuItem(this.textAreaMenu, 'Cut\tCtrl-X',  {SelectionEvent e -> this.cut()})
        this.addMenuItem(this.textAreaMenu, 'Copy\tCtrl-C',  {SelectionEvent e -> this.copy()})
        this.addMenuItem(this.textAreaMenu, 'Paste\tCtrl-V',  {SelectionEvent e -> this.paste()})
        this.addSeparator(this.textAreaMenu)
        this.addMenuItem(this.textAreaMenu, 'Select all\tCtrl-A',  {SelectionEvent e -> this.selectAll()})
	}

    def evaluate(aString) {
        return (new GroovyShell(this.getClass().getClassLoader(), this.getBinding())).evaluate(aString)
    }

    def displayErrorMessage(Exception ex, start, stop) {
        def errorMessage = ex.getMessage()

        def pre = this.textArea.getText(0, stop - 1)
        def post = this.textArea.getText(stop, this.textArea.getText().size())
        def newText = pre + errorMessage + post

        this.textArea.setText(newText)
        this.textArea.setSelection(pre.size(), pre.size() + errorMessage.size())
    }

    def doIt() {
        def (selectedString, start, stop) = this.textWithRangeForEvaluate()
        if (selectedString.trim().size() == 0) { return }

        try {
            this.evaluate(selectedString)
            this.textArea.setSelection(start, stop)
        } catch (Exception ex) {
            this.displayErrorMessage(ex, start, stop)
        }
    }

    def printIt() {
        def (selectedString, start, stop) = this.textWithRangeForEvaluate()
        if (selectedString.trim().size() == 0) { return }

        def resultObj = null
        try {
            resultObj = this.evaluate(selectedString)
        } catch (Exception ex) {
            this.displayErrorMessage(ex, start, stop)
            return
        }

        def pre = this.textArea.getText(0, stop - 1)
        def post = this.textArea.getText(stop, this.textArea.getText().size())
        def resultStr = resultObj.toString()
        def newText = pre + resultStr + post

        this.textArea.setText(newText)
        this.textArea.setSelection(pre.size(), pre.size() + resultStr.size())
    }

    def inspectIt() {
        def (selectedString, start, stop) = this.textWithRangeForEvaluate()
        if (selectedString.trim().size() == 0) { return }

        def resultObj = null
        try {
            resultObj = this.evaluate(selectedString)
            this.textArea.setSelection(start, stop)
        } catch (Exception ex) {
            this.displayErrorMessage(ex, start, stop)
            return
        }

        GInspector.openOn(resultObj)
    }

    def copy() {
        this.textArea.copy()
    }

    def cut() {
        this.textArea.cut()
    }

    def paste() {
        this.textArea.paste()
    }

    def selectAll() {
        this.textArea.selectAll()
    }

    def textWithRangeForEvaluate() {
        String selectedString = this.textArea.getSelectionText()
        if (selectedString.size() > 0) {
            def sele = this.textArea.getSelection()
            return [selectedString, sele.x, sele.y]
        }

        def txt = this.textArea.getText()
        def stopPositions = (txt.findIndexValues { it == '\n'}).collect { it as int }
        stopPositions.add(txt.size() + 1)

        def lines = []
        int start = 0
        stopPositions.each {stop ->
            def str = txt.substring(start, ((stop - 1) >= 0 ? (stop - 1) : 0))
            lines << [str , start, stop - 1]
            start = stop + 1
        }

        return lines[this.textArea.getCaretLineNumber()]
    }

}
