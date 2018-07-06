/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;
using System.Web.Script.Serialization;
using IonicSecurity.SDK;

namespace Samples
{
    class Keys
    {
        // Waits for any input for console applications.
        // This allows information to be displayed before the 
        // console application window closes.
        static void WaitForInput()
        {
            Console.WriteLine("\nPress return to exit.");
            Console.ReadKey();
            return;
        }


        // Formats JSON.
        static String JsonDump(AttributesDictionary ad)
        {
            return new JavaScriptSerializer().Serialize(ad);

        }


        static int Main(string[] args)
        {
            // Get the user's home path and password persistor from the environment.
            String homePath = Environment.GetEnvironmentVariable("USERPROFILE");

            String persistorPassword = Environment.GetEnvironmentVariable("IONIC_PERSISTOR_PASSWORD");
            if (persistorPassword == null || persistorPassword.Length == 0)
            {
                Console.WriteLine("Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
                WaitForInput();
                Environment.Exit(1);
            }

            // Create an agent object to talk to Ionic.
            Agent agent = new Agent();

            // Create a password persistor for agent initialization.
            try
            {
                DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword();
                persistor.FilePath = homePath + "\\.ionicsecurity\\profiles.pw";
                persistor.Password = persistorPassword;

                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Set the application metadata.
            try
            {
                agent.SetMetadata(Agent.MetaApplicationName, "Ionic Keys Tutorial");
                agent.SetMetadata(Agent.MetaApplicationVersion, "1.0.0");
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error setting the application metadata: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Define fixed attributes.
            AttributesDictionary fixedKeyAttrs = new AttributesDictionary();
            fixedKeyAttrs.Add("data-type", new List<string> { "Finance" });
            fixedKeyAttrs.Add("region", new List<string> { "North America" });

            // Define mutable keys.
            AttributesDictionary mutableKeyAttrs = new AttributesDictionary();
            mutableKeyAttrs.Add("classification", new List<string> { "Restricted" });
            mutableKeyAttrs.Add("designated_owner", new List<string> { "joe@hq.example.com" });

            // Create single key with fixed attributes.
            CreateKeysResponse.Key createdKey = null;
            try
            {
                createdKey = agent.CreateKey(fixedKeyAttrs, mutableKeyAttrs).Keys[0];
            }
            catch (SdkException sdkExp
            )
            {
                Console.WriteLine("Key creation error: {0}", sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Display the created key information.
            Console.WriteLine("\nNEW KEY:");
            Console.WriteLine("Key ID             : " + createdKey.Id);
            Console.WriteLine("Key Bytes          : " + BitConverter.ToString(createdKey.KeyBytes).Replace("-", String.Empty));
            Console.WriteLine("Fixed Attributes   : " + JsonDump(createdKey.Attributes));
            Console.WriteLine("Mutable Attributes : " + JsonDump(createdKey.MutableAttributes));

            // Fetch a single key from the agent.
            GetKeysResponse.Key fetchedKey = null;
            try
            {
                fetchedKey = agent.GetKey(createdKey.Id).Keys[0];
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error fetching key {0}: {1}", createdKey.Id, sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Display the fetched key information.
            Console.WriteLine("\nFETCHED KEY:");
            Console.WriteLine("Key ID             : " + fetchedKey.Id);
            Console.WriteLine("Key Bytes          : " + BitConverter.ToString(fetchedKey.KeyBytes).Replace("-", String.Empty));
            Console.WriteLine("Fixed Attributes   : " + JsonDump(fetchedKey.Attributes));
            Console.WriteLine("Mutable Attributes : " + JsonDump(fetchedKey.MutableAttributes));

            // Merge new and existing mutable attributes.
            AttributesDictionary updatedMutableKeyAttrs = fetchedKey.MutableAttributes;
            updatedMutableKeyAttrs["classification"] = new List<string> { "Highly Restricted" };

            // Create the update key request.
            bool forceUpdate = false;
            UpdateKeysRequest updateKeysRequest = new UpdateKeysRequest();
            UpdateKeysRequest.Key updateKey = new UpdateKeysRequest.Key(fetchedKey, forceUpdate);
            updateKey.MutableAttributes = updatedMutableKeyAttrs;
            updateKeysRequest.addKey(updateKey);

            // Update the key attributes on the agent.
            UpdateKeysResponse.Key updatedKey = null;
            try
            {
                updatedKey = agent.UpdateKeys(updateKeysRequest).Keys[0];
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error updating key {0}: {1}", fetchedKey.Id, sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Display the updated key information.
            Console.WriteLine("\nUPDATED KEY:");
            Console.WriteLine("Key ID             : " + updatedKey.Id);
            Console.WriteLine("Key Bytes          : " + BitConverter.ToString(updatedKey.KeyBytes).Replace("-", String.Empty));
            Console.WriteLine("Fixed Attributes   : " + JsonDump(updatedKey.Attributes));
            Console.WriteLine("Mutable Attributes : " + JsonDump(updatedKey.MutableAttributes));

            WaitForInput();
            return 0;
        }
    }
}
