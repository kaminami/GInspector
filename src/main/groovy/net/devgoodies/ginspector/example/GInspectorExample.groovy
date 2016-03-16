package net.devgoodies.ginspector.example

import net.devgoodies.ginspector.GInspector


def set = new HashSet<String>()
set.add("xxx")
set.add("yyy")
set.add("zzz")
set.add("yyy")

GInspector.openOn(null)
GInspector.openOn(3..10)
GInspector.openOn(set)
GInspector.openWaitOn([1, 2, 'c', 4, 5, 'f'])
GInspector.openWaitOn(['NULL':null, 1:10, 2:20, [1, 2, 3]:['a':123]])

println "end of script"