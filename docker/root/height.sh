#!/bin/sh
for i in /home/data/nyzo/*/log.txt
do
  if test -f "$i" 
  then
    grep freezing $i /dev/null | tail -n 1
  fi
done
