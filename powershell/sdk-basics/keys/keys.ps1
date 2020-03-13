<# (c) 2018-2020 Ionic Security Inc. #>
<# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) #>
<# and the Privacy Policy (https://www.ionic.com/privacy-notice/). #>

<# Use command ./keys.ps1 #>

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

<# Set the key attributes #>
$FixedAttrs="data-type:Finance,region:North-America"
$MutableAttrs="classification:Restricted,designated-owner:joe@hq.example.com"

<# Create new key with fixed and mutable attributes #>
$JSON=$(machina `
  --devicetype password `
  --devicefile "${PERSISTOR_PATH}" `
  --devicepw ${IONIC_PERSISTOR_PASSWORD} `
  key create --attrs "${FixedAttrs}" --mattrs "${MutableAttrs}" --metas "${ClientMetadata}")

<# Parse the 'keyId' from the new key response #>
$JSON_OBJECT=($JSON | ConvertFrom-Json)
$KEY_ID=$JSON_OBJECT.Keys[0].keyId

<# Display new key #>
echo "NEW KEY:"
echo $JSON_OBJECT.keys[0]

<# Get key by 'keyId' #>
$JSON=$(machina `
  --devicetype password `
  --devicefile "${PERSISTOR_PATH}" `
  --devicepw ${IONIC_PERSISTOR_PASSWORD} `
  key fetch --keyids "${KEY_ID}" --metas "${ClientMetadata}")

$JSON_OBJECT=($JSON | ConvertFrom-Json)

<# Display fetched key #>
echo "FETCH KEY:"
echo ""
echo $JSON_OBJECT.keys[0]

$UPDATED_MUTABLE_ATTRS="'classification:Highly-Restricted'"

# Update the 'classification' attribute to 'Highly-Restricted'
$JSON=$(machina `
  --devicetype password `
  --devicefile "${PERSISTOR_PATH}" `
  --devicepw ${IONIC_PERSISTOR_PASSWORD} `
  key modify --mattrs "${UPDATED_MUTABLE_ATTRS}" --keyids "${KEY_ID}" --metas "${ClientMetadata}")

$JSON_OBJECT=($JSON | ConvertFrom-Json)

<# Display updated key #>
echo "UPDATED KEY:"
echo ""
echo $JSON_OBJECT.keys[0]
