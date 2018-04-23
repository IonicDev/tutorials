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
import com.ionic.sdk.agent.cipher.chunk.data.ChunkCryptoChunkInfo;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.cipher.aes.AesCtrCipher;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;

public class IonicCiphers
{
    public static void main(String[] args) throws SdkException
    {
        String message = "this is a secret message";

         // load default plaintext persistor
        String ptPersistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
        DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText(ptPersistorPath);

        // initialize agent
        Agent agent = new Agent();
        agent.initialize(ptPersistor);

        // encrypt
        ChunkCipherAuto encryptCipher = new ChunkCipherAuto(agent);
        String ciphertext = encryptCipher.encrypt(message);
        System.out.println("[>] ENCRYPTING WITH CHUNKCIPHER:");
        System.out.println("    MESSAGE    : " + message);
        System.out.println("    CIPHERTEXT : " + ciphertext);

        // get ciphertext info
        ChunkCryptoChunkInfo ciphertextInfo = encryptCipher.getChunkInfo(ciphertext);
        System.out.println("\n[>] EXTRACTING INFO:");
        System.out.println("    FORMAT : " + ciphertextInfo.getCipherId());
        System.out.println("    KEYID  : " + ciphertextInfo.getKeyId());

        // decrypt
        ChunkCipherAuto decryptCipher = new ChunkCipherAuto(agent);
        String plaintext = decryptCipher.decrypt(ciphertext);
        System.out.println("\n[>] DECRYPTING WITH CHUNKCIPHER:");
        System.out.println("    CIPHERTEXT : " + ciphertext);
        System.out.println("    PLAINTEXT  : " + plaintext);
    }
}
