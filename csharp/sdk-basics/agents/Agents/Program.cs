/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;

using IonicSecurity.SDK;

namespace Samples
{
    class Agents
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


        static void Main(string[] args)
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

            // Create a blank agent.
            Agent agent = new Agent();

            // Create a password persistor and intialize agent.
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
                agent.SetMetadata(Agent.MetaApplicationName, "Ionic Agents Tutorial");
                agent.SetMetadata(Agent.MetaApplicationVersion, "1.0.0");
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error setting the application metadata: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Create a single key.
            CreateKeysResponse.Key key = null;
            try
            {
                key = agent.CreateKey().Keys[0];
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Create key error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Display the newly created key ID.
            Console.WriteLine("CREATED NEW KEY  : " + key.Id);

            WaitForInput();
        }
    }
}
