package cs455.overlay.util;

import sun.rmi.runtime.Log;

import java.util.Arrays;
import java.util.Objects;

public class LogicalNetworkAddress {
    private final byte[] IPAddress;
    private final int portNumber;

    public LogicalNetworkAddress(byte[] IPAddress, int portNumber) {
        this.IPAddress = IPAddress;
        this.portNumber = portNumber;
    }

    public byte[] getIPAddress() {
        return IPAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public boolean equals(Object o) {
        //self check
        if (this == o) {
            return true;
        }

        //null check
        if (o == null) {
            return false;
        }

        //type check and cast
        if (getClass() != o.getClass()) {
            return false;
        }

        LogicalNetworkAddress logicalAddress = (LogicalNetworkAddress) o;

        //field comparison
        return Arrays.equals(IPAddress, logicalAddress.IPAddress)
                && Objects.equals(portNumber, logicalAddress.portNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IPAddress, portNumber);
    }

    @Override
    public String toString() {
        String IPAddressString = Arrays.toString(this.IPAddress);
        String portNumberString = "" + this.portNumber;
        return "{IPAddress = " + IPAddressString + " portNumber = " + portNumberString +"}";
    }
}
