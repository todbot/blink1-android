# blink1-android


This is a driver library for communication [blink(1) USB RGB LED notification lights](https://blink1.thingm.com/), using the [Android USB Host Mode (OTG)](http://developer.android.com/guide/topics/connectivity/usb/host.html) available since Android 3.1 and working reliably since Android 4.2.

No root access, ADK, or special kernel drivers are required.
The library is all in Java, using standard Android libraries.

<img src="./docs/blink1demo.jpg" width="425">

## Quick Start

**1.** Add library to your project:


**2.** If the app should be notified when a device is attached, add
[device_filter.xml](https://github.com/mik3y/usb-serial-for-android/blob/master/usbSerialExamples/src/main/res/xml/device_filter.xml)
to your project's `res/xml/` directory and configure in your `AndroidManifest.xml`.

```xml
<activity
    android:name="..."
    ...>
    <intent-filter>
        <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
    </intent-filter>
    <meta-data
        android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
        android:resource="@xml/device_filter" />
</activity>
```

**3.** Use it! Example code snippet:

open blink(1) device:
```java
private static final String ACTION_USB_PERMISSION = "com.thingm.blink1demo.action.USB_PERMISSION";
PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

Blink1Finder blink1Finder = new Blink1Finder();
blink1Finder.setContext(this);
blink1Finder.setPermissionIntent(permissionIntent);

Blink1 blink1 = blink1Finder.openFirst();     
```
then use Blink1 API:
```java
  int r = 255;
  int g = 0;
  int b = 255;
  blink1.fadeToRGB(100, r,g,b ); // 100 msecs to fade to purple
  String serialnumber = blink1.getSerialNumber();

```

For a simple example, see the `Blink1Demo` in the repo.
