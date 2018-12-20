**Supervisor controls**

sudo supervisorctl stop all
sudo supervisorctl reload
sudo supervisorctl tail -f nyzo_verifier

**Bootstrap a new node from root**
sudo apt update && sudo apt install unattended-upgrades -y && sudo apt install nano haveged tmux htop openjdk-8-jdk supervisor git -y && git clone https://github.com/n-y-z-o/nyzoVerifier.git && cd nyzoVerifier && ./gradlew build && sudo mkdir -p /var/lib/nyzo/production && sudo cp trusted_entry_points /var/lib/nyzo/production && ln -s /root /home/ubuntu

**View votes**
watch -n 30 sudo java -jar ~/nyzoVerifier/build/libs/nyzoVerifier-1.0.jar co.nyzo.verifier.scripts.NewVerifierTallyStatusRequestScript `cat /var/lib/nyzo/production/verifier_private_seed`

**Send a vote**
sudo java -jar ~/nyzoVerifier/build/libs/nyzoVerifier-1.0.jar co.nyzo.verifier.scripts.NewVerifierVoteOverrideRequestScript `cat /var/lib/nyzo/production/verifier_private_seed` VERIFIERTOVOTEFOR
