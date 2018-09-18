# Ionic Java Tutorials

To build and run an Ionic Java Tutorial, navigate into the directory of a specific sample task and follow the steps below depending on your platform.
The Ionic Java SDK is used in all the tutorials.  Go to [Ionic Developer SDK Setup](https://dev.ionic.com/getting-started/sdk-setup) then click on the Java icon.
Note: the sample apps expect a Password Persistor located at `~/.ionicsecurity/profiles.pw`. The password needs to be provided as an environment variable.

```bash
export IONIC_PERSISTOR_PASSWORD=password123
```

**Requirements**:
- Maven
- Java 8

**Build:**
```
mvn package
```

**Run:**
```
java -jar /target/<tutorial>.jar
```
  
