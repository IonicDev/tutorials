# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import json
import copy
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

# set app metadata
agent.setmetadata({
    "ionic-application-name": "Keys Tutorial",
    "ionic-application-version": "1.0.0"
})

# define fixed attributes
fixed_attributes = {
    "data-type": ["Finance"],
    "region": ["North America"]
}

# define mutable attributes
mutable_attributes = {
    "classification": ["Restricted"],
    "designated_owner": ["joe@hq.example.com"]
}

# create new key with fixed and mutable attributes
try:
    created_key = agent.createkey(
        attributes=fixed_attributes, 
        mutableAttributes=mutable_attributes)
except ionicsdk.exceptions.IonicException as e:
    print("Error creating a key: {0}".format(e.message))
    sys.exit(-2)

# display new key
print("\nNEW KEY:")
print("KeyId        : " + created_key.id)
print("KeyBytes     : " + binascii.hexlify(created_key.bytes).decode("ascii"))
print("FixedAttrs   : " + json.dumps(created_key.attributes))
print("MutableAttrs : " + json.dumps(created_key.mutableAttributes))

# get key by KeyId
try:
    fetched_key = agent.getkey(created_key.id)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# display fetched key
print("\nFETCHED KEY:")
print("KeyId        : " + fetched_key.id)
print("KeyBytes     : " + binascii.hexlify(fetched_key.bytes).decode("ascii"))
print("FixedAttrs   : " + json.dumps(fetched_key.attributes))
print("MutableAttrs : " + json.dumps(fetched_key.mutableAttributes))

# define new mutable attributes
new_mutable_attributes = {
    "classification": ["Highly Restricted"]
}

# merge new and existing mutable attributes
updated_attributes = copy.copy(dict(fetched_key.mutableAttributes))
for key,value in new_mutable_attributes.items():
    updated_attributes[key] = value

# update key
try:
    updated_key = agent.updatekey(ionicsdk.UpdateKeyData(
        id=fetched_key.id, 
        bytes=fetched_key.bytes, 
        mutableAttributes=updated_attributes,
        attributes=fetched_key.attributes,
        attributesSigBase64FromServer=fetched_key.attributesSigBase64FromServer,
        mutableAttributesSigBase64FromServer=fetched_key.mutableAttributesSigBase64FromServer
        )
    )
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# display updated key
print("\nUPDATED KEY:")
print("KeyId        : " + updated_key.id)
print("KeyBytes     : " + binascii.hexlify(updated_key.bytes).decode("ascii"))
print("FixedAttrs   : " + json.dumps(updated_key.attributes))
print("MutableAttrs : " + json.dumps(updated_key.mutableAttributes))
