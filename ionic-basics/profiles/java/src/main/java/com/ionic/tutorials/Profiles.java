/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package ionic.tutorials;

import java.util.List;
import java.util.ArrayList;

import com.ionic.sdk.error.SdkException;
import com.ionic.sdk.error.AgentErrorModuleConstants;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.data.MetadataMap;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherV2;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;

public class Profiles
{
    public static void main(String[] args) throws SdkException
    {
        // load default plaintext persistor
        String ptPersistorPath = "profiles.pt";
        DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText(ptPersistorPath);

        // initialize agent
        Agent agent = new Agent();
        agent.initialize(ptPersistor);

        // display available profiles
        System.out.println("[>] LISTING AVAILABLE PROFILES:");
        List<DeviceProfile> profiles = agent.getAllProfiles(); 
        for (DeviceProfile profile : profiles) {
            String profileName = profile.getName();
            String profileKeyspace = profile.getKeySpace();
            String profileId = profile.getDeviceId();
            System.out.println("    " + profileName + " : " + profileKeyspace + " : " + profileId);
        }

        // display active profile
        DeviceProfile activeProfile = agent.getActiveProfile();
        System.out.println("\n[>] ACTIVE PROFILE: " + activeProfile.getDeviceId());

        // set new active profile
        DeviceProfile lastProfile = profiles.get(profiles.size() - 1);
        System.out.println("\n[>] SETTING NEW ACTIVE PROFILE: " + lastProfile.getDeviceId());
        agent.setActiveProfile(lastProfile.getDeviceId());
        
        // display new active profile
        DeviceProfile newActiveProfile = agent.getActiveProfile();
        System.out.println("\n[>] ACTIVE PROFILE: " + newActiveProfile.getDeviceId());
    }
}
