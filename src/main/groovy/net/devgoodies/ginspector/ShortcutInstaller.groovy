package net.devgoodies.ginspector

class ShortcutInstaller {
    static void install() {
        Object.metaClass {
            _i { -> GInspector.openOn(delegate) }
            _inspect { -> GInspector.openOn(delegate) }
            _iw { -> GInspector.openWaitOn(delegate) }
            _inspectWait { -> GInspector.openWaitOn(delegate) }
        }
    }
}