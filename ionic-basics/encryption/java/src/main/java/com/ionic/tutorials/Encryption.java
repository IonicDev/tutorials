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
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.cipher.aes.AesCtrCipher;
import com.ionic.sdk.core.codec.Hex;

public class Encryption
{
    public static void main(String[] args) throws SdkException
    {
        byte[] message = new String("this is a secret message").getBytes();

         // load default plaintext persistor
        String ptPersistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
        DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText(ptPersistorPath);

        // initialize agent
        Agent agent = new Agent();
        agent.initialize(ptPersistor);

        // create new key 
        CreateKeysResponse.Key createdKey = agent.createKey().getKeys().get(0);

        // initialize aes cipher object
        AesCtrCipher encryptCipher = new AesCtrCipher();
        encryptCipher.setKey(createdKey.getKey());

        // encrypt
        byte[] ciphertext = encryptCipher.encrypt(message);
        String ciphertextKeyId = createdKey.getId();

        // display ciphertext
        System.out.println("[>] ENCRYPTING WITH KEY: " + ciphertextKeyId);
        System.out.println("    MESSAGE    : " + new String(message));
        System.out.println("    CIPHERTEXT : " + new Hex().encode(ciphertext));

        // get key
        GetKeysResponse.Key fetchedKey = agent.getKey(ciphertextKeyId).getKeys().get(0);

        // initialize aes cipher object
        AesCtrCipher decryptCipher = new AesCtrCipher();
        decryptCipher.setKey(fetchedKey.getKey());
        
        // decrypt data
        byte[] plaintext = decryptCipher.decrypt(ciphertext);

        // display plaintext
        System.out.println("\n[>] DECRYPTING WITH KEY: " + ciphertextKeyId);
        System.out.println("    CIPHERTEXT : " + new Hex().encode(ciphertext));
        System.out.println("    PLAINTEXT  : " + new String(plaintext));
    }
}
