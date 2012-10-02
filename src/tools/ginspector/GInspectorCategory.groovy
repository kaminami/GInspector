package tools.ginspector

@Category(Object)
class GInspectorCategory {
	def i() {
		GInspector.openOn(this)
	}

	def iw() {
		GInspector.openWaitOn(this)
	}
}
