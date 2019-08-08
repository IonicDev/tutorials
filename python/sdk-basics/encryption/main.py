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
persistor_password = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if (persistor_password == None):
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# initialize agent with sample password profile persistor
try:
    persistor_path = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistor_path, persistor_password)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

###########################################################
## SENDER 
###########################################################

message = b"this is a secret message!"

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

# CAUTION: b64_ciphertext is a byte object; beware of type errors
print("CREATED KEYID      : " + created_key.id)
print("CIPHERTEXT         : " + binascii.hexlify(ciphertext).decode("ascii"))
print("PAYLOAD KEYID      : " + payload["keyId"])
print("PAYLOAD B64 CRYPTO : " + payload["b64_ciphertext"].decode())

###########################################################
## RECEIVER
###########################################################

# extract ciphertext and keyId
ciphertext = base64.b64decode(payload["b64_ciphertext"])
key_id = payload["keyId"]

# get key
try:
    fetched_key = agent.getkey(key_id)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(1)

# initialize aes cipher
receiver_cipher = ionicsdk.AesCtrCipher(fetched_key.bytes)

# decrypt data
plaintext = receiver_cipher.decryptbytes(ciphertext)

print("Fetched Key ID : " + key_id)
print("Plaintext      : " + plaintext.decode())
