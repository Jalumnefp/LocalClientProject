package es.jfp.localclientproject.models;

import es.jfp.localclientproject.data.Server;
import es.jfp.localclientproject.repositorys.ServerHistoryRepository;

import java.util.List;

public final class ServerHistoryModel {

    private static ServerHistoryModel instance;
    private final ServerHistoryRepository repo;
    private Server tempServer;

    private ServerHistoryModel() {
        this.repo = ServerHistoryRepository.getInstance();
    }

    public static ServerHistoryModel getInstance() {
        synchronized (ServerHistoryModel.class) {
            if (instance == null) {
                instance = new ServerHistoryModel();
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
