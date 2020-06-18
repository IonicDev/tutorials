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
  const appData = getAgentConfig('Javascript Chunk Ciphers Tutorial');
  let response;

  // Initialize the Machina agent.
  try {
    response = await new window.IonicSdk.ISAgent(appData);
  } catch (errorResp) {
    console.error('Error initializing ionic agent:' + errorResp);
    return;
  }
  const agent = response.agent;

  /**********************************************************
   ** SENDER
   **********************************************************/
  const message = 'This is a secret message!';

  console.log('');
  console.log('Plain text:     ' + message);

  // encrypt data
  try {
    response = await agent.encryptStringChunkCipher({stringData: message});
  } catch (errorResp) {
    console.error('Error encrypting string:' + errorResp);
    return;
  }
  const cipherText = response.stringChunk;

  console.log('Sending Encrypted Text: ' + cipherText);

  /**********************************************************
   ** RECEIVER
   **********************************************************/
  console.log('-----------------------------------------------------------------------------------------------------');
  console.log('Receiving Encrypted Text: ' + cipherText);

  // decrypt data
  try {
    response = await agent.decryptStringChunkCipher({stringData: cipherText});
  } catch (errorResp) {
    console.error('Error decrypting:' + errorResp);
    return;
  }
  const decryptedText = response.stringChunk;
  console.log('Decrypted Text: ' + decryptedText);
};

main();
