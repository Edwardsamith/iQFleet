package Domain.Entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Example extends Entity {
    private String name;
    private String apellido;
}
