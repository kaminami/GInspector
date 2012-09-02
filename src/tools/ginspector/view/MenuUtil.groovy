package tools.ginspector.view

import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.widgets.Widget
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Shell


class MenuUtil {

    MenuItem addSeparator(Menu aMenu) {
        MenuItem item = new MenuItem(aMenu, SWT.SEPARATOR)
        return item
    }

    MenuItem addMenuItem(Menu parentMenu, String label, Closure actionBlock) {
        MenuItem item = new MenuItem(parentMenu, SWT.PUSH)
        item.setText(label)
        this.addMenuSelectionListener(item, actionBlock)

        return item
    }

    MenuItem addMenuItem(Menu parentMenu, String label, int accelerator, Closure actionBlock) {
        MenuItem item = new MenuItem(parentMenu, SWT.PUSH)
        item.setText(label)
        item.setAccelerator(accelerator)
        this.addMenuSelectionListener(item, actionBlock)

        return item
    }

    MenuItem addRadioMenuItem(Menu parentMenu, String label, Closure actionBlock) {
        MenuItem item = new MenuItem(parentMenu, SWT.RADIO)
        item.setText(label)
        this.addMenuSelectionListener(item, actionBlock)

        return item
    }

    void addMenuSelectionListener(MenuItem menuItem, Closure actionBlock) {
        def listener = [:]
        listener['widgetSelected'] = actionBlock
        menuItem.addSelectionListener(listener as SelectionAdapter)
    }

    void addMenuEventListeners(Widget aWidget, Menu aMenu) {
        aWidget.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                aWidget.setMenu(aMenu)
            }
        })

        aWidget.addListener(SWT.Dispose, new Listener() {
            public void handleEvent(Event event) {
                aMenu.dispose()
            }
        })
    }

    Menu createPopUpMenuOn(Widget parent, Shell shell) {
        Menu menu = new Menu(shell, SWT.POP_UP)
        this.addMenuEventListeners(parent, menu)
        return menu
    }


}
