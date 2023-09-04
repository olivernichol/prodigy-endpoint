# prodigy-endpoint

The Native Android SDK based endpoint software, that sent notification and battery data to Prodigy Panel - part of my notification centralising project, Prodigy.

This piece of software was a very simple project, that I used as my first project developed using the native Android SDK, as I prepared to migrate TalkFlare from development in Unity, to development in the Android SDK. It takes notification and battery events from the device, and uses HTTP requests to communicate the information over to the server, which then uses firebase messaging to communicate with the panel, see the other projects for information.

Given that I only ever intended for this project to be used or looked at myself, the code is undocumented, however in most areas it's fairly self explanatory, note that IP addresses and BSSIDs have been removed for security reasons. Otherwise, I hope this project is interesting/useful!
