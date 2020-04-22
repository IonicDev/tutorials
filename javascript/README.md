# Machina Javascript Tutorials

To run the Machina JavaScript Tutorials, you need to either:

1. create device credentials by going through [Getting Started](https://dev.ionic.com/getting-started) on Machina Developers Portal
2. call `ISAgent.enrollUser()` with the correct credentials 

## Create Device Credentials
Before creating device credentials, you will need a Machina instance.  You can create a Machina instance from our [Machina Developers Portal](https://dev.ionic.com) by clicking on the *"Start For Free*" link. After you have created the device credentials and completedinstance the "[Hello, World!](https://dev.ionic.com/getting-started/hello-world)" example, you can run the Javascript tutorials in the Machina Developers Portal.  The following Javascript tutorials are currently available:

* [Agents](https://dev.ionic.com/tutorials/sdk-basics/agents?language=javascript)
* [Keys](https://dev.ionic.com/tutorials/sdk-basics/keys?language=javascript)
* [Ionic Ciphers](https://dev.ionic.com/tutorials/sdk-basics/ionic-ciphers?language=javascript)

***Note:*** Make sure the Javascript language square is clicked.

## Call agent.enrollUser()
If you already have a Machina instance you can call [ISAgent.enrollUser()](https://api.ionic.com/jssdk/latest/Docs/ISAgent.html#enrollUser). There is a code example, `enrollDevice()` in [Ionic Github Samples](https://github.com/IonicDev/samples/tree/master/javascript/enroll-device).
There is another code example in the Javascript SDK documentation, [hellowWorld_index.js Tutorial](https://api.ionic.com/jssdk/latest/Docs/tutorial-helloWorld_index.js.html). 
The agent configuration or `appData` needs to be set for your instance.  Below is an example of `appData` for enrollment used by `ISAgent.enrollUser()`.

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

There is an [`index.html`](https://github.com/IonicDev/tutorials/blob/master/javascript/index.html) that conveniently points to all the Javascript tutorials.  You can use it with a local webserver, for example, [http-server](https://www.npmjs.com/package/http-server).

There is a Javascript file, `consoleReroute.js` that tees `console.log()` to the Javascript console and to the an output area to be displayed in the browser.  Calling `console.log('') initializes the output area with the a fixed font and grey background.

## Documentation
The Javascript SDK documentation is located [here](https://api.ionic.com/jssdk/latest/Docs/index.html).
