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

# Configure this script to exit when any command fails
set -e

# Set the current applications name and version
ClientMetadata="ionic-application-name:Encryption CLI Tutorial,ionic-application-version:1.0.0"

# Sample message to encrypt
MESSAGE='this is a secret message!'
echo "ORIGINAL TEXT      : ${MESSAGE}"

# Create new key, push it to the internal stack and store it in the key vault
JSON=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    vault load \
    key create --push --metas "${ClientMetadata}"\
    vault store )

# Parse the 'keyId' from the new key response (note: requires python)
KEY_ID=$(echo $JSON | \
    python -c 'import json,sys;obj=json.load(sys.stdin);print obj["keys"][0]["keyId"]';)

echo "CREATED KEYID      : ${KEY_ID}"

# Fetch the key from the key vault and use it to encrypt a string
ENCRYPTED_MESSAGE=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    vault load \
    vault fetch --keyids ${KEY_ID} --push \
    chunk encrypt -s "${MESSAGE}" --pull --metas "${ClientMetadata}")


echo "CIPHER TEXT        : ${ENCRYPTED_MESSAGE}"

# Fetch the key from the key vault and use it to decrypt a string
MESSAGE=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    vault load \
    vault fetch --keyids ${KEY_ID} --push \
    chunk decrypt --vault -s "${ENCRYPTED_MESSAGE}" --metas "${ClientMetadata}")

echo "PLAIN TEXT         : ${MESSAGE}"
