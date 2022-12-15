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
        List fieldMaps = []

        Map selfMap = [:]
        def self = this.inspector.object
        selfMap[NAME] = inspector.pseudoVarName;
        selfMap[TYPE] = (self ? self.getClass().getSimpleName() : 'n/a')
        selfMap[MODIFIERS] = ''
        selfMap[DECLARER] = ''
        selfMap[VALUE] = self.toString()
        fieldMaps.add(selfMap)

        List metaFields = this.inspector.allMetaFields()
        metaFields.sort{ PropertyValue pv -> pv.getName() }.each { PropertyValue pv ->
            Map map = [:]
            map[NAME] = pv.getName()
            map[TYPE] = pv.getType().getSimpleName()
            map[MODIFIERS] = 'public'
            map[DECLARER] = 'n/a'
            map[VALUE] = binding.getProperty(pv.getName()).toString()

            fieldMaps.add(map)
        }

        List fields = this.inspector.allFields()
        fields.sort{ Field field -> field.getName()}.each { Field field ->
            Map map = [:]
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
