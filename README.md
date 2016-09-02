javapns-jdk16
=============

Apple Push Notification Service Provider for Java

Fork of JavaPNS to include Maven support - http://code.google.com/p/javapns/

Java1.6 compatible

## Updates

Version 2.3.1 released!

**2.3.1 Changes**
* PushNotificationBigPayload ```complex``` and ```fromJson``` methods fixed
* Fix to make trust store work on IBM JVM

**2.3 Changes**
* iOS>=8 bigger notification payload support (2KB)
* iOS>=7 Silent push notifications support ("content-available":1)

## Installation through Central Maven Repository
javapns-jdk16 is available on the Central Maven Repository.
To use javapns-jdk16 in your project, please add the following dependency to your pom.xml file:
```
<dependency>
	<groupId>com.github.fernandospr</groupId>
	<artifactId>javapns-jdk16</artifactId>
	<version>2.3.1</version>
</dependency>
```

## Usage examples

Based on <a href="https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/TheNotificationPayload.html#//apple_ref/doc/uid/TP40008194-CH107-SW1">Apple docs</a> 

#### Example 1
To send the following payload:
```
{
    "aps" : { "alert" : "Message received from Bob" },
    "acme2" : [ "bang",  "whiz" ]
}
```
You should code the following:
```
PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
payload.addAlert("Message received from Bob");
payload.addCustomDictionary("acme2", Arrays.asList("bang", "whiz"));
```

#### Example 2
To send the following payload:
```
{
    "aps" : {
        "alert" : {
            "title" : "Game Request",
            "body" : "Bob wants to play poker",
            "action-loc-key" : "PLAY"
        },
        "badge" : 5
    },
    "acme1" : "bar",
    "acme2" : [ "bang",  "whiz" ]
}
```
You should code the following:
```
PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
payload.addCustomAlertTitle("Game Request");
payload.addCustomAlertBody("Bob wants to play poker");
payload.addCustomAlertActionLocKey("PLAY");
payload.addBadge(5);
payload.addCustomDictionary("acme1", "bar");
payload.addCustomDictionary("acme2", Arrays.asList("bang", "whiz"));
```

#### Example 3
To send the following payload:
```
{
    "aps" : {
        "alert" : "You got your emails.",
        "badge" : 9,
        "sound" : "bingbong.aiff"
    },
    "acme1" : "bar",
    "acme2" : 42
}
```
You should code the following:
```
PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
payload.addCustomAlertBody("You got your emails.");
payload.addBadge(9);
payload.addSound("bingbong.aiff");
payload.addCustomDictionary("acme1", "bar");
payload.addCustomDictionary("acme2", 42);
```

#### Example 4
To send the following payload:
```
{
    "aps" : {
        "alert" : {
            "loc-key" : "GAME_PLAY_REQUEST_FORMAT",
            "loc-args" : [ "Jenna", "Frank"]
        },
        "sound" : "chime.aiff"
    },
    "acme" : "foo"
}
```
You should code the following:
```
PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
payload.addCustomAlertLocKey("GAME_PLAY_REQUEST_FORMAT");
payload.addCustomAlertLocArgs(Arrays.asList("Jenna", "Frank"));
payload.addSound("chime.aiff");
payload.addCustomDictionary("acme", "foo");
```


#### Example 5
To send the following payload:
```
{
    "aps" : {
        "content-available" : 1
    }, 
    "acme" : "foo"
}
```
You should code the following:
```
PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
payload.setContentAvailable(true);
payload.addCustomDictionary("acme", "foo");
```


#### Example 6
To send the following payload:
```
{
  "aps": {
    "mutable-content":1,
    "alert":{
      "title": "Notification title",
      "subtitle": "Notification subtitle",
      "body": "Notification body"
    }
  },
  "media-attachment": "https://url/to/content.mpg"
}
```
You should code the following:
```
PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
payload.addCustomAlertTitle("Notification title");
payload.addCustomAlertSubtitle("Notification subtitle");
payload.addCustomAlertBody("Notification body");
payload.setMutableContent(true);
payload.addCustomDictionary("media-attachment", "https://url/to/content.mpg");
```