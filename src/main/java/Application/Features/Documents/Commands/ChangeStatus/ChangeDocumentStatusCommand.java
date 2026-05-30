package Application.Features.Documents.Commands.ChangeStatus;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChangeDocumentStatusCommand implements ICommand<Unit> {

    private final UUID documentId;
}