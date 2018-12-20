#!/bin/bash
#Scans mesh and outputs the latest 10 verifiers by date.
sudo java -jar ~/nyzoVerifier/build/libs/nyzoVerifier-1.0.jar co.nyzo.verifier.scripts.NewVerifierMeshStatusRequestScript `cat /var/lib/nyzo/production/verifier_private_seed` > status.txt
cat status.txt | grep "  ,  " | tail -10
