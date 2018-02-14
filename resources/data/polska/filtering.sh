#!/bin/bash -l

../lib/osmfilter no_cities.o5m --keep="name=Orlen =Lotos =BP =Shell =Circle\ K  =McDonald's =Burger\ King =KFC brand=Orlen =Lotos =BP =Shell =Circle\ K highway=rest_area =services" >filtered.osm

