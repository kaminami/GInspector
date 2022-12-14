GInspector
==========

GInspector is a graphical object inspector for Groovy environment.
You can send messages to Groovy(Java) objects.

## Requirements

* [Groovy](http://groovy.codehaus.org/)
  - versions: 4 or later


## Usage
```groovy
import ginspector.GInspector

GInspector.openOn([1, 2, 3])
GInspector.openWaitOn(['a':1, 'b':2])
```

### Caution
In GInspector, use '_this' instead of 'this'.


### Gradle build.gradle example
```groovy
# build.gradle (minimum)
apply plugin: 'groovy'

repositories {
    mavenCentral()

    maven { 
        url 'https://github.com/kaminami/GInspector/raw/master/repository' 
    }
}

dependencies {
    implementation 'org.apache.groovy:groovy-all:4.0.6'
    implementation 'net.devgoodies:ginspector:2.0.0'
}
```


## License
Apache License Version 2.0