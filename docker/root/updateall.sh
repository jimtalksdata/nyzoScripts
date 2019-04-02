#!/bin/bash
cd ~/images/nyzo
docker build --no-cache -t nyzo:latest .
cd ~
./stopall.sh
./runall.sh
