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
  enrollmentUrl: 'https://preview-enrollment.ionic.com/keyspace/HVzG/register',
};

const main = async () => {
  // Initialize the Ionic agent.
  const agent = new window.IonicSdk.ISAgent();
  await agent.loadUser(appData).catch((error) => {
    console.error('Error initializing ionic agent.');
    throw error;
  });

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

  // Create single key with fixed and mutable attributes.
  const response = await agent.createKeys({
    quantity: 1,
    attributes: fixedAttributes,
    mutableAttributes: mutableAttributes,
  }).catch((error) => {
    console.error('Error creating key.');
    throw error;
  });
  const createdKey = response.keys[0];

  // Display new key.
  console.log(`\nNEW KEY:`);
  console.log(`KeyId    : ${createdKey.keyId}`);
  console.log(`KeyBytes : ${createdKey.key}`);
  console.log(`FixedAttributes   : ${JSON.stringify(createdKey.attributes, null, 0)}`);
  console.log(`MutableAttributes : ${JSON.stringify(createdKey.mutableAttributes, null, 0)}`);

  // Save the key ID.
  const keyId = createdKey.keyId;

  // Fetch the key by KeyId.
  const fetchedResponse = await agent.getKeys({keyIds: [keyId]}).catch((error) => {
    console.error('Error getting key.');
    throw error;
  });

  const fetchedKey = fetchedResponse.keys[0];

  // Display fetched key.
  console.log(`\nFETCHED KEY:`);
  console.log(`KeyId    : ${fetchedKey.keyId}`);
  console.log(`KeyBytes : ${fetchedKey.key}`);
  console.log(`FixedAttributes   : ${JSON.stringify(fetchedKey.attributes, null, 0)}`);
  console.log(`MutableAttributes : ${JSON.stringify(fetchedKey.mutableAttributes, null, 0)}`);

  // Define new mutable attributes.
  const newMutableAttributes = {
    'classification': ['Highly Restricted'],
  };

  // Merge new and existing mutable attributes.
  Object.assign(mutableAttributes, newMutableAttributes);

  // Update the key.
  const updatedResponse = await agent.updateKeys({
    keyRequests: [{
      keyId: fetchedKey.keyId,
      force: true,
      mutableAttributes: mutableAttributes,
    }],
  }).catch((error) => {
    console.error('Error updating key.');
    throw error;
  });

  const updatedKeyId = updatedResponse.keys[0].keyId;

  // To display the updated key, a fetch key is required.
  const getUpdatedResponse = await agent.getKeys({
    keyIds: [updatedKeyId],
  }).catch((error) => {
    console.error('Error fetching updated key.');
    throw error;
  });

  const updatedKey = getUpdatedResponse.keys[0];

  // Display updated key.
  console.log(`\nUPDATED KEY:`);
  console.log(`KeyId    : ${updatedKey.keyId}`);
  console.log(`KeyBytes : ${updatedKey.key}`);
  console.log(`FixedAttributes   : ${JSON.stringify(updatedKey.attributes, null, 0)}`);
  console.log(`MutableAttributes : ${JSON.stringify(updatedKey.mutableAttributes, null, 0)}`);
};

main();
