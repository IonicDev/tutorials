/*
 * (c) 2018-2020-2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using IonicSecurity.SDK;


namespace Samples
{
    class IonicHelloworld
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
            try
            {
                // Create an agent object to talk to Ionic.
                Agent agent = new Agent();
                agent.SetMetadata(Agent.MetaApplicationName, "DefaultPersistor Sample");
                agent.Initialize();
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Setup the Chunk Crypto object.
            ChunkCipherAuto chunkCrypto = new ChunkCipherAuto(agent);

            string clearText = "Hello, World!";
            string encryptedText = null;

            // Define data markings
            AttributesDictionary dataMarkings = new AttributesDictionary();
            dataMarkings.Add("clearance-level", new List<string> { "secret" });

            // Encrypt the string using an Ionic-managed key.
            chunkCrypto.Encrypt(clearText, ref encryptedText, dataMarkings);

            string decryptedText = null;

            // Note: Decryption only works if the policy allows it.
            chunkCrypto.Decrypt(encryptedText, decryptedText);

            Console.WriteLine("Plain Text: {0}", clearText);
            Console.WriteLine("Ionic Chunk Encrypted Text: {0}", encryptedText);
            Console.WriteLine("Decrypted text: {0}", decryptedText);

            WaitForInput();
        }
    }
}
