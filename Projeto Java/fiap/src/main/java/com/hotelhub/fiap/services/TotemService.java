package com.hotelhub.fiap.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotelhub.fiap.entities.CartaoChave;
import com.hotelhub.fiap.entities.ConfigWhiteLabel;
import com.hotelhub.fiap.entities.ConsumoEstadia;
import com.hotelhub.fiap.entities.DicionarioTotem;
import com.hotelhub.fiap.entities.Idioma;
import com.hotelhub.fiap.entities.Quarto;
import com.hotelhub.fiap.entities.ReservaHospede;
import com.hotelhub.fiap.entities.TransacaoPagamento;
import com.hotelhub.fiap.entities.dto.CheckinRequestDTO;
import com.hotelhub.fiap.entities.dto.CheckoutRequestDTO;
import com.hotelhub.fiap.entities.dto.InicializacaoResponseDTO;
import com.hotelhub.fiap.repositories.CartaoChaveRepository;
import com.hotelhub.fiap.repositories.ConfigWhiteLabelRepository;
import com.hotelhub.fiap.repositories.ConsumoEstadiaRepository;
import com.hotelhub.fiap.repositories.DicionarioTotemRepository;
import com.hotelhub.fiap.repositories.IdiomaRepository;
import com.hotelhub.fiap.repositories.QuartoRepository;
import com.hotelhub.fiap.repositories.ReservaHospedeRepository;
import com.hotelhub.fiap.repositories.TransacaoPagamentoRepository;

@Service
public class TotemService {

    @Autowired
    private ReservaHospedeRepository reservaRepository;
    @Autowired
    private QuartoRepository quartoRepository;
    @Autowired
    private CartaoChaveRepository cartaoChaveRepository;
    @Autowired
    private ConsumoEstadiaRepository consumoRepository;
    @Autowired
    private TransacaoPagamentoRepository pagamentoRepository;
    @Autowired
    private ConfigWhiteLabelRepository whiteLabelRepository;
    @Autowired
    private IdiomaRepository idiomaRepository;
    @Autowired
    private DicionarioTotemRepository dicionarioRepository;

    public InicializacaoResponseDTO getDadosIniciais(Integer hotelId, String idiomaIso) {
        ConfigWhiteLabel config = whiteLabelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new RuntimeException("Configuração visual não encontrada para este hotel."));

        Idioma idioma = idiomaRepository.findByHotelIdAndCodigoIso(hotelId, idiomaIso)
                .orElseThrow(() -> new RuntimeException("Idioma não suportado por este hotel."));

        List<DicionarioTotem> traducoes = dicionarioRepository.findByHotelIdAndIdiomaId(hotelId, idioma.getId());

        java.util.Map<String, String> dicionarioMap = new java.util.HashMap<>();
        for (DicionarioTotem traducao : traducoes) {
            dicionarioMap.put(traducao.getChaveComponente(), traducao.getTextoTraduzido());
        }

        InicializacaoResponseDTO response = new InicializacaoResponseDTO();
        response.setCorPrimariaHex(config.getCorPrimariaHex());
        response.setCorSecundariaHex(config.getCorSecundariaHex());
        response.setLogoPath(config.getLogoPath());
        response.setSplashImagePath(config.getSplashImagePath());
        response.setInfoHotelTexto(config.getInfoHotelTexto());
        response.setDicionario(dicionarioMap);

        return response;
    }
    
    public ReservaHospede buscarReservaEstrita(Integer hotelId, String codigo, String documento) {
        return reservaRepository.findByHotelIdAndCodigoReservaAndDocumentoCpfPassaporte(hotelId, codigo, documento)
                .orElseThrow(() -> new RuntimeException("Dados incorretos. Verifique seu CPF e Código da Reserva."));
    }

    @Transactional
    public ReservaHospede realizarCheckin(Integer hotelId, CheckinRequestDTO request) {
        ReservaHospede reserva = buscarReservaEstrita(hotelId, request.getCodigoReserva(), request.getDocumentoCpfPassaporte());

        if (!"PENDENTE".equals(reserva.getStatusReserva())) {
            throw new RuntimeException("Reserva não está pendente para check-in.");
        }

        if (reserva.getQuarto() == null) {
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
        cartao.setNumeroCartaoChave("KEY-" + java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase());
        cartao.setStatusCartao("ATIVO");
        cartaoChaveRepository.save(cartao);

        return reservaRepository.save(reserva);
    }

    public List<ConsumoEstadia> consultarExtratoSeguro(Integer hotelId, String codigo, String documento) {
        ReservaHospede reserva = buscarReservaEstrita(hotelId, codigo, documento);
        
        if (!"RESERVA_ATIVA".equals(reserva.getStatusReserva())) {
            throw new RuntimeException("Esta reserva não está ativa para check-out.");
        }
        
        return consumoRepository.findByReservaId(reserva.getId());
    }

    @Transactional
    public TransacaoPagamento realizarCheckout(Integer hotelId, CheckoutRequestDTO request) {
        ReservaHospede reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));

        TransacaoPagamento pagamento = new TransacaoPagamento();
        pagamento.setHotel(reserva.getHotel());
        pagamento.setReserva(reserva);
        pagamento.setMetodoPagamento(request.getMetodoPagamento()); // PIX ou CARTAO
        pagamento.setValorPago(request.getValorPago());
        pagamento.setDataHoraPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        List<CartaoChave> cartoes = cartaoChaveRepository.findByReservaId(reserva.getId());
        for (CartaoChave c : cartoes) {
            c.setStatusCartao("INATIVO");
        }
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