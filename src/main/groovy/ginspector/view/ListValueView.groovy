package ginspector.view

import ginspector.GInspector


class ListValueView extends AbstractValueView {
    static final String INDEX = 'Index'
    static final String VALUE = 'Value'
    static final String VALUE_TYPE = 'Value Type'

    ListValueView(GInspector inspector, InspectorFrame parentFrame) {
        super(inspector, parentFrame)
    }

    @Override
    List<String> columnNames() {
        return [INDEX, VALUE, VALUE_TYPE]
    }

    @Override
    List<Map> buildFieldMaps() {
        List self = this.inspector.object.toList()
        List fieldMaps = []

        self.eachWithIndex {value, idx ->
            Map map = [:]
            map[INDEX] = idx
            map[VALUE] = value
            map[VALUE_TYPE] = value.getClass().getSimpleName()

            fieldMaps.add(map)
        }

        return fieldMaps
    }

    @Override
    Object selectedObject() {
        int index = valueTable.getSelectedRow()
        def obj = this.inspector.object[index]
        return obj
    }
}
