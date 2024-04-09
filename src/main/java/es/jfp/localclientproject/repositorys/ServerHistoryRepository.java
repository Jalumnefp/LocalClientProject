package es.jfp.localclientproject.repositorys;

import es.jfp.localclientproject.data.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ServerHistoryRepository {

    private static ServerHistoryRepository instance;
    private final Path historyPath;

    private ServerHistoryRepository() {
        this.historyPath = Path.of("files/saved_servers");
    }

    public static ServerHistoryRepository getInstance() {
        synchronized (ServerHistoryRepository.class) {
            if (instance==null) {
                instance = new ServerHistoryRepository();
            }
            return instance;
        }
    }

    public void saveServerHistory(List<Server> serverList) {
        try (
                OutputStream os = Files.newOutputStream(historyPath,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                ObjectOutputStream oos = new ObjectOutputStream(os);
        ) {

            oos.writeObject(serverList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Server> getServerHistory() {
        List<Server> serverList = new LinkedList<>();

        try (
                InputStream is = Files.newInputStream(historyPath);
                ObjectInputStream ois = new ObjectInputStream(is);
        ) {

            while (is.available()>0) {
                serverList = (List<Server>) ois.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return serverList;
    }

}
