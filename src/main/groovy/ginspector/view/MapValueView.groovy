package ginspector.view

import groovy.swing.SwingBuilder

import javax.swing.JPopupMenu

import ginspector.GInspector


class MapValueView extends AbstractValueView {
    static final String KEY = 'Key'
    static final String KEY_TYPE = 'Key Type'
    static final String VALUE = 'Value'
    static final String VALUE_TYPE = 'Value Type'

    MapValueView(GInspector inspector, InspectorFrame parentFrame) {
        super(inspector, parentFrame)
    }

    @Override
    List<String> columnNames() {
        return [KEY, KEY_TYPE, VALUE, VALUE_TYPE]
    }

    @Override
    List<Map> buildFieldMaps() {
        Map self =  this.inspector.object
        def fieldMaps = []

        self.each {key, value ->
            def map = [:]
            map[KEY] = key
            map[KEY_TYPE] = key.getClass().getName()
            map[VALUE] = value
            map[VALUE_TYPE] = value.getClass().getName()
            fieldMaps.add(map)
        }

        return fieldMaps
    }

    void addValueTableMenuItemsOn(JPopupMenu menu) {
        SwingBuilder sb = new SwingBuilder()

        def inspectKeyAction = sb.action(
                name: 'Inspect Key',
                shortDescription: 'Inspect selected key',
                closure: {event -> inspectSelectedKey()}
        )

        def inspectValueAction = sb.action(
                name: 'Inspect Value',
                shortDescription: 'Inspect selected value',
                closure: {event -> inspectSelectedValue()}
        )

        menu.add(inspectKeyAction)
        menu.add(inspectValueAction)
    }

    void inspectSelectedKey() {
        int columnIndex = this.indexOfColumnNamed(KEY)
        def obj = this.valueTable.getValueAt(this.valueTable.getSelectedRow(), columnIndex)
        GInspector.openOn(obj)
    }

    void inspectSelectedValue() {
        this.inspectSelectedObject()
    }

    @Override
    Object selectedObject() {
        // return value object
        int columnIndex = this.indexOfColumnNamed(VALUE)
        def obj = this.valueTable.getValueAt(this.valueTable.getSelectedRow(), columnIndex)
        return obj
    }
}
