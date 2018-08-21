/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  enrollmentUrl: 'https://dev-enrollment.ionic.com/keyspace/HVzG/register'
}

const main = async () => {

  // initialize agent
  const agent = new window.IonicSdk.ISAgent()
  await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ', error)
  })

  /**********************************************************
   ** SENDER
   **********************************************************/
  const message = 'This is a secret message!'

  // encrypt data
  const encryptResponse = await agent.encryptStringChunkCipher({stringData: message})
  const ciphertext = encryptResponse.stringChunk

  console.log('CIPHERTEXT : ' + ciphertext)

  /**********************************************************
   ** RECEIVER
   **********************************************************/

  // decrypt data
  const decryptResponse = await agent.decryptStringChunkCipher({stringData: ciphertext})
  const plaintext = decryptResponse.stringChunk

  console.log('\nPLAINTEXT  : ' + plaintext)
}

main()
