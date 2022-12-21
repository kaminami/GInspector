package ginspector

import groovy.inspect.Inspector

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.security.PrivilegedAction
import java.security.AccessController
import org.codehaus.groovy.runtime.DefaultGroovyMethods

import ginspector.view.InspectorFrame

import static groovy.inspect.Inspector.MEMBER_DECLARER_IDX
import static groovy.inspect.Inspector.MEMBER_EXCEPTIONS_IDX
import static groovy.inspect.Inspector.MEMBER_MODIFIER_IDX
import static groovy.inspect.Inspector.MEMBER_NAME_IDX
import static groovy.inspect.Inspector.MEMBER_ORIGIN_IDX
import static groovy.inspect.Inspector.MEMBER_PARAMS_IDX
import static groovy.inspect.Inspector.MEMBER_TYPE_IDX


class GInspector {
    final Object object
    final String pseudoVarName = '_this'
    final String appName = 'GInspector'

    final Inspector inspector

    static Object openOn(Object obj) {
        Thread.start {
            InspectorFrame inst = new InspectorFrame(obj, false)
            inst.open()
        }
        return obj
    }

    static Object openWaitOn(Object obj) {
        InspectorFrame inst = new InspectorFrame(obj, true)
        inst.open()

        while (inst.isVisible() && inst.isWaiting) {
            Thread.sleep(200)
        }
        return obj
    }

    GInspector(Object obj) {
        this.object = obj
        this.inspector = new Inspector(obj)
    }

    List allMethods() {
        if (this.object == null) { return [] }

        return this.inspector.getMethods()
    }

    List allMetaMethods() {
        if (this.object == null) { return [] }

        return this.inspector.getMetaMethods()
    }

    List allMethodInfo() {
        if (this.object == null) { return [] }

        List list = []
        this.allMethods().each { Object[] info -> list.add(this.methodInfoFrom(info)) }
        this.allMetaMethods().each { Object[] info -> list.add(this.methodInfoFrom(info)) }

        return list
    }

    List withAllSuperclasses() {
        if (this.object == null) {
            return []
        }

        List list = this.allSuperclasses()
        list.add(0, this.object.getClass())
        return list
    }

    List allSuperclasses() {
        if (this.object == null) {
            return []
        }

        List classes = []
        Class current = this.object.getClass().getSuperclass()

        while (current != null) {
            classes.add(current)
            current = current.getSuperclass()
        }

        return classes
    }

    List allFields() {
        if (this.object == null) { return [] }

        List list = []
        this.withAllSuperclasses().each { Class klass ->
            klass.getDeclaredFields().each { Field field ->
                if (this.isValidField(field)) {
                    list.add(field)
                }
            }
        }

        return list
    }

    List allMetaFields() {
        if (this.object == null) { return [] }

        List metaFields = DefaultGroovyMethods.getMetaPropertyValues(this.object)
        return metaFields.findAll { PropertyValue propertyValue -> propertyValue.getName() != 'metaClass' }
    }

    boolean isValidField(Field field) {
        int modifiers = field.getModifiers()
        if (Modifier.isStatic(modifiers)) { return false }
        if (Modifier.isFinal(modifiers) && Modifier.isPrivate(modifiers)) { return false }
        if ((this.object instanceof GroovyObject) && field.getName().equals('metaClass')) { return false }

        return true
    }

    Object valueOf(Field field) {
        AccessController.doPrivileged({ field.setAccessible(true) } as PrivilegedAction)

        try {
            return field.get(this.object)
        } catch (Exception e) {
            // ignore
        }

        return 'n/a'
    }

    Binding bindingForEvaluate() {
        Map map = [:]
        map[this.pseudoVarName] = this.object

        this.allMetaFields().each { PropertyValue pv ->
            try {
                map[pv.getName()] = pv.getValue()
            } catch (Exception e) {
                map[pv.getName()] = 'n/a'
            }
        }

        this.allFields().each { Field f ->
            map.put(f.getName(), this.valueOf(f))
        }

        return (new Binding(map))
    }

    Map methodInfoFrom(Object[] methodInfo) {
        Map map = [:]

        map['Origin'] = methodInfo[MEMBER_ORIGIN_IDX]
        map['Name'] = methodInfo[MEMBER_NAME_IDX]
        map['Params'] = methodInfo[MEMBER_PARAMS_IDX]
        map['Type'] = methodInfo[MEMBER_TYPE_IDX]
        map['Modifiers'] = methodInfo[MEMBER_MODIFIER_IDX]
        map['Declarer'] = methodInfo[MEMBER_DECLARER_IDX]
        map['Exceptions'] = methodInfo[MEMBER_EXCEPTIONS_IDX]

        return map
    }

    Object evaluate(String groovyCode) {
        return (new GroovyShell(this.getClass().getClassLoader(), this.bindingForEvaluate())).evaluate(groovyCode)
    }
}

