/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.tutorials;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.error.IonicException;
import java.util.List;

public class Profiles
{
    public static void main(String[] args)
    {
        // initialize agent with password persistor
        Agent agent = new Agent();
        try {
            String persistorPassword = "ionic123";
            String persistorPath = "../../../sample-data/persistors/sample-persistor.pw";
            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword(persistorPath);
            persistor.setPassword(persistorPassword);
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // verify there are profiles in persistor
        List<DeviceProfile> profiles = agent.getAllProfiles();
        if (profiles.size() == 0) {
            System.out.println("No profiles found in specified profile persistor");
            System.exit(1);
        }

        // list all available profiles
        System.out.println("ALL PROFILES:");
        for (DeviceProfile profile : profiles) {
            System.out.println("---");
            System.out.println("Id       : " + profile.getDeviceId());
            System.out.println("Name     : " + profile.getName());
            System.out.println("Keyspace : " + profile.getKeySpace());
            System.out.println("ApiUrl   : " + profile.getServer());
        }

        // verify there is an active profile
        if (!agent.hasActiveProfile()) {
            System.out.println("No profile set as active");
            System.exit(1);
        }

        // display active profile
        DeviceProfile activeProfile = agent.getActiveProfile();
        System.out.println("\nACTIVE PROFILE: " + activeProfile.getDeviceId());

        // change active profile
        String newProfileId = "EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73";
        System.out.println("\nSETTING NEW ACTIVE PROFILE: " + newProfileId);
        agent.setActiveProfile(newProfileId);

        // display new active profile
        DeviceProfile newActiveProfile = agent.getActiveProfile();
        System.out.println("\nNEW ACTIVE PROFILE: " + newActiveProfile.getDeviceId());
    }
}
