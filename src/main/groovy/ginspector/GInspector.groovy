package ginspector

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.security.PrivilegedAction
import java.security.AccessController
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.DefaultGroovyMethods

import ginspector.view.InspectorFrame


class GInspector {
    final Object object
    final String pseudoVarName = '_this'
    final String appName = 'GInspector'

    static Object openOn(Object obj) {
        Thread.start {
            def inst = new InspectorFrame(obj, false)
            inst.open()
        }
        return obj
    }

    static Object openWaitOn(Object obj) {
        def inst = new InspectorFrame(obj, true)
        inst.open()

        while (inst.isVisible() && inst.isWaiting) {
            Thread.sleep(200)
        }
        return obj
    }

    GInspector(Object obj) {
        this.object = obj
    }

    List allMethods() {
        if (this.object == null) { return [] }

        return this.withAllSuperclasses().inject([]) {List list, Class klass ->
            list.addAll(klass.getDeclaredMethods());
            list
        }
    }

    List allMetaMethods() {
        if (this.object == null) { return [] }

        MetaClass metaClass = InvokerHelper.getMetaClass(this.object)
        return metaClass.getMetaMethods()
    }

    List allMethodInfo() {
        if (this.object == null) { return [] }

        def list = []
        this.allMethods().each { list.add(this.methodInfoFrom(it)) }
        this.allMetaMethods().each { list.add(this.methodInfoFrom(it)) }
        return list.sort {it['Name']}
    }

    List withAllSuperclasses() {
        if (this.object == null) { return [] }

        def list = this.allSuperclasses()
        list.add(0, this.object.getClass())
        return list
    }

    List allSuperclasses() {
        if (this.object == null) { return [] }

        def classes = []
        def current = this.object.getClass().getSuperclass()

        while (current != null) {
            classes.add(current)
            current = current.getSuperclass()
        }

        return classes
    }

    List allFields() {
        if (this.object == null) { return [] }

        def list = []
        this.withAllSuperclasses().each {Class klass ->
            klass.getDeclaredFields().each {f ->
                if (this.isValidField(f)) {
                    list.add(f)
                }
            }
        }

        return list
    }

    List allMetaFields() {
        if (this.object == null) { return [] }

        def metaFields = DefaultGroovyMethods.getMetaPropertyValues(this.object)
        return metaFields.findAll {it.getName() != 'metaClass'}
    }

    boolean isValidField(Field f) {
        def modifiers = f.getModifiers()
        if (Modifier.isStatic(modifiers)) { return false }
        if (Modifier.isFinal(modifiers) && Modifier.isPrivate(modifiers)) { return false }
        if ((this.object instanceof GroovyObject) && f.getName().equals('metaClass')) { return false }

        return true
    }

    Object valueOf(Field field) {
        AccessController.doPrivileged({field.setAccessible(true)} as PrivilegedAction)

        def res = null
        try {
            res = field.get(this.object)
        } catch (Exception e){
            res = 'n/a'
        }

        return res
    }

    Binding bindingForEvaluate() {
        def map = [:]
        map[this.pseudoVarName] = this.object

        this.allMetaFields().each {PropertyValue pv ->
            try {
                map[pv.getName()] = pv.getValue()
            } catch (Exception e) {
                map[pv.getName()] = 'n/a'
            }
        }

        this.allFields().each {Field f ->
            map.put(f.getName(), this.valueOf(f))
        }

        return (new Binding(map))
    }

    Map methodInfoFrom(Method method) {
        def map = [:]

        map['Origin'] = 'JAVA'
        map['Name'] = method.getName()
        map['Params'] = method.getParameterTypes().collect {it.name}.join(', ')
        map['Type'] = method.getReturnType().getName()
        map['Modifiers'] = Modifier.toString(method.getModifiers())
        map['Declarer'] = method.getDeclaringClass().getName()
        map['Exceptions'] = method.getExceptionTypes().collect {it.name}.join(', ')

        return map
    }

    Map methodInfoFrom(MetaMethod method) {
        def map = [:]

        map['Origin'] = 'GROOVY'
        map['Name'] = method.getName()
        map['Params'] = method.getParameterTypes().collect {it.getTheClass().getName()}.join(', ')
        map['Type'] = method.getReturnType().getName()
        map['Modifiers'] = Modifier.toString(method.getModifiers())
        map['Declarer'] = method.getDeclaringClass().getTheClass().getName()
        map['Exceptions'] = 'n/a'

        return map
    }

    Object evaluate(String groovyCode) {
        return (new GroovyShell(this.getClass().getClassLoader(), this.bindingForEvaluate())).evaluate(groovyCode)
    }
}

