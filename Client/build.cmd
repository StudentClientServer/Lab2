javadoc -classpath lib/junit.jar -d docs -charset utf-8 -sourcepath src -author -subpackages view
jar cvfm build\program.jar res\manifest.mf -C classes .

pause