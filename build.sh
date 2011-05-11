#!/bin/sh
mkdir temp
./prepare.sh
./external/jedit/prepare.sh
mvn install -Dmaven.test.skip=true
#rm -r temp
