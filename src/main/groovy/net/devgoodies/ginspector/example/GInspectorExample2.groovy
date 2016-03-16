package net.devgoodies.ginspector.example

import net.devgoodies.ginspector.GInspector

GInspector.installShortcut()

def set = new HashSet<String>()
set.add("xxx")
set.add("yyy")
set.add("zzz")
set.add("yyy")


null?.i()
(3..10)?._i()
set._inspect()
[1, 2, 'c', 4, 5, 'f']?._iw()
['NULL':null, 1:10, 2:20, [1, 2, 3]:['a':123]]?._inspectWait()

println "end of script"