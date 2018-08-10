/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

const appData = {
    appId: 'ionic-js-samples',
    userId: 'developer',
    userAuth: 'password123',
    enrollmentUrl: 'https://dev-enrollment.ionic.com/keyspace/HVzG/register'
}

const main = async () => {
    
    // initialize agent
    const agent = new window.IonicSdk.ISAgent('https://dev-api.ionic.com/jssdk/latest/');
    await agent.loadUser(appData).catch((error) => {
        console.log(`Error loading profile: ${error}`)
    })

    // Get all the profiles.
    const response = await agent.queryProfiles(appData).catch((error) => {
        console.log(`Query profiles error: ${error}`)
    })
    const profiles = response.profiles 

    // Verify there is at least one profile.
    if (profiles.length == 0) {
        console.log(`No profiles found in specified profile persistor`);
        return
    }
        
    // List all available profiles.
    var index, len, active_device_id
    console.log( `ALL PROFILES:`);
    for (index = 0, len = profiles.length; index < len; ++index) {
        console.log(`---`)
        console.log(`ID       : ${profiles[index].deviceId}`)
        console.log(`Created  : ${profiles[index].created}`);
        console.log(`Keyspace : ${profiles[index].keyspace}`)
        console.log(`ApiUrl   : ${profiles[index].server}`)

        // Save active (passive) profile device ID for later.
        if (profiles[index].active) {
            active_device_id = profiles[index].deviceId
        }
    }

    // If the number of profiles is equal to one, then there is nothing to set.
    if (profiles.length == 1) {
        console.log(`Only one profile, nothing to change`)
        return
    }
        
    // Display active profile.
    console.log(`\nACTIVE PROFILE: ${active_device_id}`)

    // Change the active profile.
    const new_profile_id = 'HVzG.3.cf55cc46-bd06-4d00-9202-eb667f03eea4'
    console.log(`\nSETTING NEW ACTIVE PROFILE: $new_profile_id`)

    // Define the profile to make active.
    const profile_to_set =
        { appId: appData.appId,
          userId: appData.userId,
          userAuth: appData.userAuth,
          deviceId: new_profile_id
        }
    
    const set_active_profile_resp = await agent.setActiveProfile(profile_to_set).catch((error) => {
        console.log(`Set active profile error: ${error}`)
    })

    // Loop through the list of profiles looking for active profile,
    // and output information about the active profile.
    const updated_profiles = set_active_profile_resp.profiles 
    for (index = 0, len = updated_profiles.length; index < len; ++index) {
        if (updated_profiles[index].active) {
            console.log(`\nNEW ACTIVE PROFILE: ${updated_profiles[index].deviceId}`);
        }
    }
}

main();
