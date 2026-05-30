package Infrastructure.Persistence.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "examples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleJpaEntity extends BaseJpaEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
}
