/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package ionic.tutorials;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.ionic.sdk.error.SdkException;
import com.ionic.sdk.error.AgentErrorModuleConstants;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.data.MetadataMap;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherV2;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysRequest;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysRequest;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysResponse;
import com.ionic.sdk.agent.key.KeyAttributesMap;

public class Keys
{
    public static void main(String[] args) throws SdkException
    {
        // load default plaintext persistor
        String ptPersistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
        DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText(ptPersistorPath);

        // initialize agent
        Agent agent = new Agent();
        agent.initialize(ptPersistor);

        // define mutable key attributes 
        KeyAttributesMap mutableKeyAttrs = new KeyAttributesMap();
        List<String> classificationValues = new ArrayList<String>();
        classificationValues.add("Restricted");
        List<String> projectsValues = new ArrayList<String>();
        projectsValues.add("Phoenix");
        mutableKeyAttrs.set("classification", classificationValues);
        mutableKeyAttrs.set("projects", projectsValues);

        // define fixed key attribute 
        KeyAttributesMap fixedKeyAttrs = new KeyAttributesMap();
        List<String> datatypeValues = new ArrayList<String>();
        datatypeValues.add("Schematics");
        fixedKeyAttrs.set("datatype", datatypeValues);

        // create key with fixed and mutable attributes
        String keyRef = "demo";
        int keyCount = 1;
        CreateKeysRequest.Key requestedKey = new CreateKeysRequest.Key(keyRef, keyCount, fixedKeyAttrs, mutableKeyAttrs);
        CreateKeysRequest createKeyRequest = new CreateKeysRequest();
        createKeyRequest.getKeys().add(requestedKey);
        CreateKeysResponse createKeyResponse = agent.createKeys(createKeyRequest);
        List<CreateKeysResponse.Key> createdKeys = createKeyResponse.getKeys();
        CreateKeysResponse.Key createdKey0 = createdKeys.get(0); 

        // display the created key
        String keyId = createdKey0.getId();
        System.out.println("[>] CREATED KEY:");
        System.out.println("    ID: " + keyId);

        // get key by keyId
        System.out.println("\n[>] FETCHING KEY: " + createdKey0.getId());
        GetKeysRequest getKeysRequest = new GetKeysRequest();
        getKeysRequest.add(keyId);
        GetKeysResponse getKeysResponse = agent.getKeys(getKeysRequest);
        List<GetKeysResponse.Key> keys = getKeysResponse.getKeys();
        GetKeysResponse.Key key0 = keys.get(0);

        // display fetched key fixed attributes
        System.out.println("\n[>] " + key0.getId() + " FIXED ATTRIBUTES:");
        KeyAttributesMap fixedAttributes = key0.getAttributesMap();
        for (Map.Entry<String, List<String>> attr : fixedAttributes.entrySet()) {
            System.out.println("    " + attr);
        }

        // display fetched key mutable attributes
        System.out.println("\n[>] " + key0.getId() + " MUTABLE ATTRIBUTES:");
        KeyAttributesMap mutableAttributes = key0.getMutableAttributes();
        for (Map.Entry<String, List<String>> attr : mutableAttributes.entrySet()) {
            System.out.println("    " + attr);
        }

        // create updated mutable attributes
        KeyAttributesMap updatedMutableKeyAttrs = key0.getMutableAttributes();
        List<String> updatedClassificationValues = new ArrayList<String>();
        updatedClassificationValues.add("Highly Restricted");
        updatedMutableKeyAttrs.set("classification", updatedClassificationValues);

        // update key 
        System.out.println("\n[>] UPDATING KEY: " + createdKey0.getId());
        boolean forceUpdate = false;
        UpdateKeysRequest updateKeysRequest = new UpdateKeysRequest();
        UpdateKeysRequest.Key updateKey = new UpdateKeysRequest.Key(key0, forceUpdate);
        updateKey.setMutableAttributes(updatedMutableKeyAttrs);
        updateKeysRequest.add(updateKey);
        UpdateKeysResponse updateKeysResponse = agent.updateKeys(updateKeysRequest);
        UpdateKeysResponse.Key updatedKey = updateKeysResponse.getKeys().get(0);

        // display updated mutable attributes
        System.out.println("\n[>] " + updatedKey.getId() + " UPDATED MUTABLE ATTRIBUTES:");
        KeyAttributesMap updatedMutableAttributes = updatedKey.getMutableAttributes();
        for (Map.Entry<String, List<String>> attr : updatedMutableAttributes.entrySet()) {
            System.out.println("    " + attr);
        }

    }
}
