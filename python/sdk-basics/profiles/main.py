# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk

# initialize agent with sample plaintext profile persistor
try:
    persistorPassword = "ionic123"
    persistorPath = os.path.abspath("../../../sample-data/persistors/sample-persistor.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# verify that there are profiles
if agent.hasanyprofiles() == False:
    print("No profiles found in specified profile persistor")
    sys.exit(1)

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
activeProfile = agent.getactiveprofile()
print("\nACTIVE PROFILE: " + activeProfile.deviceid)

# change active profile
new_profile_id = "EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73"
print("\nSETTING NEW ACTIVE PROFILE: " + new_profile_id)
try:
    agent.setactiveprofile(new_profile_id)
except ionicsdk.exceptions.IonicException as e:
    print("Failed to set new active profile: {0}".format(e.message))
    sys.exit(1)

# display new active profile
activeProfile = agent.getactiveprofile()
print("\nNEW ACTIVE PROFILE: " + activeProfile.deviceid)