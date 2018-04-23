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

public class Agents
{
    public static void main(String[] args) throws SdkException
    {
        // load default plaintext persistor
        String persistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
        DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText(persistorPath);

        // initialize agent
        Agent agent = new Agent(persistor);

        // set the application name and version 
        MetadataMap appMetadata = new MetadataMap();
        appMetadata.set("ionic-app-name", "tutorial-01-app");
        appMetadata.set("ionic-app-version", "1.0.0");
        agent.setMetadata(appMetadata);

        // create key
        CreateKeysResponse.Key key = agent.createKey().getKeys().get(0);
        System.out.println("[>] CREATED NEW KEY: " + key.getId());
    }
}
