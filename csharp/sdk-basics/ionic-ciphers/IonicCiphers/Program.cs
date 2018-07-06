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
    class IonicCiphers
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

                agent.SetMetadata(Agent.MetaApplicationName, "IonicCiphers Tutorial");
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

            // The message to encrypt.
            String message = "this is a secret message!";

            // Initialize chunk sender cipher object.
            ChunkCipherAuto SenderCipher = new ChunkCipherAuto(agent);

            // Encrypt data.
            string cipherText = null;
            try
            {
                SenderCipher.Encrypt(message, ref cipherText);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Chunk sender cipher encrypt error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            Console.WriteLine("CIPHERTEXT : {0}", cipherText);

            /*****************************************************************
             * RECEIVER
             *****************************************************************/

            // Initialize chunk receiver cipher object.
            ChunkCipherAuto recieverCipher = new ChunkCipherAuto(agent);

            // Decrypt data.
            string plainText = null;
            try
            {
                recieverCipher.Decrypt(cipherText, ref plainText);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Chunk receiver cipher decrypt error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            Console.WriteLine("\nPLAINTEXT  : {0}", plainText);

            WaitForInput();
            return 0;
        }
    }
}

