package com.hotelhub.fiap.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelhub.fiap.entities.ConsumoEstadia;
import com.hotelhub.fiap.entities.ReservaHospede;
import com.hotelhub.fiap.entities.TransacaoPagamento;
import com.hotelhub.fiap.entities.dto.CheckinRequestDTO;
import com.hotelhub.fiap.entities.dto.CheckoutRequestDTO;
import com.hotelhub.fiap.entities.dto.InicializacaoResponseDTO;
import com.hotelhub.fiap.services.TotemService;

@RestController
@RequestMapping("/totem")
public class TotemController {

    @Autowired
    private TotemService totemService;

    // 1. Busca configurações visuais e dicionário para montar a tela do totem
    // Exemplo de uso no Postman: GET http://localhost:8080/totem/inicializacao?idiomaIso=pt
    @GetMapping("/inicializacao")
    public ResponseEntity<?> getDadosIniciais(@RequestHeader("X-Hotel-ID") Integer hotelId, 
                                              @RequestParam String idiomaIso) {
        try {
            InicializacaoResponseDTO response = totemService.getDadosIniciais(hotelId, idiomaIso);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Retorna Erro 400 com a mensagem caso algo dê errado
            return ResponseEntity.badRequest().body(e.getMessage()); 
        }
    }
    
 // Passo 1 do Check-in: O Totem chama isso para mostrar os dados NA TELA antes de confirmar
    // Ex: GET /totem/reservas/busca?codigo=GP-2025-003&documento=567.890.123-45
    @GetMapping("/reservas/busca")
    public ResponseEntity<?> buscarParaCheckin(@RequestHeader("X-Hotel-ID") Integer hotelId,
                                               @RequestParam String codigo,
                                               @RequestParam String documento) {
        try {
            ReservaHospede reserva = totemService.buscarReservaEstrita(hotelId, codigo, documento);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Passo 2 do Check-in: O hóspede clica em "Confirmar" e pega o cartão
    @PostMapping("/checkin")
    public ResponseEntity<?> realizarCheckin(@RequestHeader("X-Hotel-ID") Integer hotelId, 
                                             @RequestBody CheckinRequestDTO request) {
        try {
            ReservaHospede reserva = totemService.realizarCheckin(hotelId, request);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

 // Passo 1 e 2 do Checkout: Identificação rígida para mostrar a conta (Extrato)
    // Ex: GET /totem/extrato?codigo=GP-2025-001&documento=345.678.901-23
    @GetMapping("/extrato")
    public ResponseEntity<?> consultarExtratoSeguro(@RequestHeader("X-Hotel-ID") Integer hotelId,
                                                    @RequestParam String codigo,
                                                    @RequestParam String documento) {
        try {
            List<ConsumoEstadia> extrato = totemService.consultarExtratoSeguro(hotelId, codigo, documento);
            return ResponseEntity.ok(extrato);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

 // Passo 3 e 4 do Checkout: Tela de pagamento aprovou, manda finalizar!
    @PostMapping("/checkout")
    public ResponseEntity<?> realizarCheckout(@RequestHeader("X-Hotel-ID") Integer hotelId, 
                                              @RequestBody CheckoutRequestDTO request) {
        try {
            TransacaoPagamento transacao = totemService.realizarCheckout(hotelId, request);
            // Ao retornar sucesso 200, o frontend mostra a mensagem: 
            // "Pagamento aprovado. Cartão desativado, por favor deposite-o na fenda."
            return ResponseEntity.ok(transacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}