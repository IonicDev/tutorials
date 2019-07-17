/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;

using IonicSecurity.SDK;

namespace Samples
{
    class Profiles
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
            // Set the persistors's path and password.
            // Information can be found at '../../../../sample-data/peristors/README.md',
            // or 'github-tutorials/sample-data/peristors/README.md'.
            //
            // The persistor path directory is 7 deep because the Visual Studio solution
            // executes from 'github-tutorials/csharp/sdk-basics/profiles/Profiles/bin/x64/Debug'.
            String persistorPath = "../../../../../../../sample-data/persistors/sample-persistor.pw";
            String persistorPassword = "ionic123";

            // Create a blank agent.
            Agent agent = new Agent();

            // Create a password persistor and intialize agent.
            try
            {
                DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword();
                persistor.FilePath = persistorPath;
                persistor.Password = persistorPassword;
 
                agent.SetMetadata(Agent.MetaApplicationName, "Ionic Profiles Tutorial");
                agent.SetMetadata(Agent.MetaApplicationVersion, "1.0.0");

                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Get the profiles and check if the are any.
            List<DeviceProfile> profiles = agent.AllProfiles;
            if (! agent.HasAnyProfiles)
            {
                Console.WriteLine("No profiles for password persistor.");
                WaitForInput();
                return;
            }

            // Display profile information.
            Console.WriteLine("ALL PROFILES:");
            foreach (DeviceProfile profile in profiles)
            {
                Console.WriteLine("-----");
                Console.WriteLine("ID       : " + profile.DeviceId);
                Console.WriteLine("Name     : " + profile.Name);
                Console.WriteLine("Keyspace : " + profile.KeySpace);
                Console.WriteLine("API URL  : " + profile.Server);
            }

            // Verify an active profile exists.
            if (! agent.HasActiveProfile)
            {
                Console.WriteLine("No profile is set as active.");
                WaitForInput();
                Environment.Exit(1);
            }

            // Display the active profile device ID.
            DeviceProfile activeProfilesile = agent.ActiveProfile;
            Console.WriteLine("\nACTIVE PROFILE: " + activeProfilesile.DeviceId);
            
            // Change the active profile.
            String newProfilesileId = "EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73";
            Console.WriteLine("\nSETTING NEW ACTIVE PROFILE: " + newProfilesileId);
            bool success = agent.SetActiveProfileById(newProfilesileId);
            if (! success)
            {
                Console.WriteLine("Failed to set active profile to: " + newProfilesileId);
                WaitForInput();
                Environment.Exit(1);
            }

            // Display the active device ID.
            DeviceProfile newActiveProfilesile = agent.ActiveProfile;
            Console.WriteLine("\nNEW ACTIVE PROFILE: " + newActiveProfilesile.DeviceId);

            WaitForInput();
        }
    }
}
