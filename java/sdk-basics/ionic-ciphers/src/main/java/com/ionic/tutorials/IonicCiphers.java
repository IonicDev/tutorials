/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.tutorials;

import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysRequest;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysResponse;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;
import com.ionic.sdk.error.IonicException;
import java.util.List;
import static java.util.Arrays.asList;
import javax.xml.bind.DatatypeConverter;

public class IonicCiphers
{
    public static void main(String[] args)
    {
        // read persistor password from environment variable
        String persistorPassword = System.getenv("IONIC_PERSISTOR_PASSWORD");
        if (persistorPassword == null) {
            System.out.println("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            System.exit(1);
        }

        // initialize agent
        Agent agent = new Agent();
        try {
            String persistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pw";
            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword(persistorPath);
            persistor.setPassword(persistorPassword);
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        // set app metadata 
        agent.setMetadata("ionic-application-name", "ionic-ciphers-tutorial");
        agent.setMetadata("ionic-application-version", "1.0.0");        

        //##########################################################################
        // SENDER
        //##########################################################################

        String message = "this is a secret message";

        // initialize chunk cipher object
        ChunkCipherAuto senderCipher = new ChunkCipherAuto(agent);

        // encrypt
        String ciphertext = null;
        try {
            ciphertext = senderCipher.encrypt(message.getBytes());
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("CIPHERTEXT    : " + ciphertext);

        //##########################################################################
        // RECEIVER
        //##########################################################################

        // initialize aes cipher
        ChunkCipherAuto receiverCipher = new ChunkCipherAuto(agent);

        // decrypt
        String plaintext = null;
        try {
            plaintext = receiverCipher.decrypt(ciphertext);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("PLAINTEXT     : " + plaintext);
    }
}
