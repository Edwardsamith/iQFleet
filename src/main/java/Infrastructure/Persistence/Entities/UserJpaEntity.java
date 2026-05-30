package Infrastructure.Persistence.Entities;

import Domain.Enums.IdentificationType;
import Domain.Enums.Role;
import Domain.Enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email",          columnNames = "email"),
        @UniqueConstraint(name = "uk_user_username",       columnNames = "username"),
        @UniqueConstraint(name = "uk_user_identification", columnNames = {"identification_type", "identification_number"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity extends BaseJpaEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false, length = 3)
    private IdentificationType identificationType;

    @Column(name = "identification_number", nullable = false, length = 12)
    private String identificationNumber;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "failed_attempts", nullable = false)
    @Builder.Default
    private Integer failedAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;
}
