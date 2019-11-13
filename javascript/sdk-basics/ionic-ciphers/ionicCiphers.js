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

// AppData for all Javascript samples: appId, userId, and userAuth needs to be the same
// as the appData that was used for enrollment.
const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  metadata: {
    'ionic-application-name': 'Javascript Chuck Ciphers Tutorial',
    'ionic-application-version': '1.3.0'
  }
};

const main = async () => {

  // Initialize the Machina agent.
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    /**********************************************************
     ** SENDER
     **********************************************************/
    const message = 'This is a secret message!';

    console.log('');
    console.log('Plain text:     ' + message);
  
    // encrypt data
    const encryptResponse = await agent.encryptStringChunkCipher({stringData: message});
    const cipherText = encryptResponse.stringChunk;
  
    console.log('Sending Encrypted Text: ' + cipherText);
  
    /**********************************************************
     ** RECEIVER
     **********************************************************/
  
    // decrypt data
    const decryptResponse = await agent.decryptStringChunkCipher({stringData: cipherText});
    const decryptedText = decryptResponse.stringChunk;
  
    console.log('-----------------------------------------------------------------------------------------------------');
    console.log('Receiving Encrypted Text: ' + cipherText);
    console.log('Decrypted Text: ' + decryptedText);

  } catch (error) {
    console.error('Error initializing ionic agent:' + error);
  };
}

main();
