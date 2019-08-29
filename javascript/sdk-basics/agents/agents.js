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

  // Create single key.
  const response = await agent.createKeys({
    quantity: 1,
  }).catch((error) => {
    console.error('Error creating key.');
    throw error;
  });
  const createdKey = response.keys[0];

  // Display new key.
  console.log(`CREATED NEW KEY : ${createdKey.keyId}`);
};

main();
