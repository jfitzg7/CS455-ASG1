Author: Jack Fitzgerald
Date: 2/18/2020
Assignment: CS455 Spring 2020 - ASG1


How to build the assignment:

How to execute the Registry node:

How to execute the MessagingNodes:

Class/File Descriptions:
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
        and getBytes()

        - EventFactory: Constructs the appropriate concrete Event based on the type received in a message.

        - NodeReportsOverlaySetupStatus:

        - OverlayNodeReportsTaskFinished:

        - OverlayNodeReportsTrafficSummary:

        - OverlayNodeSendsData:

        - OverlayNodeSendsDeregistration:

        - OverlayNodeSendsRegistration:

        - Protocol: Defines the constants for each type of Event/message

        - RegistryReportsDeregistrationStatus:

        - RegistryReportsRegistrationStatus:

        - RegistryRequestsTaskInitiate:

        - RegistryRequestsTrafficSummary:

        - RegistrySendsNodeManifest:

    src/main/resources:
        - log4j2.xml: The configuration file for log4j2

        - machine_list: A list of CS120 machines used in startup_script.sh

        - startup_script.sh: A script used for remotely starting up MessagingNodes


