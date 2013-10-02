To support JPA:

1) Implement javapns.devices.Device as a POJO
2) Implement javapns.devices.DeviceFactory to hook up to your own JPA-backed DAO
3) See "injection.xml" file for wiring JavaPNS using Spring
4) Define a getter/setter pair for pushNotificationManager and feedbackServiceManager on any Spring-managed bean to get access to fully-functional PushNotificationManager and FeedbackServiceManager


Notes:

- AppleNotificationServer and AppleFeedbackServer objects can also be injected instead of using *BasicImpl implementations

