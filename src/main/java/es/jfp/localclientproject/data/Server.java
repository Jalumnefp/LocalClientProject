package es.jfp.localclientproject.data;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class Server implements Serializable {

    private String alias;
    private InetAddress ipv4;
    private int port;

    public Server(String alias, InetAddress ipv4, int port) {
        this.alias = alias;
        this.ipv4 = ipv4;
        this.port = port;
    }

    @Override
    public String toString() {
        return alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public InetAddress getIpv4() {
        return ipv4;
    }

    public void setIpv4(InetAddress ipv4) {
        this.ipv4 = ipv4;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
