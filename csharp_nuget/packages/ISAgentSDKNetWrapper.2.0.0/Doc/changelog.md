# Ionic Internal Client SDK Changelog for .NET on Windows
Generated on 2020-07-02 12:23:26 EDT.

This project follows [semantic versioning] (http://semver.org/).

<br/>

## Version 2.0.0

#### Release
- Release

#### Added
- C#/.NET - publish SDK package to NuGet.org package manager
- Make SDK device fingerprinting optional
- C#/.NET - Expose cryptoutils utility functions
- DNS lookups for Keyspace Name Service (preparatory)
- HTML metatag - doxygen post processor
- SDK wrappers - HTML metatag - doxygen post processor
- Add an interface to modify Portion Marking classifications
- ISCrossPlatform could use an isDirectory function
- add 'setFingerprint()' API to Agent; user supplies fingerprint

#### Changed
- Allow Portion Marked files to be edited even without permissions
- Update Boost library to latest
- Update Catch library to latest
- Update Curl library to latest
- Update JSON Spirit library to latest
- Update libicu to latest
- Update libxml2 to latest
- Update zLib / libzip to latest
- ensure all SDK build nodes are on doxygen 1.8.18
- Update cryptopp to latest
- Core - update all SDK endpoints to v2.4
- IV for Core SDK embeds a global counter

#### Fixed
- C++ - Update invalid reference to ISCRYPTO_RSA_SEED_OVERFLOW
- Allow no AAD for AES GCM (fips changes)
- Fix portion marking color being sent as a classification
- SecretShare unexpected Encryption fail with bad JSON path

## Version 1.8.0

#### Release
- Release

#### Added
- Documentation Nice-to-Haves for Omissions/Parity
- Add documentation for Windows-specific DllMain usage limitation
- Verify agent.clone() implemented in wrappers
- new Agent constructor passing 1 String argument (SEP JSON)
- KNS - Lookup by domain provided by caller
- KNS - Add method to change active profiles server URL
- Doc - Sample to show KNS/HTTP use cases
- core SDK wrappers - new Agent constructor passing 1 String argument (SEP JSON)

#### Changed
- Move buffer-safe functions like is_strcpy (ISCryptoDLL.cpp) to common module
- Remove deprecated code from 'C' lib and have the 'C' lib link directly to the 'C++' lib instead of ISAgentSDKInternal
- Core/Wrappers: Update Log Config Example docs to be language-specific
- internal refactoring in anticipation of alternate KeyServices implementation
- IonicException improvements

#### Fixed
- Make CSV ignore minor version number changes
- Bring consistency to ISFileCryptoCiphers with respect to encrypting already encrypted file/buffers
- Core: ISLogConfig 'maxAgeSeconds' field name is too limited
- Error Code 10004
- Windows HTTP library fails on non-utf8 (really non-ascii) get/post
- C#/.NET: Documentation Issues
- Enrollment on a new machine fails when the proper directories do not exist
- ionicsdk does not handle SEPs with trailing slash
- C++: Persistor/KeyVault Locking is leaving stale .lock files
- ISAgent.getKeyspace() shouldn't require an active (or any) profile
- C#/.NET: Example corrections in 'AgentKeyServices Implementors Overview'
- CPP: intermittent segfault when server returns 502
- CPP: retcode 32766 on getDecryptStream on Mac
- problems in doc for GetKeyspaceResponse
- Update boost library consumed by SDK and published with SDKInternal to one that is tested compatible with VS2015
- create crypto function to check available entropy
- Generic v1.3 ability to truncate a file
- Add Keepalive option to curl implementation of ISHTTP
- Update copyright block in SDK
- More optimization work needed for Generic v1.3 fstream mode (read/write)
- Improve handling of entropy exhaustion
- Doc - Need to specify default cipher for chunkcipher
- C#/.NET: Nomenclature Correction in Overviews
- Python Doxygen files parity with other language wrappers

#### Removed
- Remove ISCryptoSecureContainer class and ISCryptoScopedSecureClearer

## Version 1.7.2

#### Added
- New SEO header HTML in the Core SDK wrapper libraries

#### Fixed
- Doc footer copyright year
- Inconsistent Meta Description SEO values for .NET and Python

## Version 1.7.0

#### Added
- ISFileCryptoCipherOpenXML interface for cover page marking
- Custom Encrypted File test for C++ Key Vaults
- Max Age Limit - ISO 8601 duration - ISLog JSON configuration
- Random seek encrypt/decrypt in Generic 1.3 file cipher
- External signature for SDK library file hash
- ISFileCryptoCipherPdf interface for cover page marking

#### Changed
- Limit size/age of SDK log file set
- ISLog help updated with options for controlling file space and age
- Update ISLog documentation JSON examples and tables for latest features

#### Fixed
- Expose ProfilePersistor v1.1 to wrapped SDKs
- C# - KeyVaultKey doesn't support mutableAttributes
- PDF cipher error on GetFileInfo when the PDF file has external PDF Encryption
- C++ - Error logged - stream not reset on getFileInfo clear XLSX input
- OpenXML file encryption should set fail bit on output stream when a temp RAM buffer fills
- Generic v1.3 - Error when using the read/write IO interface and reading past an encryption block
- ISKeyVaultBase logic error
- FileCryptoCipherOpenXml to write temp files into platform temp directory
- CSV decryption fails on a certain size file
- Generic v1.3 - preserve write timestamp on file read
- Generic v1.3 - ability to force flush to file

## Version 1.6.1

## Version 1.6.0

#### Added
- SDK Key Services support automatic SEP lookup for non-active keyspace
- Added TLS error codes on requests that fail due to TLS errors
- SDK Documentation update for 1000 key request limit
- SDK Log callback mechanism added
- Added method to detect and destroy corrupted key vaults
- New Persistor format containing optional SALT
- New create key simulation mode
- Streamable Generic Cipher
- Streamable CSV Cipher
- New Generic Stream Cipher version 1_3
- Streamable PDF Cipher
- Streamable OpenXML Cipher
- Default Windows persistor Roaming support
- Migrate custom doc properties in OpenXML

#### Fixed
- SDK getFileInfo consumes memory
- Error when requesting multiple updates on same key id

## Version 1.5.0

#### Added
- Documentation updates across all sdk languages.
- Added DeviceProfilePersistorWindows to NET wrapper SDK

#### Changed
- Modified ISCryptoBytes to derive from new SecureAllocator class to fix bugs in heap management

#### Fixed
- Api calls now reset stream pointers on error
- SDK generated temporary files no longer use tilde due to incompatibility with network based storage
- Missing Access Denied cover pages for CSV

## Version 1.4.0

#### Added
- Provide an indication of SDK version with all communications between SDK and Ionic.com.
- SecretShare Persistor in NET.

## Version 1.3.0

#### Added
- Added mutable key attribute support to KeyServices APIs.
- Added external-id support to the KeyServices APIs.
- Updated NET runtime to 4.X for VS2015 support.

#### Fixed
- Fixed issue with using wide characters or non-ascii as key attribute values.
- Fixed issue with encrypting large file sizes now return proper value and log an error.

## Version 1.2.1

#### Fixed
- Fixed an issue in which AutoFileCipher would not apply custom cover pages defined via ISFileCryptoCoverPageServices.

## Version 1.2.0

#### Added
- Introduced the ability to override the default Cover Pages used by the FileCrypto classes through ISFileCryptoCoverPageServices.
- The SDK now supports non-persistent  VDI.  Previously, the default SEP storage mechanism did not function in these environments.

#### Fixed
- An issue with parsing encrypted PDFs which have had their XMP attributes modified has been fixed.

## Version 1.0.0

#### Added
- A BETA Ionic SDK for Python is now available.
- Added a new method Agent::getDeviceProfileForKeyId().  If using multiple device profiles, this routine is helpful for determing the appropriate profile for a given key ID.

#### Changed
- The Ionic SDK has been incremented to Version 1.0.0!
- The SDK now communicates with v2.3 of the Ionic key creation and retrieval server APIs.  This update includes minor security improvements and causes no functional changes to SDK interfaces.
- The default key size for Agent::CreateDevice() has been updated to 3072 in line with FIPS compliance of crypto functionality.

#### Fixed
- Fixed an issue in which handling PDFs which contain certain special characters cause an exception to be thrown unexpectedly.  These problematic PDFs are now parsed correctly.

## Version 0.5.0

#### Added
- Added a new Ionic data standard for encrypting e-mail using CMS for use with S/MIME.

## Version 0.4.0

#### Added
- Added a simplified method LogFactory::CreateSimple() to enable logging.

#### Deprecated
- The ability to initialize an Agent object from a configuration file has been deprecated.  The Agent::InitializeFromFile() routines will be removed in a future release of the SDK.

#### Fixed
- Fixed an issue encrypting files in-place on Windows network drives when using a UNC path.

## Version 0.3.1

## Version 0.3.0

#### Changed
- Updated all OpenXML and PDF cover pages for aesthetics and usability.

## Version 0.2.0

#### Changed
- Updated all OpenXML cover pages for aesthetics and usability.

## Version 0.1.1

#### Changed
- Updated PDF file cipher to support additional PDF file formats.

#### Fixed
- Stopped exporting private internal library symbols, which could cause crashing at runtime when conflicting with the same symbols in an application using the SDK.
- Fixed memory leak in HTTP transport layer.

## Version 0.1.0

#### Added
- First official version of the Ionic SDK!
