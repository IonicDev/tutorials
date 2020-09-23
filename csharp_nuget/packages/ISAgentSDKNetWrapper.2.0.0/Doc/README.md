# Ionic SDK Release 2.0.0

## Introduction
Welcome to the 2.0 release of the Ionic SDK.  This is a major version update.

Details are summarized below.

## New Features/Improvements

### Device Fingerprinting Adjustments
- The SDK implementation of device fingerprinting is now optional, and is disabled by default.
- SDK users may now provide a custom fingerprint to the SDK, which will be used during service communications.

### Update Accesses to Machina Service Endpoints
All SDK accesses of Machina services (HTTPS) have been updated to utilize the Machina _v2.4_ service API endpoints.

### Portion Marking Enhancements
Additional SDK APIs have been implemented and exposed to manage the "Portion Marking" SDK feature.

### AesGcmCipher Fixes
The AES GCM cipher and the associated ProfilePersistor now allow the use of an empty AAD value.

### C# / .NET Binaries Published to NuGet
The Machina Tools SDK wrapper for the .NET language is now published to the [NuGet](https://www.nuget.org/) package  
manager.  (As part of this update, the filenames of the distributable library now incorporate an additional bit-ness  
component.  DLLs were renamed with ".x86" and ".x64".  Old "Win32" directory was also renamed to "x86".)

When initially installing the The Machina Tools SDK wrapper for the .NET language NuGet Package (NuPkg) with
Visual Studio (VS), the corresponding .Net references are automatically added to the VS project file via a project
property file (props file).  But to actually load the props file into the active VS project configuration, the 
Visual Studio project file must be reloaded, and this does not automatically happen during the NuPkg installation 
procedure.  After the initial NuPkg installation procedure is completed, either reload the VS Project File from disk, 
or restart VS to see the NuPkg references displayed correctly in the VS Project's Reference folder.  Reloading
the project file only needs be done once after initial NuPkg installation.

### Documentation Improvements
The doxygen-generated documentation for the SDK and wrappers has been augmented with context-relevant keywords.  The version
of the doxygen tool has been updated.

### Library Dependency Updates
Most library dependencies have been updated to current versions:

| Library | Website | From Version | To Version | Function |
|---------|---------|--------------|------------|----------|
| boost | [boost.org](https://www.boost.org/) |  1.71.0 | 1.72.0 | utilities |
| catch | [github.com](https://github.com/catchorg/Catch2) | 1.0 | 2.11.1 | unit test |
| curl | [curl.haxx.se](https://curl.haxx.se/) | 7.29.0 | 7.67.0 | https |
| json spirit | [codeproject.com](https://www.codeproject.com/Articles/20027/JSON-Spirit-A-C-JSON-Parser-Generator-Implemented) | 4.05 | 4.08 | json parsing |
| libicu | [icu-project.org](http://site.icu-project.org/) | ICU 58.2 | ICU 65 | internationalization |
| libxml2 / XML Soft | [xmlsoft.org](http://xmlsoft.org/) | 2.9.2 | 2.9.7 | xml parsing |
| zLib and libzip | [zlib.net](https://www.zlib.net/) | 1.2.8 | 1.2.11 | data compression |
| cryptopp | [cryptopp.com](https://www.cryptopp.com/) | 5.6.2 | 8.2.0 | cryptography |

## Issues Addressed
- An issue was addressed with the underlying implementation of the SecretSharePersistor.
- The global counter implemented in the IV of AesGcmCipher has been removed.

## Discontinued Support
- The Windows 7 operating system is no longer supported.

## Additional Notes

### LIBXML Linker Conflict
A potential linker conflict exists while using the C++ Linux SDK library (libISAgentSDK.a) if the user implements
code using their own copy of libxml2 version 2.9.x (libxml2.a). If this occurs, the linker will report multiple
definitions of several symbols.

## Additional Notes
- Additional CryptoUtils utility functions have been exposed for use with the .NET SDK wrapper library.

## Supported Platforms
The Ionic SDK is tested against the following platform configurations:

|Platform    |Version                     |
|------------|----------------------------|
|Linux       | CentOS 7.8-2003            |
|Linux       | Ubuntu 18.04               |
|Windows     | Windows 8.1 (32 and 64 bit)|
|Windows     | Windows 10 (32 and 64 bit) |
|macOS       | macOS 13 (High Sierra)     |
|macOS       | macOS 14 (Mojave)          |
|macOS       | macOS 15 (Catalina)        |
|Python      | 2.7.x                      |
|Python      | 3.5.x                      |
