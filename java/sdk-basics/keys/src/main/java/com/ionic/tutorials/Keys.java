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
import com.ionic.sdk.error.IonicException;
import java.util.List;
import static java.util.Arrays.asList;
import javax.xml.bind.DatatypeConverter;

public class Keys
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
        agent.setMetadata("ionic-application-name", "ionic-keys-tutorial");
        agent.setMetadata("ionic-application-version", "1.0.0");        

        // define fixed attributes
        KeyAttributesMap fixedAttributes = new KeyAttributesMap();
        fixedAttributes.put("data-type", asList("Finance"));
        fixedAttributes.put("region", asList("North America"));

        // define mutable attributes
        KeyAttributesMap mutableAttributes = new KeyAttributesMap();
        mutableAttributes.put("classification", asList("Restricted"));
        mutableAttributes.put("designated-owner", asList("joe@hq.example.com"));

        // create new key with fixed and mutable attributes 
        CreateKeysResponse.Key createdKey = null;
        try {
            createdKey = agent.createKey(fixedAttributes, mutableAttributes).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display new key
        System.out.println("\nNEW KEY:");
        System.out.println("KeyId        : " + createdKey.getId());
        System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(createdKey.getKey()));
        System.out.println("FixedAttrs   : " + createdKey.getAttributesMap());
        System.out.println("MutableAttrs : " + createdKey.getMutableAttributes());

        // get key by keyId
        GetKeysResponse.Key fetchedKey = null;
        try {
            fetchedKey = agent.getKey(createdKey.getId()).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display fetched key
        System.out.println("\nFETCHED KEY:");
        System.out.println("KeyId        : " + fetchedKey.getId());
        System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(fetchedKey.getKey()));
        System.out.println("FixedAttrs   : " + fetchedKey.getAttributesMap());
        System.out.println("MutableAttrs : " + fetchedKey.getMutableAttributes());

        // define new mutable attributes
        KeyAttributesMap newMutableAttributes = new KeyAttributesMap();
        newMutableAttributes.put("classification", asList("Highly Restricted"));

        // merge new and existing mutable attributes
        KeyAttributesMap updatedAttributes = new KeyAttributesMap(fetchedKey.getMutableAttributes());
        updatedAttributes.putAll(newMutableAttributes);

        // update key
        UpdateKeysRequest.Key updateRequestKey = new UpdateKeysRequest.Key(fetchedKey);
        updateRequestKey.setMutableAttributes(updatedAttributes);
        UpdateKeysResponse.Key updatedKey = null;
        try {
            updatedKey = agent.updateKey(updateRequestKey).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display updated key
        System.out.println("\nUPDATED KEY:");
        System.out.println("KeyId        : " + updatedKey.getId());
        System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(updatedKey.getKey()));
        System.out.println("FixedAttrs   : " + updatedKey.getAttributesMap());
        System.out.println("MutableAttrs : " + updatedKey.getMutableAttributes());
    }
}