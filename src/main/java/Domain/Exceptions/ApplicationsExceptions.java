package Domain.Exceptions;

public class ApplicationsExceptions extends Exception {
    public ApplicationsExceptions() {
        super("Error aplicacion iQFleet");
    }

    public ApplicationsExceptions(String message) {
        super("Error aplicacion iQFleet: " + message);
    }

    public ApplicationsExceptions(String message, Throwable cause) {
        super("Error aplicacion iQFleet: " + message, cause);
    }
}
