#!/bin/sh
if [ ! -d /var/lib/nyzo/production ]; then
	ln -s /data /var/lib/nyzo/production
fi
sudo cp trusted_entry_points /var/lib/nyzo/production
sudo cp managed_verifiers /var/lib/nyzo/production
java -jar -Xmx3G /root/nyzoVerifier/build/libs/nyzoVerifier-1.0.jar co.nyzo.verifier.sentinel.Sentinel >> /var/lib/nyzo/production/log.txt 2>&1
