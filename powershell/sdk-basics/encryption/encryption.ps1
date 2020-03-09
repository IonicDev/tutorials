Import-Module Microsoft.PowerShell.Utility

<# Confirm that the profile persistor password is set as an environment variable #>
if (-not (Get-Variable IONIC_PERSISTOR_PASSWORD -ErrorAction 'Ignore')) {
  echo "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD"
  exit 1
}

<# Confirm that the profile persistor file exists #>
$PERSISTOR_PATH="$HOME/.ionicsecurity/profiles.pw"
if (-not (Test-Path -path $PERSISTOR_PATH)) {
  echo "[!] '$PERSISTOR_PATH' does not exist"
  exit 1
}

<# Set the current applications name and version #>
$ClientMetadata="ionic-application-name:Keys CLI Tutorial,ionic-application-version:1.0.0"

<# Sample message to encrypt #>
$MESSAGE="'this is a secret message!'"
echo "ORIGINAL TEXT      : ${MESSAGE}"

<# Create new key and push it to the internal stack and then stores it in the key vault #>
$JSON=$(machina `
  --devicetype password `
  --devicefile "${PERSISTOR_PATH}" `
  --devicepw ${IONIC_PERSISTOR_PASSWORD} `
  vault load `
  key create --push --metas "${ClientMetadata}" `
  vault store )

<# Parse the 'keyId' from the new key response #>
$JSON_OBJECT=($JSON | ConvertFrom-Json)
$KEY_ID=$JSON_OBJECT.Keys[0].keyId

<# Display new key #>
echo "CREATED KEYID      : $KEY_ID"

<# Fetch the key from the key vault and use it to encrypt a string #>
$ENCRYPTED_MESSAGE=$(machina `
  --devicetype password `
  --devicefile ${PERSISTOR_PATH} `
  --devicepw ${IONIC_PERSISTOR_PASSWORD} `
    vault load `
    vault fetch --keyids ${KEY_ID} --push `
    chunk encrypt -s "${MESSAGE}" --pull --metas "${ClientMetadata}")

echo "CIPHER TEXT        : ${ENCRYPTED_MESSAGE}"

<# Fetch the key from the key vault and use it to decrypt a string #>
$MESSAGE=$(machina `
  --devicetype password `
  --devicefile ${PERSISTOR_PATH} `
  --devicepw ${IONIC_PERSISTOR_PASSWORD} `
    vault load `
    vault fetch --keyids ${KEY_ID} --push `
    chunk decrypt --vault -s "${ENCRYPTED_MESSAGE}" --metas "${ClientMetadata}")

echo "PLAIN TEXT         : ${MESSAGE}"