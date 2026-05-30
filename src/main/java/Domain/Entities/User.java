package Domain.Entities;

import Domain.Enums.IdentificationType;
import Domain.Enums.Role;
import Domain.Enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Entity {
    private String firstName;
    private String lastName;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String email;
    private String phone;
    private String username;
    private String passwordHash;
    private Role role;
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;
    @Builder.Default
    private Integer failedAttempts = 0;
    private LocalDateTime lockedUntil;
    private LocalDateTime lastAccess;
}
