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
    void testOpenThenOpenWait() {
        GInspector.openOn('open')
        GInspector.openWaitOn('openWait')
    }
}
