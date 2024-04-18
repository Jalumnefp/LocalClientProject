package es.jfp.localclientproject.exceptions;

public class PasswordComplexityException extends Exception {
    public PasswordComplexityException() {
        super("La contraseña obligatoriamente debe contener lo siguiente:\n" +
                " - Al menos 1 número (0-9)\n" +
                " - Al menos 1 letra mayúscula\n" +
                " - Al menos 1 letra minúscula\n" +
                " - Al menos 1 carácter no alfabético\n" +
                " - La contraseña debe tener entre 8 y 16 caracteres y no debe contener espacios");
    }
}
