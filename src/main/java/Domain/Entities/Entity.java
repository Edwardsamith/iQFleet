package Domain.Entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public abstract class Entity {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
