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

# Sample message to encrypt
MESSAGE='this is a secret message!'
echo "ORIGINAL TEXT      : ${MESSAGE}"

# Create new key and push it to the internal stack and then stores it in the key vault
JSON=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    vault load \
    key create --push \
    vault store )

# TODO: We could check in the binary for a bash json parser (i.e 'jq') and it instead of requiring python
# TODO: We could also use an external id - but that would have to be explained
# Parse the 'keyId' from the new key response (note: requires python)
KEY_ID=$(echo $JSON | \
    python -c 'import json,sys;obj=json.load(sys.stdin);print obj["keys"][0]["keyId"]';)

echo "CREATED KEYID      : ${KEY_ID}"

# Fetch the key from the key vault and use it to encrypt a string
ENCRYPTED_MESSAGE=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    vault load \
    vault fetch --keyids ${KEY_ID} --push \
    chunk encrypt -s "${MESSAGE}" --pull)


echo "CIPHER TEXT        : ${ENCRYPTED_MESSAGE}"

# Fetch the key from the key vault and use it to decrypt a string
MESSAGE=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    vault load \
    vault fetch --keyids ${KEY_ID} --push \
    chunk decrypt --vault -s "${ENCRYPTED_MESSAGE}")

echo "PLAIN TEXT         : ${MESSAGE}"
