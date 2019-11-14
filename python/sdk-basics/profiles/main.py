# (c) 2018-2020 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk

source_dir = 'github-tutorials/python/sdk-basics/profiles'
this_dir = os.getcwd()

# run only from source directory
if not this_dir.endswith(source_dir): 
    print("[!] Please run this sample from inside " + source_dir)
    sys.exit(1)

# initialize agent with sample password profile persistor
try:
    persistor_password = "ionic123"
    persistor_path = os.path.abspath("../../../sample-data/persistors/sample-persistor.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistor_path, persistor_password)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# verify that there are profiles
if agent.hasanyprofiles() == False:
    print("No profiles found in specified profile persistor")
    sys.exit(1)

# set app metadata
agent.setmetadata({
    "ionic-application-name": "Profiles Tutorial",
    "ionic-application-version": "1.0.0"
})

# list all available profiles
print("ALL PROFILES:")
profiles = agent.getallprofiles()
for profile in profiles:
    print("---")
    print("Name     : " + profile.name)
    print("Id       : " + profile.deviceid)
    print("Keyspace : " + profile.keyspace)
    print("ApiUrl   : " + profile.server)

# verify there is an active profile 
if agent.hasactiveprofile() == False:
    print("No profile set as active")
    sys.exit(1)

# display active profile
active_profile = agent.getactiveprofile()
print("Active Profile: " + active_profile.deviceid)

# change active profile
new_profile_id = "EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73"
print("\nSETTING NEW ACTIVE PROFILE: " + new_profile_id)
try:
    agent.setactiveprofile(new_profile_id)
except ionicsdk.exceptions.IonicException as e:
    print("Failed to set new active profile: {0}".format(e.message))
    sys.exit(1)

# display new active profile
active_profile = agent.getactiveprofile()
print("New Active Profile: " + active_profile.deviceid)
