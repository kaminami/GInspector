GInspector
==========

GInspector is an object inspector for Groovy(Java) environment.
You can send messages to Groovy(Java) objects.

![Alt text](https://raw.github.com/kaminami/GInspector/master/screenshot01.png)
![Alt text](https://raw.github.com/kaminami/GInspector/master/screenshot02.png)

## Requirements

* [Groovy](http://groovy.codehaus.org/)
  - versions: 1.8.6 or later

* [SWT: The Standard Widget Toolkit](http://www.eclipse.org/swt/)
  - swt.jar
  - versions: 3.7.2 or later

## Usage
```groovy
import net.devgoodies.ginspector.GInspector

def anObject = [1, 2, 3]
GInspector.openOn(anObject)
GInspector.openWaitOn(anObject)
```

```groovy
import net.devgoodies.ginspector.GInspectorCategory

Object.mixin(GInspectorCategory)

[1, 2, 3].i()
['a':1, 'b':2].iw()
```

### Caution
In GInspector, use 'self'(default) or '_this' instead of 'this'.



## License
MIT
