/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// Specify the user data.
const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  enrollmentUrl: 'https://dev-enrollment.ionic.com/keyspace/HVzG/register'
}

const main = async () => {

  // Initialize the Ionic agent.
  const agent = new window.IonicSdk.ISAgent()
  await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ', error)
  })

  // Define fixed attributes.
  const fixedAttributes = {
    'data-type': ['Finance'],
    'region': ['North America']
  }

  // Define mutable attributes.
  const mutableAttributes = {
    'classification': ['Restricted'],
    'designated_owner': ['joe@hq.example.com']
  }

  // Create single key with fixed and mutable attributes.
  const response = await agent.createKeys({
    quantity: 1,
    attributes: fixedAttributes,
    mutableAttributes: mutableAttributes
  }).catch((error) => {
    console.log(`Error Creating Key: ${error}`)
  })
  const createdKey = response.keys[0]

  // Display new key.
  console.log(`\nNEW KEY:`)
  console.log(`KeyId    : ${createdKey.keyId}`)
  console.log(`KeyBytes : ${createdKey.key}`)
  console.log(`FixedAttributes   : ${JSON.stringify(createdKey.attributes, null, 0)}`)
  console.log(`MutableAttributes : ${JSON.stringify(createdKey.mutableAttributes, null, 0)}`)

  // Save the key ID.
  keyId = createdKey.keyId

  // Fetch the key by KeyId.
  const fetchedResponse = await agent.getKeys({keyIds: [keyId]}).catch((error) => {
    console.log(`Error getting key: ${error}`)
  })

  const fetchedKey = fetchedResponse.keys[0]

  // Display fetched key.
  console.log(`\nFETCHED KEY:`)
  console.log(`KeyId    : ${fetchedKey.keyId}`)
  console.log(`KeyBytes : ${fetchedKey.key}`)
  console.log(`FixedAttributes   : ${JSON.stringify(fetchedKey.attributes, null, 0)}`)
  console.log(`MutableAttributes : ${JSON.stringify(fetchedKey.mutableAttributes, null, 0)}`)

  // Define new mutable attributes.
  const newMutableAttributes = {
    'classification': ['Highly Restricted'],
  }

  // Merge new and existing mutable attributes.
  var updatedAttributes = mutableAttributes
  for (var key in newMutableAttributes) {
    value = newMutableAttributes[key]
    updatedAttributes[key] = value
  }
  
  // Update the key.
  const updatedResponse = await agent.updateKeys({
    keyRequests: [{
      keyId: fetchedKey.keyId,
      force: true,
      mutableAttributes: updatedAttributes
    }]
  }).catch((error) => {
    console.log(`Error updating key: ${error}`)
  })

  // To display the updated key, a fetch key is required.
  const getUpdatedResponse = await agent.getKeys({keyIds: [keyId]}).catch((error) => {
    console.log(`Error getting key: ${error}`)
  })

  var updatedKey = getUpdatedResponse.keys[0]

  // Display updated key.
  console.log(`\nUPDATED KEY:`)
  console.log(`KeyId    : ${updatedKey.keyId}`)
  console.log(`KeyBytes : ${updatedKey.key}`)
  console.log(`FixedAttributes   : ${JSON.stringify(updatedKey.attributes, null, 0)}`)
  console.log(`MutableAttributes : ${JSON.stringify(updatedKey.mutableAttributes, null, 0)}`)

}

main()
