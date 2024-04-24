package es.jfp.localclientproject.models;

import es.jfp.localclientproject.data.Server;
import es.jfp.localclientproject.repositorys.ServerConnectionsRepository;

import java.util.List;

public final class ServerConnectionModel {

    private static ServerConnectionModel instance;
    private final ServerConnectionsRepository repo;
    private Server tempServer;

    private ServerConnectionModel() {
        this.repo = ServerConnectionsRepository.getInstance();
    }

    public static ServerConnectionModel getInstance() {
        synchronized (ServerConnectionModel.class) {
            if (instance == null) {
                instance = new ServerConnectionModel();
            }
            return instance;
        }
    }

    public void requestSaveServers(List<Server> serverList) {
        repo.saveServerHistory(serverList);
    }

    public List<Server> requestGetServers() {
        return repo.getServerHistory();
    }

    public void setTempServer(Server server) {
        this.tempServer = server;
    }

    public Server getTempServer() {
        return tempServer;
    }


}
