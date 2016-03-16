GInspector
==========

GInspector is a graphical object inspector for Groovy environment.
You can send messages to Groovy(Java) objects.

## Requirements

* [Groovy](http://groovy.codehaus.org/)
  - versions: 2.2.1 or later


## Usage
```groovy
import net.devgoodies.ginspector.GInspector

def anObject = [1, 2, 3]
GInspector.openOn(anObject)
GInspector.openWaitOn(anObject)
```

```groovy
import net.devgoodies.ginspector.GInspectorCategory

GInspector.installShortcut()

[1, 2, 3]._i()
['a':1, 'b':2]._iw()
```

### Caution
In GInspector, use 'self' instead of 'this'.



## License
MIT
