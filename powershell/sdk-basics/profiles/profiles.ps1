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

<# List profile(s) store in the profile persistor #>
ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} profile list

