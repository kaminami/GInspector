package ginspector.view

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import ginspector.GInspector

class BasicValueView extends AbstractValueView {
    static final String NAME = 'Name'
    static final String TYPE = 'Type'
    static final String MODIFIERS = 'Modifiers'
    static final String DECLARER = 'Declarer'
    static final String VALUE = 'Value'


    BasicValueView(GInspector inspector, InspectorFrame parentFrame) {
        super(inspector, parentFrame)
    }

    @Override
    List<String> columnNames() {
        return [NAME, TYPE, MODIFIERS, DECLARER, VALUE]
    }

    @Override
    List<Map> buildFieldMaps() {
        Binding binding = this.inspector.bindingForEvaluate()
        def fieldMaps = []

        def selfMap = [:]
        def self = this.inspector.object
        selfMap[NAME] = inspector.pseudoVarName;
        selfMap[TYPE] = (self ? self.getClass().getName() : 'n/a')
        selfMap[MODIFIERS] = ''
        selfMap[DECLARER] = ''
        selfMap[VALUE] = self.toString()
        fieldMaps.add(selfMap)

        def metaFields = this.inspector.allMetaFields()
        metaFields.sort{it.getName()}.each {PropertyValue pv ->
            def map = [:]
            map[NAME] = pv.getName()
            map[TYPE] = pv.getType().getName()
            map[MODIFIERS] = 'public'
            map[DECLARER] = 'n/a'
            map[VALUE] = binding.getProperty(pv.getName()).toString()

            fieldMaps.add(map)
        }

        def fields = this.inspector.allFields()
        fields.sort{it.getName()}.each {Field field ->
            def map = [:]
            map[NAME] = field.getName()
            map[TYPE] = field.getType().getName()
            map[MODIFIERS] = Modifier.toString(field.getModifiers())
            map[DECLARER] = field.getDeclaringClass().getName()
            map[VALUE] = binding.getProperty(field.getName()).toString()

            fieldMaps.add(map)
        }

        return fieldMaps
    }

    @Override
    Object selectedObject() {
        int columnIndex = this.indexOfColumnNamed(NAME)
        def selector = this.valueTable.getValueAt(this.valueTable.getSelectedRow(), columnIndex)
        def obj = this.inspector.bindingForEvaluate()[selector]
        return obj
    }
}
