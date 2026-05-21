package com.hotelhub.fiap.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotelhub.fiap.entities.ConfigWhiteLabel;
import com.hotelhub.fiap.entities.Hotel;
import com.hotelhub.fiap.entities.dto.HotelDTO;
import com.hotelhub.fiap.entities.dto.ParametrosEsteticosDTO;
import com.hotelhub.fiap.repositories.ConfigWhiteLabelRepository;
import com.hotelhub.fiap.repositories.HotelRepository;
import com.hotelhub.fiap.repositories.UsuarioPortalRepository;

@Service
public class MasterService {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ConfigWhiteLabelRepository whiteLabelRepository;
    @Autowired
    private UsuarioPortalRepository usuarioRepository;
    
    @Transactional
    public Hotel editarHotelBasico(Integer hotelId, HotelDTO dto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel não encontrado."));

        hotel.setNomeEstabelecimento(dto.getNomeEstabelecimento());
        hotel.setEmailSuporteMock(dto.getEmailSuporteMock());
        hotel.setStatusLicenca(dto.getStatusLicenca());

        return hotelRepository.save(hotel);
    }
    
    @Transactional
    public ConfigWhiteLabel atualizarWhiteLabelCompleto(
            Integer hotelId, String corPrimaria, String corSecundaria, String textoInfo,
            org.springframework.web.multipart.MultipartFile logo,
            org.springframework.web.multipart.MultipartFile splash) throws java.io.IOException {

        ConfigWhiteLabel config = whiteLabelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada."));

        // Atualiza os textos/cores
        if (corPrimaria != null) config.setCorPrimariaHex(corPrimaria);
        if (corSecundaria != null) config.setCorSecundariaHex(corSecundaria);
        if (textoInfo != null) config.setInfoHotelTexto(textoInfo);

        // Se a Logo foi enviada, faz o upload e atualiza o caminho
        if (logo != null && !logo.isEmpty()) {
            String caminhoLogo = salvarArquivoFisico(logo, DIR_LOGOS, "logo_" + hotelId);
            config.setLogoPath(caminhoLogo);
        }

        // Se o Banner (Splash) foi enviado, faz o upload e atualiza o caminho
        if (splash != null && !splash.isEmpty()) {
            String caminhoSplash = salvarArquivoFisico(splash, DIR_SPLASH, "splash_" + hotelId);
            config.setSplashImagePath(caminhoSplash);
        }

        return whiteLabelRepository.save(config);
    }
    
    private String salvarArquivoFisico(org.springframework.web.multipart.MultipartFile file, String diretorio, String prefixo) throws java.io.IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".png";
        
        String newFileName = prefixo + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
        
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(diretorio);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        java.nio.file.Files.copy(file.getInputStream(), uploadPath.resolve(newFileName));
        
        // Retorna o caminho que será salvo no banco (ex: "uploads/logos/logo_1_abc123.png")
        return diretorio.replace("flexmedia_", "") + newFileName; 
    }

    // Diretórios base
    private final String DIR_LOGOS = "flexmedia_uploads/logos/";
    private final String DIR_SPLASH = "flexmedia_uploads/splash/";

    @Transactional
    public Hotel criarNovoHotel(HotelDTO dto) {
        // 1. Cria o Hotel
        Hotel hotel = new Hotel();
        hotel.setNomeEstabelecimento(dto.getNomeEstabelecimento());
        hotel.setCnpj(dto.getCnpj());
        hotel.setEmailSuporteMock(dto.getEmailSuporteMock());
        hotel.setStatusLicenca(dto.getStatusLicenca());
        Hotel hotelSalvo = hotelRepository.save(hotel);

        // 2. Cria uma Configuração White Label padrão (cores neutras) para evitar erros
        ConfigWhiteLabel config = new ConfigWhiteLabel();
        config.setHotel(hotelSalvo);
        config.setCorPrimariaHex("#000000");
        config.setCorSecundariaHex("#FFFFFF");
        whiteLabelRepository.save(config);

        return hotelSalvo;
    }

    public ConfigWhiteLabel atualizarParametrosEsteticos(Integer hotelId, ParametrosEsteticosDTO dto) {
        ConfigWhiteLabel config = whiteLabelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada para este hotel."));

        config.setCorPrimariaHex(dto.getCorPrimariaHex());
        config.setCorSecundariaHex(dto.getCorSecundariaHex());
        config.setInfoHotelTexto(dto.getInfoHotelTexto());

        return whiteLabelRepository.save(config);
    }

    public ConfigWhiteLabel atualizarLogoPath(Integer hotelId, String caminhoRelativo) {
        ConfigWhiteLabel config = whiteLabelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada para este hotel."));
        
        config.setLogoPath(caminhoRelativo);
        return whiteLabelRepository.save(config);
    }
    
    public java.util.Map<String, Object> gerarMetricasPlataforma() {
        long totaisHoteis = hotelRepository.count();
        long hoteisAtivos = hotelRepository.countByStatusLicenca("ATIVO");
        long hoteisInativos = hotelRepository.countByStatusLicenca("INATIVO");
        long totalUsuariosPortal = usuarioRepository.count();

        java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("totaisHoteis", totaisHoteis);
        dashboard.put("hoteisAtivos", hoteisAtivos);
        dashboard.put("hoteisInativos", hoteisInativos);
        dashboard.put("totalUsuariosPortal", totalUsuariosPortal);

        return dashboard;
    }
}
