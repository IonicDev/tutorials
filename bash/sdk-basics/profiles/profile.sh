#! /usr/bin/env bash
# comment: use command ./profile.sh

# Confirm that the sample profile persistor file exists
PERSISTOR_PATH="../../../sample-data/persistors/sample-persistor.pw"
PERSISTOR_PASSWORD="ionic123"
if [[ ! -f "$PERSISTOR_PATH" ]]; then
  echo "[!] '$PERSISTOR_PATH' does not exist"
  exit 1
fi

# List profile(s) stored in the profile persistor
machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${PERSISTOR_PASSWORD} profile list

# Store the output of the profile show command in a variable
ACTIVE_PROFILE=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${PERSISTOR_PASSWORD} profile show 2>&1)

# Parse the Device Id from the output stored in the 'ACTIVE_PROFILE' variable
ACTIVE_DEVICE_ID=$(echo $ACTIVE_PROFILE \
  | sed -e 's/.*DeviceId:\(.*\) Server:.*/\1/')

# Print the active Device Id
echo "ACTIVE PROFILE:" $ACTIVE_DEVICE_ID
echo ''

# Set the active profile
echo "SETTING NEW ACTIVE PROFILE: ABcd.4.9f521b6f-ac9e-48bc-a54a-2f1730288bf"
machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${PERSISTOR_PASSWORD} \
  profile set -d 'ABcd.4.9f521b6f-ac9e-48bc-a54a-2f1730288bf'

# Once again, store the output of the profile show command in a variable
NEW_ACTIVE_PROFILE=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${PERSISTOR_PASSWORD} profile show 2>&1)

# Parse the Device Id from the output stored in the 'NEW_ACTIVE_PROFILE' variable
NEW_ACTIVE_DEVICE_ID=$(echo $NEW_ACTIVE_PROFILE \
  | sed -e 's/.*DeviceId:\(.*\) Server:.*/\1/')

# Print the active device Id
echo "NEW ACTIVE PROFILE:" $NEW_ACTIVE_DEVICE_ID
