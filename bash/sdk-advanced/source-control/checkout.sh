#! /usr/bin/env bash

# (c) 2018-2020 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

# comment: use command ./checkout.sh

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
ClientMetadata="ionic-application-name:CircleCI-Demo,ionic-application-version:1.0.0"

# Create an array containing each API key that needs to be protected
array=( $(cat ./src/external-services.json | jq '(.openWeatherMap.apiKey)') )

for ENCRYPTED_API_KEY in ${array[@]}
do
  # Remove quotes
  ENCRYPTED_API_KEY=$(sed -e 's/^"//' -e 's/"$//' <<<"$ENCRYPTED_API_KEY")

  # Skip if the API key is empty
  if [ ! -z "$API_KEY" ]; then

    # Decrypt a string (The correlating key is automatically fetched)
    API_KEY=$(machina --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
        chunk decrypt -s "${ENCRYPTED_API_KEY}" --metas "${ClientMetadata}")

    replace='s#'${ENCRYPTED_API_KEY}'#'${API_KEY}'#'

    sed -i '' "${replace}" ./src/external-services.json

    echo "Detected encrypted API key '"${ENCRYPTED_API_KEY}"' and replaced it with plain text"
  fi

done
