#!/bin/bash
#Scans mesh and returns a file.
sudo java -jar ~/nyzoVerifier/build/libs/nyzoVerifier-1.0.jar co.nyzo.verifier.scripts.NewVerifierMeshStatusRequestScript `cat /var/lib/nyzo/production/verifier_private_seed` > status.txt
cat status.txt | less
