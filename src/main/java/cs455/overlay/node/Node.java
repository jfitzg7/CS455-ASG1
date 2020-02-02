package cs455.overlay.node;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.net.Socket;

public abstract class Node {
    public EventFactory eventFactory;
    public abstract void onEvent(Socket socket, Event event);
}
