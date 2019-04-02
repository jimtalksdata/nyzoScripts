# nyzoScripts
(Unsanctioned) addons for the stock Nyzo verifier. https://github.com/n-y-z-o/nyzoVerifier

Basic commands: https://github.com/jimtalksdata/nyzoScripts/blob/master/BASICCOMMANDS.md

**Instructions:**

1. Clone to an empty directory (NOT your verifier)
2. Manually copy the files in the correct place (anything in src goes into the verifier directory, anything outside stays wherever).
3. Follow the directions in the scripts.
4. ./gradlew build in the nyzoVerifier directory
5. Reload the supervisor

**Explanation:**

This project contains code loosely organized into directories. This is as follows:
- Root: Some MarkDown help files
- **additional_commands**: Quality of life scripting for the stock nyzo verifier.
- **lowendsetup**: Scripts and code changes suitable for lower-end / lower-spec systems.
- **chainstats**: Scripts and code changes used for the nyzo chainstats site.
- **python**: Python scripts for quality of life and convenience.
- **shell**: Shell scripts for quality of life and convenience.

For advanced users (I will provide no technical support):
- **docker**: Sample bare-bones docker setup. 
- **munin**: Sample munin (http://http://munin-monitoring.org/) based monitoring script.

Disclaimer: I am not responsible if you break your verifier, unintentionally cause a DDoS attack, or lose your place in the cycle. Use with caution.


