package Application.Features.Drivers.Commands.Update;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.IdentificationType;
import Domain.Enums.LicenseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateDriverCommand implements ICommand<Unit> {

    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final LicenseCategory licenseCategory;
    private final LocalDate licenseExpiry;
    private final String phone;
    private final String email;
    private final String address;
}