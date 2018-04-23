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
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherV2;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysRequest;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.cipher.aes.AesCtrCipher;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;

public class DataPolicy1
{
    public static void main(String[] args) throws SdkException
    {
        String plaintext = "this is a secret message";

        // initialize user profile agent
        String userPersistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
        Agent agent1 = new Agent();
        agent1.initialize(new DeviceProfilePersistorPlainText(userPersistorPath));

        // initialize test profile agent
        Agent agent2 = new Agent();
        agent2.initialize(new DeviceProfilePersistorPlainText("test-profile.pt"));

        // protect message
        System.out.println("\n[>] Agent1: Protecting message");
        ChunkCipherAuto chunkCipher1 = new ChunkCipherAuto(agent1);
        String encryptedMessage = chunkCipher1.encrypt(plaintext);
        String encryptedMessageKeyId = chunkCipher1.getChunkInfo(encryptedMessage).getKeyId();
        System.out.println("    CIPHERTEXT: " + encryptedMessage);

        // decrypt message attempt #1
        try {
            System.out.println("\n[>] Agent2: Decrypting message");
            ChunkCipherAuto decryptCipher = new ChunkCipherAuto(agent2);
            String message = decryptCipher.decrypt(encryptedMessage);
            System.out.println("    MSG: " + message);
        } catch (SdkException e) {
            System.out.println("    MSG: [!] ACCESS DENIED");
        }

        // grant access 
        System.out.println("\n[>] Agent1: Granting test@ionic.com access to key " + encryptedMessageKeyId);
        GetKeysResponse.Key key = agent1.getKey(encryptedMessageKeyId).getKeys().get(0);
        KeyAttributesMap grantAccessAttrs = key.getMutableAttributes();
        List<String> permitUserValues = new ArrayList<String>();
        permitUserValues.add("test@ionic.com");
        grantAccessAttrs.set("ionic-permit-user-by-email", permitUserValues);
        UpdateKeysRequest grantUpdateKeysRequest = new UpdateKeysRequest();
        UpdateKeysRequest.Key grantUpdateKey = new UpdateKeysRequest.Key(key, false);
        grantUpdateKey.setMutableAttributes(grantAccessAttrs);
        grantUpdateKeysRequest.add(grantUpdateKey);
        UpdateKeysResponse grantUpdateKeysResponse = agent1.updateKeys(grantUpdateKeysRequest);

        // decrypt message attempt #2
        try {
            System.out.println("\n[>] Agent2: Decrypting message");
            ChunkCipherAuto decryptCipher = new ChunkCipherAuto(agent2);
            String message = decryptCipher.decrypt(encryptedMessage);
            System.out.println("    MSG: " + message);
        } catch (SdkException e) {
            System.out.println("    MSG: [!] ACCESS DENIED");
        }

        // revoke access 
        System.out.println("\n[>] Agent1: Revoking test@ionic.com access to key " + encryptedMessageKeyId);
        KeyAttributesMap revokeAccessAttrs = key.getMutableAttributes();
        List<String> revokeUserValues = new ArrayList<String>();
        revokeUserValues.add("");
        revokeAccessAttrs.set("ionic-permit-user-by-email", revokeUserValues);
        UpdateKeysRequest revokeUpdateKeysRequest = new UpdateKeysRequest();
        UpdateKeysRequest.Key revokeUpdateKey = new UpdateKeysRequest.Key(key, true);
        revokeUpdateKey.setMutableAttributes(revokeAccessAttrs);
        revokeUpdateKeysRequest.add(revokeUpdateKey);
        UpdateKeysResponse revokeUpdateKeysResponse = agent1.updateKeys(revokeUpdateKeysRequest);

        // decrypt message attempt #3
        try {
            System.out.println("\n[>] Agent2: Decrypting message");
            ChunkCipherAuto decryptCipher = new ChunkCipherAuto(agent2);
            String message = decryptCipher.decrypt(encryptedMessage);
            System.out.println("    MSG: " + message);
        } catch (SdkException e) {
            System.out.println("    MSG: [!] ACCESS DENIED");
        }

    }
}
