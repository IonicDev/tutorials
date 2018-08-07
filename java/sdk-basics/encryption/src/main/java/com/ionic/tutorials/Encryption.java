/*
 * (c) 2018 Ionic Security Inc.
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
import com.ionic.sdk.cipher.aes.AesCtrCipher;
import com.ionic.sdk.error.IonicException;
import java.util.List;
import static java.util.Arrays.asList;
import javax.xml.bind.DatatypeConverter;

public class Encryption
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
        agent.setMetadata("ionic-application-name", "ionic-encryption-tutorial");
        agent.setMetadata("ionic-application-version", "1.0.0");        

        //##########################################################################
        // SENDER
        //##########################################################################

        String message = "this is a secret message";

        // create new key with fixed and mutable attributes 
        CreateKeysResponse.Key createdKey = null;
        try {
            createdKey = agent.createKey().getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // initialize aes cipher
        AesCtrCipher senderCipher = null;
        try {
            senderCipher = new AesCtrCipher();
            senderCipher.setKey(createdKey.getKey());
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // encrypt
        byte[] ciphertext = null;
        try {
            ciphertext = senderCipher.encrypt(message.getBytes());
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("CREATED KEYID : " + createdKey.getId());
        System.out.println("CIPHERTEXT    : " + DatatypeConverter.printHexBinary(ciphertext));

        //##########################################################################
        // RECEIVER
        //##########################################################################

        String keyId = createdKey.getId();

        // get key
        GetKeysResponse.Key fetchedKey = null;
        try {
            fetchedKey = agent.getKey(keyId).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        // initialize aes cipher
        AesCtrCipher receiverCipher = null;
        try {
            receiverCipher = new AesCtrCipher();
            receiverCipher.setKey(fetchedKey.getKey());
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // decrypt
        byte[] plaintext = null;
        try {
            plaintext = receiverCipher.decrypt(ciphertext);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("\nFETCHED KEYID : " + fetchedKey.getId());
        System.out.println("PLAINTEXT     : " + new String(plaintext));
    }
}