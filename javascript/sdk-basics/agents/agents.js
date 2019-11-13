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
    'ionic-application-name': 'Javascript Agents Tutorial',
    'ionic-application-version': '1.3.0'
  }
};

const main = async () => {

  let response;

  // Initialize the Machina agent.
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // Create single key.
    try {
      response = await agent.createKeys({
        quantity: 1,
      })
    } catch (error) {
      console.log('Error creating key: ' + error);
      return;
    };
  } catch (error) {
    console.error('Error initializing ionic agent:' + error);
  };

  const createdKey = response.keys[0];

  // Display new key.
  console.log('');
  console.log(`Created New Key : ${createdKey.keyId}`);
};

main();
