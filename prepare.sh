#!/bin/bash
copywosvn() {
  mkdir -p $2
  rsync -r --exclude=.svn $1 $2
}
mavenfy () {
	copywosvn "$1/src/*"       $2/src/main/java
	copywosvn "$1/test/*"      $2/src/test/java
	copywosvn "$1/resources/*" $2/src/test/resources
	copywosvn "$1/demo/*"      $2/src/demo/java
}

fltsrc=temp/flt-svn
flttgt=flt
svn co https://nirwana.cs.uni-dortmund.de/repos/FoXLib/FLT/trunk/ $fltsrc
mavenfy $fltsrc/FLT-Utilities $flttgt/flt-utilities
mavenfy $fltsrc/FLT-Core      $flttgt/flt-core
mavenfy $fltsrc/FLT-Learning  $flttgt/flt-learning

mkdir external/jama/src/main/resources
cp $fltsrc/FLT-Externals/jama.jar external/jama/src/main/resources

bnxsrc=temp/bonxai-svn
bnxtgt=bonxai
svn co https://nirwana.cs.uni-dortmund.de/repos/FoXLib/bonXai/trunk/ $bnxsrc
copywosvn "$bnxsrc/src/*"       $bnxtgt/src/main/java
copywosvn "$bnxsrc/tests/*"     $bnxtgt/src/test/java
copywosvn "$bnxsrc/resources/*" $bnxtgt/src/test/resources
#rm -r bonxai/src/main/java/de/tudortmund/cs/bonxai/bonxai/parser/New Parser/
