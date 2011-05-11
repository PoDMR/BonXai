#!/bin/bash
muxedcopy() {
  rsync -r --include "*/" --include "*.java" --exclude "*" $1 $2/src/main/java
  rsync -r --exclude "*java" $1 $2/src/main/resources
}

wget -P temp http://sourceforge.net/projects/jedit/files/jedit/4.3.2/jedit4.3.2source.tar.bz2/download
cd temp
tar -jxvf jedit*source.tar.bz2
cd ..

jedsrc=temp/jEdit
jedtgt=external/jedit
mkdir -p $jedtgt/src/main/java
mkdir -p $jedtgt/src/main/resources
muxedcopy $jedsrc/org $jedtgt
muxedcopy $jedsrc/com $jedtgt
