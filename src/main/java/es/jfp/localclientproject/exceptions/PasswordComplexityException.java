package es.jfp.localclientproject.exceptions;

public class PasswordComplexityException extends Exception {
    public PasswordComplexityException() {
        super("""
            La contraseña obligatoriamente debe contener lo siguiente:
             - Al menos 1 número (0-9)
             - Al menos 1 letra mayúscula
             - Al menos 1 letra minúscula
             - Al menos 1 carácter no alfabético
             - La contraseña debe tener entre 8 y 16 caracteres y no debe contener espacios""");
    }
}
