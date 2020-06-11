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

"use strict";
import {getAgentConfig} from '../../jssdkConfig.js';

const main = async () => {
  
  // Get the tutorial application data. This assures all tutorils use the same
  // app ID, user ID and user authentication. It matches what was used for enrollment.
  const appData = getAgentConfig('Javascript Profiles Tutorial');

  // Initialize the Machina agent.
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    if (resp.autoProfileLoadResult.sdkResponseCode !== 0) {
      console.log('Error: ' + resp.autoProfileLoadResult.error);
      return;
    }
    const agent = resp.agent;

    const activeResp = await agent.getActiveProfile().catch((error) => {
      console.log('NO ACTIVE PROFILE: ' + error.error);
    });

    if (activeResp === undefined) {
      return;
    }

    // Get all the profiles.
    let response = await agent.queryProfiles(appData).catch((errorResp) => {
      console.log('Query profiles error: ' +  errorResp.error);
    });
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
    response = await agent.getActiveProfile().catch((errorResp) => {
      console.log('Get Active Profile error: ' +  errorResp.error);
    });
    const activeDeviceId = response.deviceId;

    // Display active profile.
    console.log('\nACTIVE PROFILE: ' + activeDeviceId);
    console.log(' ');

    // If the number of profiles is equal to one, then there is nothing to set.
    if (profiles.length === 1) {
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
    const profile_to_set =
      { appId: appData.appId,
        userId: appData.userId,
        userAuth: appData.userAuth,
        deviceId: newProfileId
      };
  
    // Set the active profile.
    const set_active_profile_resp = await agent.setActiveProfile(profile_to_set).catch((error) => {
      console.log('Set active profile error: ' +  error);
    });

    // Get the updated active profile.
    response = await agent.getActiveProfile().catch((errorResp) => {
      console.log('Get Active Profile error: ' +  errorResp.error);
    });
    const newActiveDeviceId = response.deviceId;

    // Display active profile.
    console.log('\nNEW ACTIVE PROFILE: ' + newActiveDeviceId);

  } catch (errorResp) {
    console.error('Error initializing ionic agent:' + errorResp.error);
  }
};

main();
