GInspector
==========

GInspector is a object inspector for Groovy(Java) environment.
You can send messages to Groovy(Java) objects.

![Alt text](./GInspector/blob/master/screenshot01.png)

## Requirements

* [Groovy](http://groovy.codehaus.org/)
  - versions: 1.8.6 or later

* [SWT: The Standard Widget Toolkit](http://www.eclipse.org/swt/)
  - swt.jar
  - versions: 3.7.2 or later

## Usage
```groovy
import tools.ginspector.GInspector

def anObject = [1, 2, 3]
GInspector.openWaitOn(anObject)
GInspector.openOn(anObject)
```

```groovy
import tools.ginspector.GInspectorCategory

Object.mixin(GInspectorCategory)

[1, 2, 3].i()
['a':1, 'b':2].iw()
```

### Caution
In GInspector, use 'self'(default) or '_this' instead of 'this'.



## License
MIT
