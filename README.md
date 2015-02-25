InMobiAPIMonetizationLibrary-Java
=================================

This is the API library, build for Server integrations which use Java technology.

The code structure would be split into these components:

Request classes/model stubs - As per the JSON request
Enums - To help create efficient AdRequestObject underlying structure.
Network package - For http connections with InMobi API 2.0
Utils package - For serialization, Base64, etc utility work
Serialization - JSON serializer/de-serializer
Base64
XML Parser 
Others
Third party classes - If any third party “as-is” component is required.
Monetization package - consisting of IMBanner, IMInterstitial, IMNative classes.


Publishers are expected to use classes under monetization package directly, and pass IMAdRequest object with valid arguments, to obtain ads from InMobi.

Note: Publishers may freely use any components of the code they like, if the entire project, or its components may not be used somehow.
