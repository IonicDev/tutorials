#! /usr/bin/env bash

# (c) 2018-2020 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

# comment: use command ./message-expire.sh

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
ClientMetadata="ionic-application-name:Expire Email Tutorial,ionic-application-version:1.0.0"

SUBJECT="Machina%20Protected%20Message"

INCOMING_MESSAGE=$(pbpaste)
INCOMING_MESSAGE_INFO=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    chunk getinfo --instr "${INCOMING_MESSAGE}" | grep "encrypted" | sed -e "s/^    \"encrypted\" : //")

if [ "$INCOMING_MESSAGE_INFO" = "true" ]; then

    # Decrypt the incoming string (The correlating key is automatically fetched)
    DECRYPTED_INCOMING_MESSAGE=$(machina \
      --devicetype password \
      --devicefile ${PERSISTOR_PATH} \
      --devicepw ${IONIC_PERSISTOR_PASSWORD} \
        chunk decrypt --instr "${INCOMING_MESSAGE}" --metas "${ClientMetadata}")

    # Print the received message in plain text
    echo "INCOMING MESSAGE       : ${DECRYPTED_INCOMING_MESSAGE}"
    echo ""

    # Prompt for a response
    SUBJECT="Re:%20${SUBJECT}"
    echo "Enter a response       : "
else
    # Prompt for a message to send
    echo "Enter a secret message : "
fi

# Read message input
read MESSAGE

# Prompt for an expire duraton
echo "Expire timeout (mins)  : "

# Read expire duration
read MINS

# Calculate the expire time
EXPIRE=$(date -v +${MINS}M -u +"%Y-%m-%dT%H:%M:%SZ")

# Encrypt the message string (The key is automatically created)
ENCRYPTED_MESSAGE=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    chunk encrypt \
    --instr "${MESSAGE}" \
    --attrs "ionic-expiration:${EXPIRE}" \
    --metas "${ClientMetadata}" )

# Print the encrypted messge
echo "CIPHER TEXT            : ${ENCRYPTED_MESSAGE}"

# Open the system mail handler
open "mailto:?subject=${SUBJECT}&body=${ENCRYPTED_MESSAGE}"
