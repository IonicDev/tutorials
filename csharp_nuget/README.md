## Ionic C# SDK 2.0.0 Tutorials Using NuGet

These C# examples use the C# SDK 2.0.0 along with the NuGet package manager.  This means
that you don't have to download the C# SDK manually.  In this directory, there is one
solution file for all the examples.

All the `.csproj` files point to source files in the appropriate csharp directory.  This implies
that the code is the same for both directories csharp and csharp_nuget.  If you want to modify
code, please modify in the csharp directory.

The C# SDK 2.0.0 is not FIPS 140-2 compliant.  If you want to use a FIPS 140-2 compliant SDK, please
use C# SDK 1.8.0 in the charp directory.
For more information, see [Machina SDK Releases and FIPS 140-2](https://ionic.com/developers/machina-sdk-releases-and-fips-140-2/).

### Requirements
- Visual Studio 2017
- A Password Persistor located at `~/.ionicsecurity/profiles.pw`. The persistor password needs to be provided in the environment variable `IONIC_PERSISTOR_PASSWORD`.

More information:

- [Password persistor](https://dev.ionic.com/getting-started/create-ionic-profile)
- [Persistor password in environment variable](https://dev.ionic.com/getting-started/hello-world)

### Build

In Visual Studio:

- Open the solution file: `IonicTutorials.sln`.
- Set the build/run setting to `Release` or `Debug`.
- Set the architecture setting to be machine specific like `x64` or `x86`, but not `Any CPU`.
-  Click on `Build` -> `Build Solution`.  This builds all the C# examples.

### Execute

In Visual Studio:

- Set the project setting (left of the architecture setting) to the example you want to execute.
- Click on `Debug` -> `Start Debugging` or `Debug` -> `Start Without Debugging`.  The application will run in a popped-up DOS window.

The app can also be executed in a DOS or PowerShell window:

```
<app>\<C# app>\bin\<architecture>\Release\<C# app>.exe
```

For example: `create-key\CreateKey\bin\x64\Release\CreateKey.exe`.

