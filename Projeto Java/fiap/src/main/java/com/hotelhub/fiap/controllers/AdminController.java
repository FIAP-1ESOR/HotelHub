package com.hotelhub.fiap.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotelhub.fiap.entities.ConsumoEstadia;
import com.hotelhub.fiap.entities.Quarto;
import com.hotelhub.fiap.entities.ReservaHospede;
import com.hotelhub.fiap.entities.TransacaoPagamento;
import com.hotelhub.fiap.entities.dto.CheckinManualDTO;
import com.hotelhub.fiap.entities.dto.CheckoutRequestDTO;
import com.hotelhub.fiap.entities.dto.ConsumoDTO;
import com.hotelhub.fiap.entities.dto.NovaReservaDTO;
import com.hotelhub.fiap.services.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/reservas")
    public ResponseEntity<?> criarReserva(@RequestHeader("X-Hotel-ID") Integer hotelId,
                                          @RequestBody NovaReservaDTO dto) {
        try {
            ReservaHospede novaReserva = adminService.criarReserva(hotelId, dto);
            return ResponseEntity.ok(novaReserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/checkin")
    public ResponseEntity<?> checkinManual(@RequestHeader("X-Hotel-ID") Integer hotelId,
                                           @RequestBody CheckinManualDTO dto) {
        try {
            ReservaHospede reserva = adminService.realizarCheckinManual(hotelId, dto);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutManual(@RequestHeader("X-Hotel-ID") Integer hotelId,
                                            @RequestBody CheckoutRequestDTO dto) {
        try {
            // Reutilizamos o CheckoutRequestDTO pois os dados de pagamento são os mesmos
            TransacaoPagamento pagamento = adminService.realizarCheckoutManual(
                    hotelId, dto.getReservaId(), dto.getMetodoPagamento(), dto.getValorPago());
            return ResponseEntity.ok(pagamento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 1. Governança: Listar status dos quartos do Hotel logado
    // Ex: GET http://localhost:8080/admin/quartos
    @GetMapping("/quartos")
    public ResponseEntity<?> listarQuartos(@RequestHeader("X-Hotel-ID") Integer hotelId) {
        try {
            List<Quarto> quartos = adminService.listarQuartos(hotelId);
            return ResponseEntity.ok(quartos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar quartos: " + e.getMessage());
        }
    }

    // 2. Governança: Atualizar status do quarto
    // Ex: PATCH http://localhost:8080/admin/quartos/3/status
    // Body (Text): LIVRE
    @PatchMapping("/quartos/{quartoId}/status")
    public ResponseEntity<?> atualizarStatusQuarto(@RequestHeader("X-Hotel-ID") Integer hotelId, 
                                                   @PathVariable Integer quartoId, 
                                                   @RequestBody String novoStatus) {
        try {
            // A anotação @RequestBody String recebe o novo status em texto plano
            Quarto quartoAtualizado = adminService.atualizarStatusQuarto(hotelId, quartoId, novoStatus.trim());
            return ResponseEntity.ok(quartoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar quarto: " + e.getMessage());
        }
    }

    // 3. Recepção: Lançamento manual de consumos (ex: achou uma lata vazia no checkout)
    // Ex: POST http://localhost:8080/admin/consumos
    @PostMapping("/consumos")
    public ResponseEntity<?> lancarConsumoManual(@RequestHeader("X-Hotel-ID") Integer hotelId, 
                                                 @RequestBody ConsumoDTO consumoDto) {
        try {
            ConsumoEstadia novoConsumo = adminService.lancarConsumoManual(hotelId, consumoDto);
            return ResponseEntity.ok(novoConsumo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Falha ao lançar consumo: " + e.getMessage());
        }
    }

    // 4. Gerência: Métricas e Auditoria Operacional
    // Ex: GET http://localhost:8080/admin/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<?> getMetricas(@RequestHeader("X-Hotel-ID") Integer hotelId) {
        try {
            Map<String, Object> metricas = adminService.gerarMetricasDashboard(hotelId);
            return ResponseEntity.ok(metricas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao gerar dashboard: " + e.getMessage());
        }
    }
}