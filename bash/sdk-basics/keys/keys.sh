#! /usr/bin/env bash
# comment: use command ./profile.sh

# Confirm that the profile persistor password is set as an environment variable
if [[ -z "${IONIC_PERSISTOR_PASSWORD}" ]]; then
  echo "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD"
  exit 1
fi

# Confirm that the profile persistor file exists
PERSISTOR_PATH="${HOME}/.ionicsecurity/profiles.pw"
if [[ ! -f "$PERSISTOR_PATH" ]]; then
    echo "[!] '$PERSISTOR_PATH' does not exist"
    exit 1
fi

# exit when any command fails
set -e

# Set the current applications name and version
ClientMetadata="ionic-application-name:Keys CLI Tutorial,ionic-application-version:1.0.0"

# Set the key attributes
FixedAttrs="'data-type:Finance,region:North-America'"
MutableAttrs="'classification:Restricted,designated-owner:joe@hq.example.com'"

# Create new key with fixed and mutable attributes
JSON=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    key create --attrs ${FixedAttrs} --mattrs ${MutableAttrs} --metas "${ClientMetadata}")

# Display new key
echo "NEW KEY:"
echo ""
echo $JSON

# Parse the 'keyId' from the new key response
KEY_ID=$(echo $JSON | awk 'BEGIN {RS=","}; /keyId/ {print $3}' | tr -d '"')

# Get key by keyId
JSON=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    key fetch --keyids ${KEY_ID} --metas "${ClientMetadata}")

# Display fetched key
echo "FETCH KEY:"
echo ""
echo $JSON

UpdatedMutableAttrs="'classification:Highly-Restricted'"

# Update the 'classification' attribute to 'Highly-Restricted'
JSON=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    key modify --mattrs ${UpdatedMutableAttrs} --keyids ${KEY_ID} --metas "${ClientMetadata}")

# Display updated key
echo "UPDATED KEY:"
echo ""
echo $JSON
