package cs455.overlay.wireformats;

public interface Protocol {
    byte OVERLAY_NODE_SENDS_REGISTRATION = 2;
    byte REGISTRY_REPORTS_REGISTRATION_STATUS = 3;
    byte OVERLAY_NODE_SENDS_DEREGISTRATION = 4;
    byte REGISTRY_REPORTS_DEREGISTRATION_STATUS = 5;
    byte REGISTRY_SENDS_NODE_MANIFEST = 6;
    byte NODE_REPORTS_OVERLAY_SETUP_STATUS = 7;
    byte REGISTRY_REQUESTS_TASK_INITIATE = 8;
    byte OVERLAY_NODE_SENDS_DATA = 9;
    byte OVERLAY_NODE_REPORTS_TASK_FINISHED = 10;
    byte REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 11;
    byte OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 12;
}
