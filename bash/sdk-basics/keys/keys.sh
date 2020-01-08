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
ClinetMetadata="ionic-application-name:Keys CLI Tutorial,ionic-application-version:1.0.0"

# Set the key attributes
FixedAttrs="'data-type:Finance,region:North-America'"
MutableAttrs="'classification:Restricted,designated-owner:joe@hq.example.com'"

# Create new key with fixed and mutable attributes
JSON=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    key create --attrs ${FixedAttrs} --mattrs ${MutableAttrs} --metas "${ClinetMetadata}")

# Display new key
echo "NEW KEY:"
echo ""
echo $JSON

# TODO: We could check in the binary for a bash json parser (i.e 'jq') and it instead of requiring python
# TODO: We could also use an external id - but that would have to be explained
# Parse the 'keyId' from the new key response (note: requires python)
KEY_ID=$(echo $JSON | \
    python -c 'import json,sys;obj=json.load(sys.stdin);print obj["keys"][0]["keyId"]';)

# Get key by keyId
JSON=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    key fetch -i ${KEY_ID} --metas "${ClinetMetadata}")

# Display fetched key
echo "FETCH KEY:"
echo ""
echo $JSON

UpdatedMutableAttrs="'classification:Highly-Restricted'"

# Update the 'classification' attribute to 'Highly-Restricted'
JSON=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    key modify  --mattrs ${UpdatedMutableAttrs} -i ${KEY_ID} --metas "${ClinetMetadata}")

# Display updated key
echo "UPDATED KEY:"
echo ""
echo $JSON
