package Infrastructure.Seeder;

import Domain.Entities.RegistrationRequest;
import Domain.Entities.User;
import Domain.Enums.DecisionStatus;
import Domain.Enums.IdentificationType;
import Domain.Enums.Role;
import Domain.Enums.UserStatus;
import Infrastructure.Repositories.JpaRegistrationRequestRepository;
import Infrastructure.Repositories.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {

    private final JpaUserRepository userRepository;
    private final JpaRegistrationRequestRepository registrationRequestRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * @return [0] = owner, [1] = admin
     */
    public List<User> seed() {
        User owner = userRepository.save(User.builder()
                .firstName("Fabian")
                .lastName("Ramírez")
                .identificationType(IdentificationType.CC)
                .identificationNumber("1020345678")
                .email("fabian.ramirez@iqfleet.co")
                .phone("3001234567")
                .username("fabian.owner")
                .passwordHash(passwordEncoder.encode("Owner@2024"))
                .role(Role.ROLE_OWNER)
                .status(UserStatus.ACTIVE)
                .lastAccess(LocalDateTime.now())
                .build());

        registrationRequestRepository.save(RegistrationRequest.builder()
                .user(owner)
                .decision(DecisionStatus.APPROVED)
                .reviewedBy(owner)
                .decisionDate(LocalDateTime.now().minusMonths(3))
                .build());

        User admin = userRepository.save(User.builder()
                .firstName("Andrés")
                .lastName("González")
                .identificationType(IdentificationType.CC)
                .identificationNumber("1098765432")
                .email("andres.gonzalez@iqfleet.co")
                .phone("3109876543")
                .username("andres.admin")
                .passwordHash(passwordEncoder.encode("Admin@2024"))
                .role(Role.ROLE_ADMIN)
                .status(UserStatus.ACTIVE)
                .lastAccess(LocalDateTime.now().minusDays(1))
                .build());

        registrationRequestRepository.save(RegistrationRequest.builder()
                .user(admin)
                .decision(DecisionStatus.APPROVED)
                .reviewedBy(owner)
                .decisionDate(LocalDateTime.now().minusMonths(2))
                .build());

        User pending = userRepository.save(User.builder()
                .firstName("Laura")
                .lastName("Martínez")
                .identificationType(IdentificationType.CC)
                .identificationNumber("1067890123")
                .email("laura.martinez@gmail.com")
                .phone("3201112233")
                .username("laura.admin")
                .passwordHash(passwordEncoder.encode("Temp@2024"))
                .role(Role.ROLE_ADMIN)
                .status(UserStatus.PENDING)
                .build());

        registrationRequestRepository.save(RegistrationRequest.builder()
                .user(pending)
                .build());

        log.info("Usuarios creados: owner={}, admin={}, pendiente={}", owner.getEmail(), admin.getEmail(), pending.getEmail());
        return List.of(owner, admin);
    }
}
