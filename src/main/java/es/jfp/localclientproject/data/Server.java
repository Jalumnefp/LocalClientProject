package es.jfp.localclientproject.data;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {

    private String alias;
    private InetAddress ipv4;
    private int port;

    public Server(String alias, InetAddress ipv4, int port) {
        this.alias = alias;
        this.ipv4 = ipv4;
        this.port = port;
    }

    public Server(String alias, String ipv4, String port) {
        this.alias = alias;
        try {
            this.ipv4 = InetAddress.getByName(ipv4);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.port = Integer.parseInt(port);
    }

    @Override
    public String toString() {
        return alias;
    }

    public String getCsvFormat(char delimiter) {
        return alias + delimiter + ipv4.getHostAddress() + delimiter + port;
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
