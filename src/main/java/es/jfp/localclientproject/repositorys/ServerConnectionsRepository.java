package es.jfp.localclientproject.repositorys;

import es.jfp.localclientproject.data.Server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

public final class ServerConnectionsRepository {

    private static ServerConnectionsRepository instance;
    private final Path historyPath;

    private ServerConnectionsRepository() {
        this.historyPath = Path.of("files/server_connections.csv");
    }

    public static ServerConnectionsRepository getInstance() {
        synchronized (ServerConnectionsRepository.class) {
            if (instance==null) {
                instance = new ServerConnectionsRepository();
            }
            return instance;
        }
    }

    public void saveServerHistory(List<Server> serverList) {
        try (BufferedWriter bw = Files.newBufferedWriter(historyPath)) {
            for (Server svf: serverList) {
                bw.write(svf.getCsvFormat(',') + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Server> getServerHistory() {
        List<Server> serverList = new LinkedList<>();
        try (BufferedReader br = Files.newBufferedReader(historyPath)) {
            String line;
            while((line = br.readLine())!=null) {
                String[] data = line.split(",");
                serverList.add(new Server(data[0], data[1], data[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverList;
    }

}
