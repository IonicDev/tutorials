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
  
  // Get the tutorial application data. This assures all tutorils use the same
  // app ID, user ID and user authentication. It matches what was used for enrollment.
  const appData = getAgentConfig('Javascript Profiles Tutorial');
  let response;

  // Initialize the Machina agent.
  try {
    response = await new window.IonicSdk.ISAgent(appData);
  } catch (errorResp) {
    console.error('Error initializing ionic agent:' + errorResp.error);
  }
  const agent = response.agent;

  // Get all the profiles.
  try {
    response = await agent.queryProfiles(appData);
   } catch (errorResp) {
      console.log('Query profiles error: ' +  errorResp.error);
      return;
   }
  const profiles = response.profiles;

  // Verify there is at least one profile.
  if (profiles.length === 0) {
    console.log('No profiles found');
    return;
  }
    
  // List all available profiles.
  let index, len;
  console.log('');
  console.log('ALL PROFILES:');
  for (index = 0, len = profiles.length; index < len; ++index) {
    console.log(' ');
    console.log('Device ID : ' + profiles[index].deviceId);
    console.log('Created&nbsp;&nbsp; : ' + profiles[index].created);
    console.log('Keyspace&nbsp;  : ' + profiles[index].keyspace);
    console.log('ApiUrl&nbsp;&nbsp&nbsp; : ' + profiles[index].server);
  }
  console.log(' ');

  // Get the active profile.
  let activeProfile = true;
  try {
    response = await agent.getActiveProfile();
  } catch (errorResp) {
    console.log('Get Active Profile error: ' +  errorResp.error);
    activeProfile = false;
  }
  const activeDeviceId = response.deviceId;

  // Display active profile.
  if (activeProfile) {
    console.log('\nACTIVE PROFILE: ' + activeDeviceId);
  } else {
    console.log('\nNO ACTIVE PROFILE');
  }
  console.log(' ');
  
  // If there is an active profile and only one profile, then there is nothing to set.
  if (activeProfile && profiles.length === 1) {
    console.log('Only one profile, nothing to change.');
    return;
  }

  // Search or a non-active profile.
  let newProfileId = '';
  for (index = 0, len = profiles.length; index < len; ++index) {
    if (! profiles[index].active) {
      newProfileId = profiles[index].deviceId;
      break;
    }
  }

  if (newProfileId === '') {
    console.log('Didn\'t find a non-active profile');
    return;
  }

  // Change the active profile.
  console.log('\nSETTING NEW ACTIVE PROFILE: ' +  newProfileId);

  // Define the profile to make active.
  const profileToSet =
    { appId: appData.appId,
      userId: appData.userId,
      userAuth: appData.userAuth,
      deviceId: newProfileId
    };

  // Set the active profile.
  try {
    response = await agent.setActiveProfile(profileToSet);
  } catch (errorResp) { 
    console.log('Set active profile error: ' +  errorResp.error);
    return;
  }

  // Get the updated active profile.
  try {
    response = await agent.getActiveProfile();
  } catch (errorResp) {
    console.log('Get Active Profile error: ' +  errorResp.error);
    return;
  }
  const newActiveDeviceId = response.deviceId;

  // Display active profile.
  console.log('\nNEW ACTIVE PROFILE: ' + newActiveDeviceId);
};

main();
