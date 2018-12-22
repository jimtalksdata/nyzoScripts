###############################################################################
# NYZO Node Monitor
###############################################################################
# This is another version of the node monitor that runs on a server
# with a Nyzo node. It queries the mesh directly (not via the web like my other
# script). 
#
# More instructions below.
###############################################################################

import smtplib
from email.mime.text import MIMEText
import re
import subprocess

# Add your node IPs here, you can find them in the info shown after you click the 
# node name on the nyzo.co 'mesh' page
nodes = ['13.52.10.83', '18.144.72.192', '54.193.31.203', '13.57.9.202']

# Email details (Gmail!)
# Note: you have to activate "unsafe" apps for this to work, and maybe
# you have to log into Gmail to confirm that your server is allowed to send mails
# on your behalf. Put this on a safe server, or use a throwaway Gmail address that
# you forward to your main address.
address = '<YOUR ADDRESS>'
gmail_pass = '<YOUR PASSWORD>'

# JAR location
# The StatusRequestScript has only been added to the Nyzo repo recently. Update
# your node if you haven't done so already: https://nyzo.co/updateInstructions
nyzo_node_jar = '/home/ubuntu/nyzoVerifier/build/libs/nyzoVerifier-1.0.jar'

# Run with crontab, example: */5 * * * * cd /home/ubuntu && python3 nyzo_monitor_node.py
# Run this on more than one node to make sure that you catch any situation where your
# complete server goes AWOL and this script is no longer executed either.

alerts = ""

def alert(message):
    global alerts
    alerts = alerts + message + '\n'
    print('Alert: ' + message)
    
def send_alerts():
    if alerts != "":
        fromaddr = address
        toaddr = address
        msg = MIMEText(alerts)
        msg['From'] = fromaddr
        msg['To'] = toaddr
        msg['Subject'] = 'NYZO Node Monitor: Issue(s) Detected'

        server = smtplib.SMTP('smtp.gmail.com', 587)
        server.starttls()
        server.login(address, gmail_pass)
        text = msg.as_string()
        server.sendmail(fromaddr, toaddr, text)
        server.quit()

# check on all the nodes
for i in range(0, len(nodes)):
    try:
        result = subprocess.run(['java', '-jar',  nyzo_node_jar, 'co.nyzo.verifier.scripts.StatusRequestScript', nodes[i]], stdout=subprocess.PIPE)
        status = result.stdout.decode('utf-8')
        exp = re.compile('set frozen edge to (\d+)')
        frozen_edge_mesh = int(exp.search(status).group(1))
        print('Mesh frozen edge: ' + str(frozen_edge_mesh))
        exp = re.compile('frozen edge: (\d+)')
        frozen_edge_node = int(exp.search(status).group(1))
        if frozen_edge_node > frozen_edge_mesh + 25 or frozen_edge_node < frozen_edge_mesh - 25:
            alert('Node seems to be out of sync: ' + nodes[i] + ' on: ' + str(frozen_edge_node))
        else:
            print('Node in sync: ' + nodes[i])
    except:
        alert('Could not get status of node: ' + nodes[i])
        
send_alerts()