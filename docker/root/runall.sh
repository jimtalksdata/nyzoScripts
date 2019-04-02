#!/bin/sh
docker run --name nickname --privileged -p 1.2.3.4:9444:9444 --restart unless-stopped -v /home/data/nyzo/nickname:/data nyzo:latest /root/nyzoVerifier/start.sh &