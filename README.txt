Author: Jack Fitzgerald
Date: 2/18/2020
Assignment: CS455 Spring 2020 - ASG1


HOW TO BUILD THE ASSIGNMENT:
    1. Enter "gradle build" at the command line in the same directory that the tar file was unpacked.

        1.1 Alternatively, you can enter "./gradlew build" at the command line to use the gradle wrapper instead.
        Use this if you don't have gradle in your PATH variable.

    2. Confirm that a newly created "build" directory shows up in the same directory where the tar file was unpacked.


HOW TO EXECUTE the Registry node:
    1. Make sure that the build succeeded after building the project with either "gradle build" or "./gradlew build",
    and that there is a new "build" directory that shows up in the same directory where the tar file was unpacked.

    2. To run the Registry node, enter "java -cp build/libs/ASG1-1.0.jar cs455.overlay.node.Registry <port number>"
    at the command line in the same directory where the tar file was originally unpacked.

        2.1 the "<port number>" can be any available port number. If the port number is already in use then the
        Registry will need to be run again, but with a different port number.

    3. Once the Registry node is running, "Enter a registry command: " should appear at the command line and you can start
    issuing registry commands, registering MessagingNodes, set up the overlay, etc...


HOW TO EXECUTE MessagingNodes:
    1. As with the Registry node, Make sure that the build succeeded after building the project with either "gradle build"
    or "./gradlew build", and that there is a new "build" directory that shows up in the same directory where the tar file was unpacked.

    2. Make sure that there is a Registry node running before executing a MessagingNode

    3. To run a MessagingNode, enter "java -cp build/libs/ASG1-1.0.jar cs455.overlay.node.MessagingNode <Registry IP address/hostname> <Registry port>"
    at the command line in the same directory where the tar file was originally unpacked.

        3.1 You can use either the IP address (i.e. 129.82.44.157) or the hostname (i.e. montgomery.cs.colostate.edu) for the <Registry IP address/hostname> argument.

    4. You can start the MessagingNodes manually, or if you'd like to you can use my startup_script.sh to start 10 MessagingNodes remotely. For the sake of testing,
    I would highly recommend starting the MessagingNodes manually just to be safe, but if you would like to save some time feel free to use the startup script.

        4.1 IMPORTANT NOTE!: My startup script assumes that the Registry node is running on montgomery.cs.colostate.edu (129.82.44.157) on port 5001. Unless the Registry is
        running on the montgomery machine at port 5001, the MessagingNodes started in the script will not be able to connect to the Registry node. You can manually change the
        variables associated with the Registry IP address and port number in startup_script.sh if you would like to!

        4.2 ANOTHER IMPORTANT NOTE!: If you use the startup_script.sh, you will certainly have to change the "user" variable to your cs username, and very likely the "jar_path"
        variable will need to be changed as well!


IMPORTANT THINGS TO NOTE:
    - Most of my error handling is handled with log4j statements, I have turned logging off so most errors will not appear on the console if they do occur!
    If you suspect errors are occurring, then I recommend turning on WARN and ERROR statements. You can do this by navigating to src/main/resources and modifying
    the log4j2.xml file slightly. On line 12 of log4j2.xml, change <Root level="off"> to <Root level="warn"> to turn on WARN and ERROR statements. After doing this,
    build the project again in the directory where the tar file was originally unpacked by running "gradle build" or "./gradlew build" again.


CLASS/FILE DESCRIPTIONS:
    cs455.overlay.node:
        - Node: An abstract class that contains a public EventFactory member and defines an abstract method
        called onEvent which the MessagingNode and Registry override and use to handle incoming messages

        - Registry: A concrete implementation of the Node class, it is used to maintain a registration table
        of MessagingNodes and to ultimately construct routing tables for each registered MessagingNode which
        are used to construct a logical overlay between all registered MessagingNodes. Once the overlay is successfully
        created, the Registry will then instruct MessagingNodes to start sending rounds of messages to other randomly chosen
        MessagingNodes in the overlay.

        - MessagingNode: A concrete implementation of the Node class. It takes in two arguments, the IP address
        and port number of a Registry node, and immediately attempts to register itself with the Registry node upon
        starting up. Once the Registry successfully establishes routing tables at each registered MessagingNode,
        they will begin sending messages to one another and report their traffic summaries back to the Registry.

    cs455.overlay.transport:
        - TCPConnection: A class that encapsulates a TCP connection, contains a TCPSender and TCPReceiverThread

        - TCPConnectionCache: A cache for TCPConnections that the MessagingNodes use to store their connections
        to other MessagingNodes in their routing tables

        - TCPReceiverThread: A thread for handling the input stream of a socket/connection between a Registry node
        and a MessagingNode or between two MessagingNodes.

        - TCPSender: A class for handling the output stream of a socket/connection between a Registry node and a
        MessagingNode or between two MessagingNodes.

        - TCPServerThread: A server thread that is used to spawn a new socket when a MessagingNode attempts to establish
        a connection to a Registry node or another MessagingNode.

    cs455.overlay.util:
        - InteractiveMessagingNodeCommandParser: The foreground command parsing thread for MessagingNodes.

        - InteractiveRegistryCommandParser: The foreground command parsing thread for the Registry.

        - LogicalNetworkAddress: Contains an IPAddress (byte[]) and a portNumber (int) which is used to store/compare
        network addresses of MessagingNodes and the Registry. Essentially represents an network address.

        - MessagingNodeInfo: Contains an LogicalNetworkAddress and a Socket associated with that network address. Used
        in the RegistrationTable to associate a socket with a registered MessagingNode.

        - NodeReportsOverlaySetupStatusHandler: Converts an Event back into a NodeReportsOverlaySetupStatus message that
        can be used to get information associated with that particular message.

        - OverlayNodeReportsTrafficSummaryHandler: Converts an Event back into a OverlayNodeReportsTrafficSummary message
        that can be used to get information associated with that particular message.

        - OverlayNodeSendsDataHandler: Converts an Event back into a OverlayNodeSendsData message that can be used to get
        information associated with that particular message.

        - OverlayNodeSendsDeregistrationHandler: Converts an Event back into a OverlayNodeSendsDeregistration message that
        can be used to get information associated with that particular message.

        - OverlayNodeSendsRegistrationHandler: Converts an Event back into a OverlayNodeSendsRegistration message that can
        be used to get information associated with that particular message.

        - RegistrationTable: Used to store and retrieve information about registered MessagingNodes

        - RegistryReportsDeregistrationStatusHandler: Converts an Event back into a RegistryReportsDeregistrationStatus message
        that can be used to get information associated with that particular message.

        - RegistryReportsRegistrationStatusHandler: Converts an Event back into a RegistryReportsRegistrationStatus message that
        can be used to get information associated with that particular message.

        - RegistryRequestsTaskInitiateHandler: Converts an Event back into a RegistryRequestsTaskInitiate message that can be used
        to get information associated with that particular message.

        - RegistrySendsNodeManifestHandler: Converts an Event back into a RegistrySendsNodeManifest message that can be used to get
        information associated with that particular message.

        - RoutingEntry: Stores the node ID, IP address, port number, and hops away of a particular entry in a MessagingNode's RoutingTable

        - RoutingTable: Stores RoutingEntrys that MessagingNodes use to initiate connections with other MessagingNodes and subsequently
        send messages to them when instructed to do so by the Registry

        - TrafficSummary: Encapsulates the statistics of sent/received/relay trackers and sent/receive summations at the MessagingNodes
        after they have finished a set of messaging rounds and the Registry requests a traffic summary.

    cs455.overlay.wireformats:
        - Event: An abstract class that represents the messages exchanged between nodes. It contains two abstract methods getType()
        and getBytes(). getType() returns the specified message type (2-12), and getBytes() returns a byte[] of the information contained
        in that specific message.

        - EventFactory: Constructs the appropriate concrete Event based on the type received in a message.

        - NodeReportsOverlaySetupStatus: Concrete implementation of Event for the NODE_REPORTS_OVERLAY_SETUP_STATUS message.

        - OverlayNodeReportsTaskFinished: Concrete implementation of Event for the OVERLAY_NODE_REPORTS_TASK_FINISHED message.

        - OverlayNodeReportsTrafficSummary: Concrete implementation of Event for the OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY message.

        - OverlayNodeSendsData: Concrete implementation of Event for the OVERLAY_NODE_SENDS_DATA message.

        - OverlayNodeSendsDeregistration: Concrete implementation of Event for the OVERLAY_NODE_SENDS_DEREGISTRATION message.

        - OverlayNodeSendsRegistration: Concrete implementation of Event for the OVERLAY_NODE_SENDS_REGISTRATION message.

        - Protocol: Defines the constants for each type of Event/message

        - RegistryReportsDeregistrationStatus: Concrete implementation of Event for the REGISTRY_REPORTS_DEREGISTRATION_STATUS message.

        - RegistryReportsRegistrationStatus: Concrete implementation of Event for the REGISTRY_REPORTS_REGISTRATION_STATUS message.

        - RegistryRequestsTaskInitiate: Concrete implementation of Event for the REGISTRY_REQUESTS_TASK_INITIATE message.

        - RegistryRequestsTrafficSummary: Concrete implementation of Event for the REGISTRY_REQUESTS_TRAFFIC_SUMMARY message.

        - RegistrySendsNodeManifest: Concrete implementation of Event for the REGISTRY_SENDS_NODE_MANIFEST message.

    src/main/resources:
        - log4j2.xml: The configuration file for log4j2

        - machine_list: A list of CS120 machines used in startup_script.sh

        - startup_script.sh: A script used for remotely starting up MessagingNodes


