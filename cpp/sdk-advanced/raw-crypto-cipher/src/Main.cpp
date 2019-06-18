#include <ISAgent.h>
#include "RawCryptoCipher.h"


int main()
{
	ISAgent agent;
	agent.initialize();
	RawCryptoCipher cipher(agent);

	ISCryptoBytes input;
	// Generate some arbitrary plaintext
	for (int i = 0; i <= 255; i++)
	{
		input.push_back(i);
	}

	ISCryptoBytes cipherText;
	RawCryptoEncryptAttributes encryptAttributes;
	std::string keyId;

	int nErr = cipher.encrypt(input, cipherText, encryptAttributes);
	if (nErr != RawCryptoCipher::OK)
	{
		// Report back an error to the user
		return nErr;
	}

	// The keyId is necessary to unencrypt the data, but is not considered
	//  to be sensitive data and should be kept alongside the ciphertext.
	//  ISFileCryptoCipher and ISChunkCryptoCipher establish formats that
	//  include the key ID so that the encrypted file or chunk of data can be
	//  sent in place of what would normally be unencrypted data
	keyId = encryptAttributes.getKeyIdOut();

	ISCryptoBytes recoveredData;
	RawCryptoDecryptAttributes decryptAttributes;

	nErr = cipher.decrypt(cipherText, keyId, recoveredData, decryptAttributes);
	if (nErr != RawCryptoCipher::OK)
	{
		// Report back an error to the user
		return -1;
	}

	// Check that the recovered data matches the input
	for (int i = 0; i < input.size(); i++)
	{
		if (recoveredData[i] != input[i])
		{
			return -1;
		}
	}

	// Success!
    return 0;
}

