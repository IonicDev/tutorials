# Machina Javascript Tutorials

To run the Machina JavaScript Tutorials, you need to either:

* create device credentials in Machina's Developer Portal (DevPortal)
* call `ISAgent.enrollUser()` with the correct credentials 

## Create Device Credentials
Before creating device credentials, you will need a Machina tenant.  You can create a Machina tenant from our [DevPortal](https://dev.ionic.com) by clicking on the *"Start For Free*" link. After you have created the device credentials and done the "[Hello, World!](https://dev.ionic.com/getting-started/hello-world)" example, you can run the Javascript tutorials in the DevPortal.  The following Javascript tutorials are available:

* [Agents](https://dev.ionic.com/tutorials/sdk-basics/agents?language=javascript)
* [Keys](https://dev.ionic.com/tutorials/sdk-basics/keys?language=javascript)
* [Ionic Ciphers](https://dev.ionic.com/tutorials/sdk-basics/ionic-ciphers?language=javascript)

***Note:*** Make sure you the Javascript language square is clicked.

## Call agent.enrollUser()
If you already have a Machina tenant you can call [ISAgent.enrollUser()](https://api.ionic.com/jssdk/latest/Docs/ISAgent.html#enrollUser). There is a code example in the Machina Javascript SDK documentation, [hellowWorld_index.js Tutorial](https://dev.ionic.com/sdk_docs/ionic_platform_sdk/javascript/version_2.2.0/tutorial-helloWorld_index.js.html). There is also another code example, `enrollDevice()` in [Ionic Github Samples](https://github.com/IonicDev/samples/tree/master/javascript/enroll-device).

The agent configuration or `appData` needs to be set for your tenant.  Below is an example of `appData` for enrollment used by `ISAgent.enrollUser()`.

```
const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  enrollmentUrl: 'https://enrollment.ionic.com/keyspace/HvzG/idc/6d8d832785f3a66824ae2c23/default/register'
  metadata: {
    'ionic-application-name': 'Javascript Enroll Device',
    'ionic-application-version': '1.3.0'
  }
};
```

## Tips

There is an `index.html` that conveniently points to all the Javascript tutorials.  You can use it with a local webserver, for example, [http-server](https://www.npmjs.com/package/http-server).

There is a Javascript file, `consoleReroute.js` that tees `console.log()` to the Javascript console and to the an output area to be displayed in the browser.  Calling `console.log('') initializes the output area with the a fixed font and grey background.
