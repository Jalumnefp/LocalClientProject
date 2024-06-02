package es.jfp.localclientproject.repositorys;

import es.jfp.SerialMap;
import es.jfp.localclientproject.elements.ProgressWidget;
import es.jfp.localclientproject.models.MainModel;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.socket = new Socket();
        socket.connect(new InetSocketAddress(ipv4, port), 3000);
        System.out.println(socket);
    }

    public SerialMap getDirectoryMap() {
        try {

            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(11);
            os.flush();

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

            return (SerialMap) is.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean loginUser(String username, String hashPassword) {
        System.out.println("login username " + username);
        try {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            os.write(02);
            os.flush();
            os.writeUTF(username);
            os.flush();
            os.writeUTF(hashPassword);
            os.flush();

            return is.readBoolean();

        } catch (IOException e) {
            return false;
        }
    }

    public boolean registerUser(String username, String hashPassword) {
        System.out.println("register username " + username);
        try {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            os.write(01);
            os.writeUTF(username);
            os.writeUTF(hashPassword);

            return is.readBoolean();

        } catch (IOException e) {
            return false;
        }
    }

    public void closeSession() {
        try {
            OutputStream os = socket.getOutputStream();

            os.write(03);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(File file, String relativePath) {

        System.out.println("Client start upload to: " + file);

        ProgressWidget progressWidget = new ProgressWidget("Upload " + file.getName());

        Thread uploadThread = new Thread(() -> {

            MainModel.getInstance().insertOnProcessToolbar(progressWidget);
            try (InputStream is = Files.newInputStream(file.toPath())) {

                OutputStream os = new BufferedOutputStream(socket.getOutputStream());

                os.write(21);
                os.flush();

                os.write(relativePath.getBytes().length);
                os.flush();

                os.write(relativePath.getBytes());
                os.flush();

                long bytesSent = 0;
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    os.flush();
                    bytesSent += bytesRead;

                    int progress = (int) (((float) bytesSent / file.length()) * 100);

                    Platform.runLater(() -> progressWidget.setBarProgress(progress));
                }
                os.write(-1);
                os.flush();
                System.out.println("Final");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        progressWidget.setProcessThread(uploadThread);
    }

    public void downloadFile(String destinationPath, String path) {

        ProgressWidget progressWidget = new ProgressWidget("Download " + Path.of(path).getFileName());
        MainModel.getInstance().insertOnProcessToolbar(progressWidget);

        Thread downloadThread = new Thread(() -> {
            try (OutputStream fos = Files.newOutputStream(Path.of(destinationPath))) {

                OutputStream os = new BufferedOutputStream(socket.getOutputStream());
                InputStream is = new BufferedInputStream(socket.getInputStream());

                os.write(22);
                os.flush();

                os.write(path.getBytes().length);
                os.flush();

                os.write(path.getBytes());
                os.flush();

                long bytesSent = 0;
                byte[] buffer = new byte[2048];
                int bytes;
                while ((bytes=is.read(buffer))!=-1) {
                    if (bytes == 1 && buffer[0] == -1) {
                        break;
                    }
                    fos.write(buffer, 0, bytes);
                    fos.flush();
                    bytesSent += bytes;
                    int progress = (int) (((float) bytesSent / new File(path).length()) * 100);

                    Platform.runLater(() -> progressWidget.setBarProgress(progress));
                }
                System.out.println("Final");
                fos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        progressWidget.setProcessThread(downloadThread);
    }

    public void downloadFolder(String destinationPath, String path) {
        // descargar zip
    }

    public void deleteFile(String pathToDelete) {
        try {

            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            os.write(24);
            os.flush();
            os.writeUTF(pathToDelete);
            os.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFolder(String pathToDelete) {
        try {

            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            os.write(14);
            os.flush();
            os.writeUTF(pathToDelete);
            os.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNewFolder(String folderPath) {
        try {

            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            os.write(12);
            os.flush();

            os.writeUTF(folderPath);
            os.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean socketIsRunning() {
        return this.socket.isConnected();
    }

}
