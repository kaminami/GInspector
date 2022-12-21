package ginspector

import org.junit.Test

class GroovyShellTest {
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
