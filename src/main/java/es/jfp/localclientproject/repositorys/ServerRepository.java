package es.jfp.localclientproject.repositorys;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import org.controlsfx.dialog.ProgressDialog;

public class ServerRepository {

    private static ServerRepository instance;

    private Socket socket;

    private ServerRepository() {}

    public static ServerRepository getInstance() {
        synchronized (ServerRepository.class) {
            if (instance==null) {
                instance = new ServerRepository();
            }
            return instance;
        }
    }

    public void setServerSocket(InetAddress ipv4, int port) throws IOException {
        this.socket = new Socket(ipv4, port);
        System.out.println(socket);
    }

    public Map<String, List<String[]>> getDirectoryMap() {
        Map<String, List<String[]>> directoryMap = null;
        try {

            OutputStream os = new BufferedOutputStream(socket.getOutputStream());

            os.write(0);
            os.flush();

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            directoryMap = (Map<String, List<String[]>>) is.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return directoryMap;
    }

    public void uploadFile(File file, String relativePath) {

        System.out.println("Client start upload to: " + file);

        Thread thread = new Thread(() -> {
            try (InputStream is = Files.newInputStream(file.toPath())) {

                OutputStream os = new BufferedOutputStream(socket.getOutputStream());

                String path = relativePath + file.getName();

                os.write(5);
                os.flush();

                os.write(path.getBytes().length);
                os.flush();

                os.write(path.getBytes());
                os.flush();

                long fileSize = file.length();
                long bytesSent = 0;
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    os.flush();
                    bytesSent += bytesRead;

                    int progress = (int) ((bytesSent * 100) / fileSize);
                    System.out.println(progress);
                    //updateProgress(progress, 100);

                }
                os.flush();

                /*Platform.runLater(() -> {

                    Task<Object> task = new Task<>() {
                        @Override
                        protected Object call() throws Exception {

                            return null;
                        }
                    };

                    ProgressDialog progressDialog = new ProgressDialog(task);
                    new Thread(task).start();
                    progressDialog.show();

                });*/

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();

    }

    public void createNewFolder(String folderPath) {
        try {

            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            os.write(2);
            os.flush();

            os.writeUTF(folderPath);
            os.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
