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

const main = async () => {
  
  // Get the tutorial application data. This assures all tutorials use the same
  // app ID, user ID and user authentication. It matches what was used for enrollment.
  const appData = getAgentConfig('Javascript Agents Tutorial');
  let response;

  // Initialize the Machina agent.
  try {
    response = await new window.IonicSdk.ISAgent(appData);
  } catch (errorResp) {
    console.error('Error initializing ionic agent: ' + errorResp.error);
  }
  const agent = response.agent;

  // Create single key to verify agent.
  try {
    response = await agent.createKeys({ quantity: 1 });
  } catch (errorResp) {
    console.log('Error creating key: ' + errorResp.error);
    return;
  }

  const createdKey = response.keys[0];

  // Display new key.
  console.log('');
  console.log('Created New Key : ' + createdKey.keyId);
};

main();
