package net.devgoodies.ginspector.view

import net.devgoodies.ginspector.GInspector

class NullValueView extends AbstractValueView {
    static final String NAME = 'Name'
    static final String TYPE = 'Type'
    static final String VALUE = 'Value'

    NullValueView(GInspector inspector, InspectorFrame parentFrame) {
        super(inspector, parentFrame)
    }

    @Override
    List<String> columnNames() {
        return [NAME, TYPE, VALUE]
    }

    @Override
    List<Map> buildFieldMaps() {
        def fieldMaps = []

        def selfMap = [:]
        selfMap[NAME] = inspector.pseudoVarName;
        selfMap[TYPE] = 'n/a'
        selfMap[VALUE] = 'null'
        fieldMaps.add(selfMap)

        return fieldMaps
    }

    @Override
    Object selectedObject() {
        return null
    }
}
