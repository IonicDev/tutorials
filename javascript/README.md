# Machina JavaScript Tutorials

You will need a Machina instance to execute these tutorials.  You can create a Machina instance from our [Machina Developers Portal](https://dev.ionic.com) by clicking on the *"Start For Free*" link. You will receive an e-mail which contains your keyspace, tenant ID, and user name (e-mail).  Please save these for future use.

If you already have a Machina instance, you will need the keyspace, user name (e-mail),
and password.  These can be found in the original welcome e-mail or from your Machina instance
administrator.

All the tutorials can be executed in the browser from [Machina Developers Portal](https://dev.ionic.com/tutorials/sdk-basics/profiles?language=javascript) or in a local web server.

## Executing Tutorials in Local Web Server

To run the Machina JavaScript tutorials in a local web server, you will need to:

**Step 1**: Clone Ionic tutorials repo from Github.

~~~bash
git clone https://github.com/IonicDev/tutorials
cd tutorials/javascript/
~~~

**Step 2**: Start a local webserver (example: [http-server](https://www.npmjs.com/package/http-server)) in the `samples/javascript` directory.

~~~bash
node http-server
~~~

## Create Device Credentials

After the http server has been started, use a Chrome or Firefox browser.

1. Go to URL: `http: 127.0.0.1:8080`. This should bring up an index page.
2. Under the "*Machina JavaScript Create Device Credentials*" heading, click:

* Enroll Device

You will need your Machina Console e-mail address and password to complete the enrollment form.

Enroll-device will only create device credentials. You can read more about enrollment in the [JavaScript documentation](https://dev.ionic.com/sdk_docs/ionic_platform_sdk/javascript/latest/index.html). 

**Note:** Failure to find any profiles may indicate that the device credentials were not created; or that the specified parameters aren't a match to those used during enrollment.

* [Profiles](https://dev.ionic.com/tutorials/sdk-basics/profiles?language=javascript)
* [Agents](https://dev.ionic.com/tutorials/sdk-basics/agents?language=javascript)
* [Keys](https://dev.ionic.com/tutorials/sdk-basics/keys?language=javascript)
* [Ionic Ciphers](https://dev.ionic.com/tutorials/sdk-basics/ionic-ciphers?language=javascript)

***Note:*** Make sure the JavaScript language square is clicked.

## Tips

There is a JavaScript file, `consoleReroute.js` that tees `console.log()` to the JavaScript console and to the an output area to be displayed in the browser.  Calling `console.log('') initializes the output area with the a fixed font and grey background.

## Documentation
The JavaScript SDK documentation is located [here](https://dev.ionic.com/sdk_docs/ionic_platform_sdk/javascript/latest/index.html).
