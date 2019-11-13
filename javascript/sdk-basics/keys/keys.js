/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

/*
 * WARNING *
 * Calling agent.enrollUser() successfully is a pre-requisite before using this code.
 * This is done when you enrolled your device after signing up for a tenant.
 */

function printKey( text, key ) {
  console.log(text);
  console.log(`KeyId    : ${key.keyId}`);
  console.log(`KeyBytes : ${key.key}`);
  console.log(`FixedAttributes   : ${JSON.stringify(key.attributes, null, 0)}`);
  console.log(`MutableAttributes : ${JSON.stringify(key.mutableAttributes, null, 0)}`);
  console.log(' ');
}

// AppData for all Javascript samples: appId, userId, and userAuth needs to be the same
// as the appData that was used for enrollment.
const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  metadata: {
    'ionic-application-name': 'Javascript Keys Tutorial',
    'ionic-application-version': '1.3.0'
  }
};

const main = async () => {

  // Initialize the Machina agent.
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // Define fixed attributes.
    const fixedAttributes = {
      'data-type': ['Finance'],
      'region': ['North America'],
    };

    // Define mutable attributes.
    let mutableAttributes = {
      'classification': ['Restricted'],
      'designated_owner': ['joe@hq.example.com'],
    };

    let response;

    // Create single key with fixed and mutable attributes.
    try {
      response = await agent.createKeys({
        quantity: 1,
        attributes: fixedAttributes,
        mutableAttributes: mutableAttributes
      })
    } catch (error) {
      console.log('Error creating key: ' + error);
      return;
    }

    // Save the key ID.
    const createdKey = response.keys[0];
    const keyId = createdKey.keyId;

    // Display new key.
    console.log('');
    printKey('New Key:', createdKey);
  
    // Fetch the key by Key ID.
    try {
      response = await agent.getKeys({keyIds: [keyId]})
    } catch (error) {
        console.log('Error getting key: ' + error);
        return;
    };
  
    const fetchedKey = response.keys[0];
  
    // Display fetched key.
    printKey('Fetched Key:', fetchedKey);
  
    // Define new mutable attributes.
    const newMutableAttributes = {
      'classification': ['Highly Restricted'],
    };
  
    // Merge new and existing mutable attributes.
    Object.assign(mutableAttributes, newMutableAttributes);
  
    // Update the key.
    try {
      response = await agent.updateKeys({
        keyRequests: [{
          keyId: fetchedKey.keyId,
          force: true,
          mutableAttributes: mutableAttributes,
        }],
      })
      } catch (error) {
        console.log('Error updating key: ' + error);
        return;
    };
  
    const updatedKeyId = response.keys[0].keyId;
  
    // To display the updated key, a fetch key is required.
    try {
      response = await agent.getKeys({
        keyIds: [updatedKeyId],
        })
    } catch (error) {
      console.log('Error fetching updated key: ' + error);
      return;
    };
  
    const updatedKey = response.keys[0];
  
    // Display updated key.
    printKey('Updated Key:', updatedKey);

  } catch (error) {
    console.error('Error initializing ionic agent:' + error);
  };

};

main();
