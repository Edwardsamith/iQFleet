package Application.Features.Example.Common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExampleResponse {
    private UUID id;
    private String name;
    private String apellido;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
