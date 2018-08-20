# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

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

###########################################################
## SENDER 
###########################################################

message = "this is a secret message!"

# create new key
try:
    created_key = agent.createkey()
except ionicsdk.exceptions.IonicException as e:
    print("Error creating a key: {0}".format(e.message))
    sys.exit(1)

# set app metadata
agent.setmetadata({
    "ionic-application-name": "Encryption Tutorial",
    "ionic-application-version": "1.0.0"
})

# initialize aes cipher
sender_cipher = ionicsdk.AesCtrCipher(created_key.bytes)

# encrypt data
ciphertext = sender_cipher.encryptbytes(message)

# package ciphertext and keyId
payload = {
    "keyId": created_key.id,
    "b64_ciphertext": base64.b64encode(ciphertext)
}

print("CREATED KEYID : " + created_key.id)
print("CIPHERTEXT    : " + binascii.hexlify(ciphertext))
print("\nPAYLOAD       : " + json.dumps(payload))


###########################################################
## RECEIVER
###########################################################

# extract ciphertext and keyId
ciphertext = base64.b64decode(payload["b64_ciphertext"])
keyId = payload["keyId"]

# get key
try:
    fetched_key = agent.getkey(keyId)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(1)

# initialize aes cipher
receiver_cipher = ionicsdk.AesCtrCipher(fetched_key.bytes)

# decrypt data
plaintext = receiver_cipher.decryptbytes(ciphertext)

print("\nFETCHED KEYID : " + keyId)
print("PLAINTEXT     : " + plaintext)
