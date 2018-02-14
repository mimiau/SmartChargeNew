#!/bin/bash -l

echo "Extracting cities..."
time=`date`
echo $time
../lib/osmconvert cities.pbf -B=polys/bialystok.poly -o=bialystok.pbf
echo "1 Done"

../lib/osmconvert cities.pbf -B=polys/bielsko-biala.poly -o=bielsko-biala.pbf
echo "2 Done"
../lib/osmconvert cities.pbf -B=polys/bydgoszcz.poly -o=bydgoszcz.pbf
echo "3 Done"
../lib/osmconvert cities.pbf -B=polys/czestochowa.poly -o=czestochowa.pbf
echo "4 Done"
../lib/osmconvert cities.pbf -B=polys/gorzow_wielkopolski.poly -o=gorzow_wielkopolski.pbf
echo "5 Done"
../lib/osmconvert cities.pbf -B=polys/kalisz.poly -o=kalisz.pbf
echo "6 Done"
../lib/osmconvert cities.pbf -B=polys/katowice.poly -o=katowice.pbf
echo "7 Done"
../lib/osmconvert cities.pbf -B=polys/kielce.poly -o=kielce.pbf
echo "8 Done"
../lib/osmconvert cities.pbf -B=polys/konin.poly -o=konin.pbf
echo "9 Done"
../lib/osmconvert cities.pbf -B=polys/koszalin.poly -o=koszalin.pbf
echo "10 Done"
../lib/osmconvert cities.pbf -B=polys/krakow.poly -o=krakow.pbf
echo "11 Done"
../lib/osmconvert cities.pbf -B=polys/lodz.poly -o=lodz.pbf
echo "12 Done"
../lib/osmconvert cities.pbf -B=polys/lublin.poly -o=lublin.pbf
echo "13 Done"
../lib/osmconvert cities.pbf -B=polys/nowy_sacz.poly -o=nowy_sacz.pbf
echo "14 Done"
../lib/osmconvert cities.pbf -B=polys/olsztyn.poly -o=olsztyn.pbf
echo "15 Done"
../lib/osmconvert cities.pbf -B=polys/opole.poly -o=opole.pbf
echo "16Done"
../lib/osmconvert cities.pbf -B=polys/plock.poly -o=plock.pbf
echo "17 Done"
../lib/osmconvert cities.pbf -B=polys/poznan.poly -o=poznan.pbf
echo "18 Done"
../lib/osmconvert cities.pbf -B=polys/radom.poly -o=radom.pbf
echo "19 Done"
../lib/osmconvert cities.pbf -B=polys/rybnik.poly -o=rybnik.pbf
echo "20 Done"
../lib/osmconvert cities.pbf -B=polys/rzeszow.poly -o=rzeszow.pbf
echo "21 Done"
../lib/osmconvert cities.pbf -B=polys/szczecin.poly -o=szczecin.pbf
echo "22 Done"
../lib/osmconvert cities.pbf -B=polys/tarnow.poly -o=tarnow.pbf
echo "23 Done"
../lib/osmconvert cities.pbf -B=polys/torun.poly -o=torun.pbf
echo "24 Done"
../lib/osmconvert cities.pbf -B=polys/trojmiasto.poly -o=trojmiasto.pbf
echo "25 Done"
../lib/osmconvert cities.pbf -B=polys/walbrzych.poly -o=walbrzych.pbf
echo "26 Done"
../lib/osmconvert cities.pbf -B=polys/warszawa.poly -o=warszawa.pbf
echo "27 Done"
../lib/osmconvert cities.pbf -B=polys/wroclaw.poly -o=wroclaw.pbf
echo "28 Done"
../lib/osmconvert cities.pbf -B=polys/zielona_gora.poly -o=zielona_gora.pbf
echo "29 Done"
echo "All Done"
time=`date`
echo $time
