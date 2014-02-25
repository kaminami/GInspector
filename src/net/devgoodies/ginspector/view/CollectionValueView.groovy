package net.devgoodies.ginspector.view

import net.devgoodies.ginspector.GInspector


class CollectionValueView extends AbstractValueView {
    static final String VALUE = 'Value'
    static final String VALUE_TYPE = 'Value Type'

    CollectionValueView(GInspector inspector, InspectorFrame parentFrame) {
        super(inspector, parentFrame)
    }

    @Override
    List<String> columnNames() {
        return [VALUE, VALUE_TYPE]
    }

    @Override
    List<Map> buildFieldMaps() {
        List self =  this.inspector.object.toList()
        def fieldMaps = []

        self.each {value ->
            def map = [:]
            map[VALUE] = value
            map[VALUE_TYPE] = value.getClass().getName()

            fieldMaps.add(map)
        }

        return fieldMaps
    }

    @Override
    Object selectedObject() {
        int columnIndex = this.indexOfColumnNamed(VALUE)
        def obj = this.valueTable.getValueAt(this.valueTable.getSelectedRow(), columnIndex)
        return obj
    }
}
