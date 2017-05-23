# Android App: the proxy of a Flic button

Read the blog [Flic and ARTIK Cloud place IoT at your fingertips](https://www.artik.io/blog/2017/05/flic-button-artik-cloud/) to learn what this app does and how is it implemented.

## Requirements
- [A Flic button connected to the Flic app](https://flic.io/)
- Android Studio
- [ARTIK Cloud Java SDK](https://github.com/artikcloud/artikcloud-java)
- [Flic For Android SDK](https://github.com/50ButtonsEach/fliclib-android)

## Setup / Installation

### Setup at ARTIK Cloud

Follow [these instructions](https://developer.artik.cloud/documentation/tools/web-tools.html#creating-an-application) to create an application. For this Android app, select the following:

 - Under “AUTHORIZATION METHODS”, check “Client credentials, auth code, implicit”.
 - Set “Redirect URL” to cloud.artik.example.flicproxy://oauth2callback .
 - Under “PERMISSIONS”, check “Read” for “Profile”.
Click the “Add Device Type” button. Choose “Example Flic Button” (unique name: cloud.artik.example.flicbutton) as the device type. Check “Read” and “Write” permissions for this device type.

Get the [client ID](https://developer.artik.cloud/documentation/tools/web-tools.html#how-to-find-your-application-id), which you will need later.

### Setup at Flic

Go to [Flic Developer site](https://partners.flic.io/partners/developers/credentials) to create an app. Get the key and secret, which you will need later.

### Setup Android project

 1. Clone this sample repository.
 2. Under the root of the source, clone [Flic Android SDK repository](https://github.com/50ButtonsEach/fliclib-android). This will create directory "fliclib-android" under the root.
 2. In Android Studio, import the sample app Android project. 
  3. Update Config.java file. Replace the placeholders (starting with "YOUR_*****") with your own client ID from ARTIK Cloud and your own credentials from Flic. You should have obtained them in the previous setup steps.

Now build the project, which will download the ARTIK Cloud SDK JAR from [Maven Central Repository](http://search.maven.org/). Deploy the APK to an Android phone.

## More about ARTIK Cloud

If you are not familiar with ARTIK Cloud, we have extensive documentation at https://developer.artik.cloud/documentation

The full ARTIK Cloud API specification can be found at https://developer.artik.cloud/documentation/api-spec

Check out advanced sample applications at https://developer.artik.cloud/documentation/samples/

To create and manage your services and devices on ARTIK Cloud, create an account at https://developer.artik.cloud

Also see the ARTIK Cloud blog for tutorials, updates, and more: http://artik.io/blog/cloud

## License and Copyright

Licensed under the Apache License. See [LICENSE](LICENSE).

Copyright (c) 2017 Samsung Electronics Co., Ltd.
