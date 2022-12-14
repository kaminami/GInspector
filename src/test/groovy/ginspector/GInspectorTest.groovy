package ginspector

import org.junit.Test

class GInspectorTest {
    @Test
    void testInspectList() {
        List aList = [1, 2, 3]
        GInspector.openWaitOn(aList)
    }

    @Test
    void testInspectMap() {
        Map aMap = [a: 1, b: 2, c: 3]
        GInspector.openWaitOn(aMap)
    }

    @Test
    void testInspectNumber() {
        int anInteger = 234
        GInspector.openWaitOn(anInteger)
    }

    @Test
    void testInspectString() {
        String aString = 'Groovy'
        GInspector.openWaitOn(aString)
    }

    @Test
    void testEvaluate() {
        def obj = 'hello'

        Binding binding = new Binding()
        binding.setVariable('_this', obj)

        String scriptString = '''
            println _this
'''

        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding)
        shell.evaluate(scriptString)
    }
}
