// This code sample demonstrates an Ionic SDK bulk file encryption use case.
// Multiple files may be specified by using a wildcard GLOB.
// The Ionic SDK must be configured to be available to the compiler.
#include <ISAgent.h>
#include <ISCrossPlatformFileUtil.h>
#include "BulkEncrypt.h"
#include <ISAgentSDKError.h>

#if defined(_WIN32) || defined(_WIN64)
#include "io.h"
#else
#include <glob.h>
#endif

#include <iostream>

int main(int argc, const char* argv[])
{
	int nErr;
	bool encryptMode = true;
	bool encryptModeSet = false;
	bool hasFileInputGlob = false;
	bool hasOutputDir = false;
	bool hasPersistor = false;
	std::string fileglob;
	std::string outputDir;
	std::string password;
	std::string persistorFile;
	std::vector<std::string> filenames;

	// There are many better tools for handling command lines arguments that would be preferred
	// We're doing this by hand just to reduce dependencies for demonstration purposes
	std::string usage = "Usage: bulkencrypt -e|-d [-p password persistorFile] [-l [N] logfile ] file outputdir \n";
	usage += "    Options:\n";
	usage += "        -e    Encrypt all matching files to output directory\n";
	usage += "        -d    Decrypt all matching files to output directory\n";
	usage += "        -p    Load a password protected persistor file (Optional on platforms with a default persistor)\n";
	usage += "        -l    Write out log file (At severy level N, 0 = trace, 5 = fatal, default is 4 = error)\n";

	if (argc < 4)
	{
		std::cout << "Error: More arguments are required" << std::endl;
		std::cout << usage << std::endl;
		return BulkEncrypt::INPUT_ERROR;
	}

	int curarg = 1;
	while (argc - curarg > 0)
	{
		if (strcmp(argv[curarg], "-e") == 0)
		{
			if (encryptModeSet)
			{
				std::cout << "Error: Encrypt/Decrypt mode already set" << std::endl;
				std::cout << usage << std::endl;
				return BulkEncrypt::INPUT_ERROR;
			}
			encryptModeSet = true;
			encryptMode = true;
			curarg++;
			continue;
		}
		if (strcmp(argv[curarg], "-d") == 0)
		{
			if (encryptModeSet)
			{
				std::cout << "Error: Encrypt/Decrypt mode already set" << std::endl;
				std::cout << usage << std::endl;
				return BulkEncrypt::INPUT_ERROR;
			}
			encryptModeSet = true;
			encryptMode = false;
			curarg++;
			continue;
		}
		if (strcmp(argv[curarg], "-p") == 0)
		{
			if (argc - curarg <= 2)
			{
				std::cout << "Error: missing persistor arguments" << std::endl;
				std::cout << usage << std::endl;
				return BulkEncrypt::INPUT_ERROR;
			}
			hasPersistor = true;
			password = argv[curarg + 1];
			persistorFile = argv[curarg + 2];
			curarg += 3;
			continue;
		}
		if (strcmp(argv[curarg], "-l") == 0)
		{
			if (argc - curarg <= 1)
			{
				std::cout << "Error: missing log file name argument" << std::endl;
				std::cout << usage << std::endl;
				return BulkEncrypt::INPUT_ERROR;
			}

			std::string logArgument = argv[curarg + 1];

			int logSeverity = SEV_ERROR;
			char* pNonInt = 0;
			int parsedInt = strtol(logArgument.c_str(), &pNonInt, 10);
			if(*pNonInt == 0) // pointing to end of string null terminator
			{	// no non-integer characters found in argument, treat it as a severity
				logSeverity = parsedInt;
				// Get the next argument for the filename, don't forget to check for errors
				curarg++;
				if (argc - curarg <= 1)
				{
					std::cout << "Error: missing log file name argument" << std::endl;
					std::cout << usage << std::endl;
					return BulkEncrypt::INPUT_ERROR;
				}
				if (logSeverity < SEV_TRACE || logSeverity > SEV_FATAL)
				{
					std::cout << "Error: invalid log severity value, must be between " << SEV_TRACE << " and " << SEV_FATAL << std::endl;
					std::cout << usage << std::endl;
					return BulkEncrypt::INPUT_ERROR;
				}
				logArgument = argv[curarg + 1];
			}

			ISLogBase * pLogger = ISLogFactory::getInstance().createSimple(logArgument, false, (ISLogSeverity)logSeverity);
			ISLog::setSingleton(pLogger);
			curarg += 2;
			continue;
		}
		if (argv[curarg][0] == '-')
		{
			std::cout << "Error: invalid argument" << std::endl;
			std::cout << usage << std::endl;
			return BulkEncrypt::INPUT_ERROR;
		}
		if (!hasFileInputGlob)
		{
			hasFileInputGlob = true;
			fileglob = argv[curarg];
			curarg++;
			continue;
		}
		if (!hasOutputDir)
		{
			hasOutputDir = true;
			outputDir = argv[curarg];
			curarg++;
			continue;
		}

		std::cout << "Error: too many arguments" << std::endl;
		std::cout << usage << std::endl;
		return BulkEncrypt::INPUT_ERROR;
	}
	if (!encryptModeSet)
	{
		std::cout << "Error: No encryption/decryption mode set" << std::endl;
		std::cout << usage << std::endl;
		return BulkEncrypt::INPUT_ERROR;
	}
	if (!hasFileInputGlob || !hasOutputDir)
	{
		std::cout << "Error: Not enough arguments" << std::endl;
		std::cout << usage << std::endl;
		return BulkEncrypt::INPUT_ERROR;
	}

#if defined(_WIN32) || defined(_WIN64)
	intptr_t file;
	_finddata_t filedata;
	file = _findfirst(fileglob.c_str(), &filedata);
	if (file != 1)
	{
		std::string globpath = ISFileUtil::getParentPath(fileglob);
		do
		{
			filenames.push_back(ISFileUtil::joinPaths(globpath,filedata.name));
		} while (_findnext(file, &filedata) == 0);
	}
	_findclose(file);
#else
	glob_t glob_result;
	memset(&glob_result, 0, sizeof(glob_result));
	nErr = glob(fileglob.c_str(), GLOB_TILDE, NULL, &glob_result);
	if (nErr == 0)
	{
		for (size_t i = 0; i < glob_result.gl_pathc; ++i) {
			filenames.push_back(string(glob_result.gl_pathv[i]));
		}
	}
	globfree(&glob_result);
#endif

	if (filenames.empty())
	{
		std::cout << "No matching filenames found, please specify a valid file or wildcard" << std::endl;
		return BulkEncrypt::INPUT_ERROR;
	}

	if (!ISFileUtil::createDirectory(outputDir))
	{
		std::cout << "Failed to create output directory" << std::endl;
		return BulkEncrypt::INPUT_ERROR;
	}

	ISAgent agent;
	if (hasPersistor)
	{
		ISAgentDeviceProfilePersistorPassword passwordPersistor;
		nErr = passwordPersistor.setPassword(password);
		if (nErr != ISAGENT_OK)
		{
			std::cout << "Persistor password too short" << std::endl;
			return nErr;
		}
		passwordPersistor.setFilePath(persistorFile);

		nErr = agent.initialize(passwordPersistor);
		if (nErr != ISAGENT_OK)
		{
			std::cout << "Failed to initialize agent with persistor" << std::endl;
			std::cout << "Error message: " << ISAgentSDKError::getErrorCodeString(nErr).c_str() << std::endl;
			return nErr;
		}
	}
	else
	{
		nErr = agent.initialize();
		if (nErr != ISAGENT_OK)
		{
			std::cout << "Failed to initialize agent with default platform persistor" << std::endl;
			std::cout << "Error message: " << ISAgentSDKError::getErrorCodeString(nErr).c_str() << std::endl;
			return nErr;
		}
	}

	BulkEncrypt bulk(agent);
	if (encryptMode)
	{
		nErr = bulk.encrypt(filenames, outputDir);
	}
	else
	{
		nErr = bulk.decrypt(filenames, outputDir);
	}

	return nErr;
}

