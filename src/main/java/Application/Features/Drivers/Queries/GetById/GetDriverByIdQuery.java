package Application.Features.Drivers.Queries.GetById;

import Application.Abstractions.IQuery;
import Domain.Entities.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetDriverByIdQuery implements IQuery<Driver> {

    private final UUID id;
}