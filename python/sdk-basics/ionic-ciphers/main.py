# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import json
import base64
import binascii
import ionicsdk

# read persistor password from environment variable
persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if (persistorPassword == None):
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# initialize agent with sample password profile persistor
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# set app metadata
agent.setmetadata({
    "ionic-application-name": "Ionic-Ciphers Tutorial",
    "ionic-application-version": "1.0.0"
})

###########################################################
## SENDER 
###########################################################

message = "this is a secret message!"

# initialize chunk cipher
sender_cipher = ionicsdk.ChunkCipherAuto(agent)

# encrypt data
ciphertext = sender_cipher.encryptstr(message)

print("CIPHERTEXT : " + ciphertext)


###########################################################
## RECEIVER
###########################################################

# initialize chunk cipher
receiver_cipher = ionicsdk.ChunkCipherAuto(agent)

# decrypt data
plaintext = receiver_cipher.decryptstr(ciphertext)

print("\nPLAINTEXT  : " + plaintext)
