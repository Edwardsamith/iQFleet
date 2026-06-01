package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Dashboard.Queries.GetAlertasActivas.GetAlertasActivasQuery;
import Application.Features.Dashboard.Queries.GetPanelAdmin.GetPanelAdminQuery;
import Application.Features.Dashboard.Queries.GetResumenFinanciero.GetResumenFinancieroQuery;
import Application.Features.Dashboard.Queries.GetResumenFlota.GetResumenFlotaQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Panel de métricas y estado general — RF-009")
public class DashboardController {

    private final IMediator mediator;

    @Operation(summary = "Totalizadores operativos de la flota")
    @GetMapping("/resumen-flota")
    public ResponseEntity<?> resumenFlota() {
        var result = mediator.send(new GetResumenFlotaQuery());
        if (!result.isSuccess()) return ResponseEntity.internalServerError().body(result.getErrors());
        return ResponseEntity.ok(result.getValue());
    }

    @Operation(summary = "Indicadores financieros del período")
    @GetMapping("/resumen-financiero")
    public ResponseEntity<?> resumenFinanciero(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        var result = mediator.send(new GetResumenFinancieroQuery(fechaDesde, fechaHasta));
        if (!result.isSuccess()) return ResponseEntity.internalServerError().body(result.getErrors());
        return ResponseEntity.ok(result.getValue());
    }

    @Operation(summary = "Alertas activas: documentos vencidos y por vencer, licencias próximas")
    @GetMapping("/alertas")
    public ResponseEntity<?> alertas() {
        var result = mediator.send(new GetAlertasActivasQuery());
        if (!result.isSuccess()) return ResponseEntity.internalServerError().body(result.getErrors());
        return ResponseEntity.ok(result.getValue());
    }

    @Operation(summary = "Estado de usuarios y actividad del sistema")
    @GetMapping("/panel-admin")
    public ResponseEntity<?> panelAdmin() {
        var result = mediator.send(new GetPanelAdminQuery());
        if (!result.isSuccess()) return ResponseEntity.internalServerError().body(result.getErrors());
        return ResponseEntity.ok(result.getValue());
    }
}
