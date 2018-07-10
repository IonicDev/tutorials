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
    class Encryption
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
        static String JsonDump(Dictionary<String, String> ad)
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

                agent.SetMetadata(Agent.MetaApplicationName, "Ionic Encryption Tutorial");
                agent.SetMetadata(Agent.MetaApplicationVersion, "1.0.0");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            /*****************************************************************
             * SENDER
             *****************************************************************/

            //The message to encrypt.
            String message = "this is a secret message!";

            // Create single key without attributes.
            CreateKeysResponse.Key createdKey = null;
            try
            {
                createdKey = agent.CreateKey().Keys[0];
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Key creation error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Initialize sender AES CTR cipher object.
            AesCtrCipher senderAes = new AesCtrCipher();
            byte[] senderKeyBytes = createdKey.KeyBytes;
            senderAes.KeyBytes = senderKeyBytes;

            // Encrypt
            byte[] cipherText = new byte[256];

            try
            {
                senderAes.Encrypt(message, ref cipherText);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Encryption error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Create a string payload to send to the receiver.
            String b64CipherText = Convert.ToBase64String(cipherText);
            Dictionary<String, String> payload = new Dictionary<String, String>
                { ["key_id"] = createdKey.Id, 
                  ["b64_ciphertext"] = b64CipherText };

            Console.WriteLine("CREATED KEYID : " + createdKey.Id);
            Console.WriteLine("CIPHERTEXT    : {0}", BitConverter.ToString(cipherText).Replace("-", String.Empty));

            Console.WriteLine("\nPAYLOAD       : " + JsonDump(payload));

            /*****************************************************************
             * RECEIVER
             *****************************************************************/

            // Imagine that this reciever recieved a 'payload'.
            String payloadKeyId = payload["key_id"];

            // Fetch the key from the payload.
            GetKeysResponse.Key fetchedKey = null;
            try
            {
                fetchedKey = agent.GetKey(payloadKeyId).Keys[0];
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error fetching payload key {0}: {1}", payloadKeyId, sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Initialize receiver AES CTR cipher object.
            AesCtrCipher receiverAes = new AesCtrCipher();
            byte[] keyBytes = fetchedKey.KeyBytes;
            receiverAes.KeyBytes = keyBytes;

            // Decrypt
            string plainText = null;
            try
            {
                receiverAes.Decrypt(cipherText, ref plainText);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Decryption error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            Console.WriteLine("\nFETCHED KEYID : " + fetchedKey.Id);
            Console.WriteLine("PLAINTEXT     : {0}", plainText);

            WaitForInput();
            return 0;
        }
    }
}

