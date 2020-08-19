<# (c) 2018-2020 Ionic Security Inc. #>
<# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) #>
<# and the Privacy Policy (https://www.ionic.com/privacy-notice/). #>

<# Use command ./default-persistor.ps1 #>

Import-Module Microsoft.PowerShell.Utility

<# Set the current applications name and version #>
$ClientMetadata="ionic-application-name:CLI Default Persistor Tutorial,ionic-application-version:1.1.0"

<# Sample message to encrypt #>
$MESSAGE="'this is a secret message!'"
echo "ORIGINAL TEXT      : ${MESSAGE}"

<# Define data markings #>
$MutableAttrs="'clearance-level:secret'"

<# Encrypt a string (The key is automatically created) #>
$ENCRYPTED_MESSAGE=$(machina `
    chunk encrypt --instr "${MESSAGE}" --mattrs "${MutableAttrs}" --metas "${ClientMetadata}")

echo "CIPHER TEXT        : ${ENCRYPTED_MESSAGE}"

<# Decrypt a string (The correlating key is automatically fetched) #>
$MESSAGE=$(machina `
    chunk decrypt --instr "${ENCRYPTED_MESSAGE}" --metas "${ClientMetadata}")

echo "PLAIN TEXT         : ${MESSAGE}"

