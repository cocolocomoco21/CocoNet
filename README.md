# CocoNet
CocoNet is a lightweight network infrastructure library, utilizing a hybrid peer-to-peer and client/server network architecture. Each device connects to the network as a peer by registering with the server. As such, the server stores and maintains a list of all registered peers, and each peer uses this as a source of truth to talk with any other peer on the network.


## Getting Up and Running
### Requirements
(In process of being updated)
* Java 8
* Gradle (Install here: https://gradle.org/install/)
   
    To initialize Gradle, from the `/CocoNet` directory, run:
    1) `gradle init`
    2) `gradle wrapper`
    3) `gradle build`
* SparkJava

    This is installed through Gradle, but documentation can be found here: http://sparkjava.com/
* (Optional) VSCode, and the Java Extention Pack plugin
    

### Running 
I currently use the debugger from the Java Extention Pack in VSCode to run the server or peers. There are two classes, `ServerRunner` and `PeerRunner`, which allow for easy creation and running of a `Server` or `Peer` and testing of the various functions within these classes. 