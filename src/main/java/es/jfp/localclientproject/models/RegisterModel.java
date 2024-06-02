package es.jfp.localclientproject.models;

import es.jfp.localclientproject.repositorys.ServerRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterModel {

    private static RegisterModel instance;

    private RegisterModel(){}

    public static RegisterModel getInstance() {
        synchronized (RegisterModel.class) {
            if (instance==null) {
                instance = new RegisterModel();
            }
            return instance;
        }
    }

    public boolean requestRegister(String username, String password) {
        try {
            System.out.println(1);
            return ServerRepository.getInstance().registerUser(username, createPasswordHash(password));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String createPasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = messageDigest.digest(password.getBytes());
        return new String(hashBytes, 0, hashBytes.length, StandardCharsets.UTF_8);
    }

}
