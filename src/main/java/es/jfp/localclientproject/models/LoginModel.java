package es.jfp.localclientproject.models;

import es.jfp.localclientproject.repositorys.ServerRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginModel {

    private static LoginModel instance;

    private LoginModel(){}

    public static LoginModel getInstance() {
        synchronized (LoginModel.class) {
            if (instance==null) {
                instance = new LoginModel();
            }
            return instance;
        }
    }

    public boolean requestLogin(String username, String password) {
        boolean login;
        try {
            login = ServerRepository.getInstance().loginUser(username, createPasswordHash(password));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return login;
    }

    private String createPasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = messageDigest.digest(password.getBytes());
        return new String(hashBytes, 0, hashBytes.length, StandardCharsets.UTF_8);
    }

}
