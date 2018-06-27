# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk

# read persistor password from environment variable
persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if (persistorPassword == None):
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# initialize agent with sample plaintext profile persistor
try:
    persistorPassword = "ionic123"
    persistorPath = os.path.abspath("../../../sample-data/persistors/sample-persistor.pw")
    print(persistorPath)
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# define fixed attributes
fixed_attributes = {
    "data-type": ["Finance"],
    "region": ["North America"]
}

# define mutable attributes
mutable_attributes = {
    "classification": ["Restricted"]
}

# create new key
try:
    created_key = agent.createkey(
        attributes=fixed_attributes, 
        mutableAttributes=mutable_attributes)
except ionicsdk.exceptions.IonicException as e:
    print("Error creating a key: {0}".format(e.message))
    sys.exit(-2)

# display new key
print("KeyId        : " + created_key.id)
print("KeyBytes     : " + binascii.hexlify(created_key.bytes))
print("FixedAttrs   : " + json.dumps(created_key.attributes))
print("MutableAttrs : " + json.dumps(created_key.mutableAttributes))

# get key by KeyId
try:
    fetched_key = agent.getkey(keyId)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# display fetched key
print("KeyId        : " + fetched_key.id)
print("KeyBytes     : " + binascii.hexlify(fetched_key.bytes))
print("FixedAttrs   : " + json.dumps(fetched_key.attributes))
print("MutableAttrs : " + json.dumps(fetched_key.mutableAttributes))

# define new mutable attributes
new_mutable_attributes = {
    "classification": ["Highly Restricted"]
}

# merge new and existing mutable attributes
updated_attributes = copy.copy(dic(fetched_key.mutableAttributes))
for key,value in new_mutable_attributes.items():
    updated_attributes[key] = value

# update key
try:
    updated_key = agent.updatekey(key_id, updated_attributes)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# display updated key
print("KeyId        : " + updated_key.id)
print("KeyBytes     : " + binascii.hexlify(updated_key.bytes))
print("FixedAttrs   : " + json.dumps(updated_key.attributes))
print("MutableAttrs : " + json.dumps(updated_key.mutableAttributes))
