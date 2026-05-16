package Application.Result;

import lombok.Data;

public @Data class Unit {

    public static Unit value() {
        return new Unit();
    }

}
