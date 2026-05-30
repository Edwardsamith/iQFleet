package Application.Features.Drivers.Commands.Create;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.IdentificationType;
import Domain.Enums.LicenseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CreateDriverCommand implements ICommand<Unit> {

    private final IdentificationType identificationType;
    private final String identificationNumber;
    private final String firstName;
    private final String lastName;
    private final String licenseNumber;
    private final LicenseCategory licenseCategory;
    private final LocalDate licenseExpiry;
    private final String phone;
    private final String email;
    private final String address;
    private final LocalDate registrationDate;
}
