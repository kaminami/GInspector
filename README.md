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
import net.devgoodies.ginspector.GInspector

GInspector.installShortcut()

[1, 2, 3]._i()
['a':1, 'b':2]._iw()
```

### Caution
In GInspector, use 'self' instead of 'this'.

# Gradle
  # build.gradle (minimum)
  apply plugin: 'groovy'

  repositories {
      jcenter()

      maven { 
          url 'https://github.com/kaminami/GInspector/raw/master/repository' 
      }
  }

  dependencies {
      compile 'org.codehaus.groovy:groovy-all:2.4.7'
      compile 'net.devgoodies:ginspector:1.0.0'
}


## License
MIT
