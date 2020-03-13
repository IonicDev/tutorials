<# (c) 2018-2020 Ionic Security Inc. #>
<# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) #>
<# and the Privacy Policy (https://www.ionic.com/privacy-notice/). #>

<# Use command ./profiles.ps1 #>

<# Confirm that the sample profile persistor file exists #>
$PERSISTOR_PATH="../../../sample-data/persistors/sample-persistor.pw"
$PERSISTOR_PASSWORD="ionic123"
if (-not (Test-Path -path $PERSISTOR_PATH)) {
  echo "[!] '$PERSISTOR_PATH' does not exist"
  exit 1
}

<# List profile(s) store in the profile persistor #>
machina `
  --devicetype password `
  --devicefile ${PERSISTOR_PATH} `
  --devicepw ${PERSISTOR_PASSWORD} profile list

<# Store the output of the profile show command in a variable #>
$ACTIVE_PROFILE=$(machina `
  --devicetype password `
  --devicefile ${PERSISTOR_PATH} `
  --devicepw ${PERSISTOR_PASSWORD} profile show 2>&1)

<# Parse the Device Id from the output stored in the 'ACTIVE_PROFILE' variable #>
$PATTERN = "DeviceId: (.*)`n Server:"
$ACTIVE_DEVICE_ID= [regex]::Match($ACTIVE_PROFILE,$PATTERN).Groups[1].Value

<# Print the active Device Id #>
echo "ACTIVE PROFILE: ${ACTIVE_DEVICE_ID}"
echo ''

<# Set the active profile #>
echo "SETTING NEW ACTIVE PROFILE: EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73"
machina `
  --devicetype password `
  --devicefile ${PERSISTOR_PATH} `
  --devicepw ${PERSISTOR_PASSWORD} `
  profile set --deviceid 'EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73'

<# Once again, store the output of the profile show command in a variable #>
$NEW_ACTIVE_PROFILE=$(machina `
  --devicetype password `
  --devicefile ${PERSISTOR_PATH} `
  --devicepw ${PERSISTOR_PASSWORD} profile show 2>&1)

<# Parse the Device Id from the output stored in the 'NEW_ACTIVE_PROFILE' variable #>
$ACTIVE_DEVICE_ID= [regex]::Match($NEW_ACTIVE_PROFILE,$PATTERN).Groups[1].Value

<# Print the active device Id #>
echo "NEW ACTIVE PROFILE: ${$NEW_ACTIVE_PROFILE}"
