###############################################################################
# NYZO Node Monitor
###############################################################################
# This script monitors one or more Nyzo nodes using information from
# the nyzo.co website and sends you a mail if your nodes go out of sync. 
# It does not require access to a Nyzo node and can run on practically any 
# system with Python 3 and internet access.
# This approach is simple to install and run, and it can monitor multiple
# nodes at once, but it of course also has disadvantages, e.g. if 
# the structure of the nyzo.co website changes, or the
# website goes down, you will receive email alerts that are not necessarily
# connected to the actual status of your nodes.
# For node-based status monitoring, see the other scripts in this repo.
# Instructions on how to set up the script: see below.
###############################################################################

import urllib3
import json
import smtplib
from email.mime.text import MIMEText
import re
import sys

# Add your node partial IDs here (click the node name on the "mesh" page, 
# then copy the partial ID from the URL that's being called)
nodes = ['a270.ffa1', '6afd.5ca7', '9f99.1509', '7e73.b477']
# Email details (Gmail!)
# Note: you have to activate "unsafe" apps for this to work, and maybe
# you have to log into Gmail to confirm that your server is allowed to send mails
# on your behalf. Put this on a safe server, or use a throwaway Gmail address that
# you forward to your main address.
address = '<YOUR ADDRESS>'
gmail_pass = '<YOUR PASSWORD>'

# run with crontab, example: */5 * * * * cd /home/ubuntu && python3 nyzo_monitor.py

node_status = 'https://www.nyzo.co/status?id='
mesh_update = 'https://www.nyzo.co/meshUpdate?s=0'

alerts = ""

def alert(message):
    global alerts
    alerts = alerts + message + '\n'
    print('Alert: ' + message)
    return
    
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
    return

# we don't verify https requests for the purpose of this simple script
urllib3.disable_warnings()
http = urllib3.PoolManager()

# find the frozen edge published on the mesh page
try:
    r = http.request('GET', mesh_update)
    data = r.data.decode('UTF-8')
    exp = re.compile('Frozen edge: (\d+)')
    frozen_edge_mesh = int(exp.search(data).group(1))
    print('Frozen edge: ' + str(frozen_edge_mesh))
except:
    alert('Mesh frozen edge could not be determined.')
    send_alerts()
    sys.exit(0)

# check on all the nodes
for i in range(0, len(nodes)):
    try:
        r = http.request('GET', node_status + nodes[i])
        data = r.data.decode('UTF-8')
        exp = re.compile('frozen edge: (\d+)')
        frozen_edge_node = int(exp.search(data).group(1))
        if frozen_edge_node > frozen_edge_mesh + 25 or frozen_edge_node < frozen_edge_mesh - 25:
            alert('Node seems to be out of sync: ' + nodes[i] + ' on: ' + str(frozen_edge_node))
        else:
            print('Node in sync: ' + nodes[i])
    except:
        alert('Could not get status of node: ' + nodes[i])
        
send_alerts()