package Application.Result;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Result<T> {

    @Getter @Setter
    T value;

    @Getter @Setter
    boolean success;

    @Getter @Setter
    ArrayList<String> errors;



    public Result(String error) {
        ArrayList<String> errors = new ArrayList<>();
        errors.add(error);
        this.errors = errors;
    }

    public Result(T value) {
        this.value = value;
        this.success = true;
    }

    public Result(ArrayList<String> errors) {
        this.errors = errors;
        this.success = false;
    }



    private static final Unit unit = Unit.value();

    public static <T>Result<T> Success(T value) {
        return new Result<T>(value);
    }

    public static <T>Result<T> Failure(String error) {
        return new Result<T>(error);
    }

    public static <T>Result<T> Failure(ArrayList<String> errors) {
        return new Result<T>(errors);
    }

    public static Result<Unit> Success(){
        return new Result<Unit>(unit);
    }


}
