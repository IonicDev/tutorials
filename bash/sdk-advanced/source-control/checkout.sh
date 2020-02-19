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
ClientMetadata="ionic-application-name:CircleCI-Demo,ionic-application-version:1.0.0"

#awk '{if($0~/"current_key"/) {NR=NR-1;print $0; NR=NR+2;print $0; } }' ./google-services.json

# Create an array containing each API key that needs to be protected
array=( $(cat ./google-services.json | jq '(.client[].api_key[].current_key)') )

for ENCRYPTED_API_KEY in ${array[@]}
do
  # Remove quotes
  ENCRYPTED_API_KEY=$(sed -e 's/^"//' -e 's/"$//' <<<"$ENCRYPTED_API_KEY")

  # Decrypt a string (The correlating key is automatically fetched)
  API_KEY=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} \
      chunk decrypt -s "${ENCRYPTED_API_KEY}" --metas "${ClientMetadata}")

  replace='s#'${ENCRYPTED_API_KEY}'#'${API_KEY}'#'

  sed -i '' "${replace}" ./google-services.json

  echo "Detected encrypted API key '"${ENCRYPTED_API_KEY}"' and replaced it with plain text"

done
