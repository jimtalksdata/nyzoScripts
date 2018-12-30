#!/bin/bash
rm verstatus.txt
cat meshstatus.txt  | grep "C,  " | grep -oE "\b([0-9]{1,3}\.){3}[0-9]{1,3}\b" > ips.txt
sudo java -jar ~/nyzoVerifier/build/libs/nyzoVerifier-1.0.jar co.nyzo.verifier.scripts.StatusRequestScriptBatch > verstatus.txt
cat verstatus.txt | grep "version: " | sort | uniq -c > ~/versionNumbers.txt
#cat verstatus.txt | grep "version: " | grep -oE "[0-9]{3,4}" > ~/versionNumbersonly.txt
#echo "" >> ~/versionNumbers.txt
#cat ~/versionNumbersonly.txt | hist -x >> ~/versionNumbers.txt
echo "" >> ~/versionNumbers.txt
echo "* In cycle verifiers only. Numbers may be slightly off." >> ~/versionNumbers.txt