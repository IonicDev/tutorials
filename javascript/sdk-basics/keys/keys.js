/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

/*
 ** PRE-REQUISITES **
 * 1. A Machina tenant. You can obtain one at: https://ionic.com/start-for-free/.
 * 2. Your device, in this case the browser, needs to be enrolled. This is done 
 *    when you enrolled your device after signing up for a tenant. Enrollment 
 *    can also be accomplished by executing the Enroll Device script at:
 *    ../../enroll-device/index.html.
 */

import {getAgentConfig} from '../../jssdkConfig.js';

function printKey( text, key ) {
  console.log(text);
  console.log(`KeyId    : ${key.keyId}`);
  console.log(`KeyBytes : ${key.key}`);
  console.log(`FixedAttributes   : ${JSON.stringify(key.attributes, null, 0)}`);
  console.log(`MutableAttributes : ${JSON.stringify(key.mutableAttributes, null, 0)}`);
  console.log(' ');
}

const main = async () => {

  // Get the tutorial application data. This assures all tutorials use the same
  // app ID, user ID and user authentication. It matches what was used for enrollment.
  const appData = getAgentConfig('JavaScript Keys Tutorial');
  let response;

  // Initialize the Machina agent.
  try {
    response = await new window.IonicSdk.ISAgent(appData);
  } catch (errorResp) {
    console.error('Error initializing ionic agent:' + errorResp);
    return;
  }
  const agent = response.agent;

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
  try {
    response = await agent.createKeys({
      quantity: 1,
      attributes: fixedAttributes,
      mutableAttributes: mutableAttributes
    });
  } catch (errorResp) {
    console.log('Error creating key: ' + errorResp);
    return;
  }

  // Save the key ID.
  const createdKey = response.keys[0];
  const keyId = createdKey.keyId;

  // Display new key.
  console.log('');
  printKey('New Key:', createdKey);

  // Fetch key by Key ID.
  try {
    response = await agent.getKeys({keyIds: [keyId]});
  } catch (errorResp) {
      console.log('Error getting key: ' + errorResp);
      return;
  }

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
    });
    } catch (errorResp) {
      console.log('Error updating key: ' + errorResp);
      return;
  }

  const updatedKeyId = response.keys[0].keyId;

  // To display the updated key, a fetch key is required.
  try {
    response = await agent.getKeys({
      keyIds: [updatedKeyId],
      });
  } catch (errorResp) {
    console.log('Error fetching updated key: ' + errorResp);
    return;
  }

  const updatedKey = response.keys[0];

  // Display updated key.
  printKey('Updated Key:', updatedKey);

};

main();
