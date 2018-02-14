#!/bin/bash -l

echo "Cutting cities from map"
time=`date`
echo $time
bzcat poland-latest.osm.bz2 | ../lib/osmconvert - -B=polys/all.poly -o=cities.pbf
time=`date`
echo $time
