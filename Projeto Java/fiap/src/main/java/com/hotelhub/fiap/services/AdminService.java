package com.hotelhub.fiap.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotelhub.fiap.entities.CartaoChave;
import com.hotelhub.fiap.entities.ConsumoEstadia;
import com.hotelhub.fiap.entities.Hotel;
import com.hotelhub.fiap.entities.Quarto;
import com.hotelhub.fiap.entities.ReservaHospede;
import com.hotelhub.fiap.entities.TransacaoPagamento;
import com.hotelhub.fiap.entities.dto.CheckinManualDTO;
import com.hotelhub.fiap.entities.dto.ConsumoDTO;
import com.hotelhub.fiap.entities.dto.NovaReservaDTO;
import com.hotelhub.fiap.repositories.CartaoChaveRepository;
import com.hotelhub.fiap.repositories.ConsumoEstadiaRepository;
import com.hotelhub.fiap.repositories.HotelRepository;
import com.hotelhub.fiap.repositories.QuartoRepository;
import com.hotelhub.fiap.repositories.ReservaHospedeRepository;
import com.hotelhub.fiap.repositories.TransacaoPagamentoRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminService {

    @Autowired
    private QuartoRepository quartoRepository;
    @Autowired
    private ReservaHospedeRepository reservaRepository;
    @Autowired
    private ConsumoEstadiaRepository consumoRepository;
    @Autowired
    private TransacaoPagamentoRepository pagamentoRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private CartaoChaveRepository cartaoChaveRepository;

    public java.util.Map<String, Object> gerarMetricasDashboard(Integer hotelId) {
        List<Quarto> todosQuartos = quartoRepository.findByHotelId(hotelId);
        List<TransacaoPagamento> transacoes = pagamentoRepository.findByHotelId(hotelId);
        
        long totalQuartos = todosQuartos.size();
        long quartosLivres = todosQuartos.stream().filter(q -> "LIVRE".equals(q.getStatus())).count();
        long quartosOcupados = todosQuartos.stream().filter(q -> "OCUPADO".equals(q.getStatus())).count();
        long quartosEmLimpeza = todosQuartos.stream().filter(q -> "LIMPEZA".equals(q.getStatus())).count();

        java.math.BigDecimal faturamentoTotal = transacoes.stream()
                .map(TransacaoPagamento::getValorPago)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("totalQuartos", totalQuartos);
        dashboard.put("quartosLivres", quartosLivres);
        dashboard.put("quartosOcupados", quartosOcupados);
        dashboard.put("quartosEmLimpeza", quartosEmLimpeza);
        dashboard.put("faturamentoTotal", faturamentoTotal);
        dashboard.put("totalTransacoes", transacoes.size());
        
        return dashboard;
    }
    
    public List<Quarto> listarQuartos(Integer hotelId) {
        return quartoRepository.findByHotelId(hotelId);
    }

    public Quarto atualizarStatusQuarto(Integer hotelId, Integer quartoId, String novoStatus) {
        Quarto quarto = quartoRepository.findById(quartoId)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));

        if (!quarto.getHotel().getId().equals(hotelId)) {
            throw new RuntimeException("Acesso negado.");
        }

        quarto.setStatus(novoStatus.toUpperCase());
        return quartoRepository.save(quarto);
    }

    public ConsumoEstadia lancarConsumoManual(Integer hotelId, ConsumoDTO dto) {
        ReservaHospede reserva = reservaRepository.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));

        if (!reserva.getHotel().getId().equals(hotelId)) {
            throw new RuntimeException("Acesso negado ao hóspede de outro hotel.");
        }

        ConsumoEstadia consumo = new ConsumoEstadia();
        consumo.setHotel(reserva.getHotel());
        consumo.setReserva(reserva);
        consumo.setTipoConsumo(dto.getTipoConsumo());
        consumo.setDescricaoItem(dto.getDescricaoItem());
        consumo.setQuantidade(dto.getQuantidade());
        consumo.setPrecoUnitarioMomento(dto.getPrecoUnitarioMomento());
        
        consumo.setValorTotalItem(dto.getPrecoUnitarioMomento().multiply(new java.math.BigDecimal(dto.getQuantidade())));

        return consumoRepository.save(consumo);
    }
    
    @Transactional
    public ReservaHospede criarReserva(Integer hotelId, NovaReservaDTO dto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel não encontrado."));

        ReservaHospede reserva = new ReservaHospede();
        reserva.setHotel(hotel);
        reserva.setNomeHospede(dto.getNomeHospede());
        reserva.setDocumentoCpfPassaporte(dto.getDocumentoCpfPassaporte());
        reserva.setQuantidadePessoas(dto.getQuantidadePessoas());
        reserva.setDataEntradaPrevista(dto.getDataEntradaPrevista());
        reserva.setDataSaidaPrevista(dto.getDataSaidaPrevista());
        reserva.setTermoConsentimentoAceito(false);
        reserva.setStatusReserva("PENDENTE");

        int ano = java.time.LocalDate.now().getYear();
        String hash = java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        reserva.setCodigoReserva("HTL-" + ano + "-" + hash);

        return reservaRepository.save(reserva);
    }
    
    @Transactional
    public ReservaHospede realizarCheckinManual(Integer hotelId, CheckinManualDTO dto) {
        ReservaHospede reserva = reservaRepository.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));

        if (!reserva.getHotel().getId().equals(hotelId)) {
            throw new RuntimeException("Acesso negado.");
        }
        if (!"PENDENTE".equals(reserva.getStatusReserva())) {
            throw new RuntimeException("Reserva não está pendente.");
        }

        if (dto.getQuartoId() != null) {
            Quarto quartoEscolhido = quartoRepository.findById(dto.getQuartoId())
                    .orElseThrow(() -> new RuntimeException("Quarto inválido."));
            if (!"LIVRE".equals(quartoEscolhido.getStatus())) {
                throw new RuntimeException("O quarto selecionado não está LIVRE.");
            }
            reserva.setQuarto(quartoEscolhido);
        } else {
            List<Quarto> quartosLivres = quartoRepository.findByHotelIdAndStatus(hotelId, "LIVRE");
            if (quartosLivres.isEmpty()) throw new RuntimeException("Sem quartos disponíveis.");
            reserva.setQuarto(quartosLivres.get(0));
        }

        Quarto quarto = reserva.getQuarto();
        quarto.setStatus("OCUPADO");
        quartoRepository.save(quarto);

        reserva.setStatusReserva("RESERVA_ATIVA");
        reserva.setDataEntradaReal(LocalDateTime.now());
        reserva.setTermoConsentimentoAceito(true); 

        CartaoChave cartao = new CartaoChave();
        cartao.setHotel(reserva.getHotel());
        cartao.setReserva(reserva);
        cartao.setNumeroCartaoChave("KEY-MANUAL-" + java.util.UUID.randomUUID().toString().substring(0,5));
        cartao.setStatusCartao("ATIVO");
        cartaoChaveRepository.save(cartao);

        return reservaRepository.save(reserva);
    }

    @Transactional
    public TransacaoPagamento realizarCheckoutManual(Integer hotelId, Integer reservaId, String metodoPagamento, java.math.BigDecimal valorPago) {
        ReservaHospede reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));

        if (!reserva.getHotel().getId().equals(hotelId)) throw new RuntimeException("Acesso negado.");

        TransacaoPagamento pagamento = new TransacaoPagamento();
        pagamento.setHotel(reserva.getHotel());
        pagamento.setReserva(reserva);
        pagamento.setMetodoPagamento(metodoPagamento);
        pagamento.setValorPago(valorPago);
        pagamento.setDataHoraPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        List<CartaoChave> cartoes = cartaoChaveRepository.findByReservaId(reserva.getId());
        cartoes.forEach(c -> c.setStatusCartao("INATIVO"));
        cartaoChaveRepository.saveAll(cartoes);

        Quarto quarto = reserva.getQuarto();
        quarto.setStatus("LIMPEZA");
        quartoRepository.save(quarto);

        reserva.setStatusReserva("FINALIZADA");
        reserva.setDataSaidaReal(LocalDateTime.now());
        reservaRepository.save(reserva);

        return pagamento;
    }
}