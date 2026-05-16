package Domain.Exceptions;

public class ApplicationsExepctions extends Exception{
    public ApplicationsExepctions(){
        super("Error aplicacion iQFleet");
    }

    public ApplicationsExepctions(String message){
        super("Error aplicacion iQFleet: " + message);
    }

    public ApplicationsExepctions(String message, Throwable cause){
        super("Error aplicacion iQFleet: " + message, cause);
    }
}
