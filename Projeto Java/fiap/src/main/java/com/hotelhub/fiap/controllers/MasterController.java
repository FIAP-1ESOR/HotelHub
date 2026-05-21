package com.hotelhub.fiap.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hotelhub.fiap.entities.ConfigWhiteLabel;
import com.hotelhub.fiap.entities.Hotel;
import com.hotelhub.fiap.entities.dto.HotelDTO;
import com.hotelhub.fiap.entities.dto.ParametrosEsteticosDTO;
import com.hotelhub.fiap.services.MasterService;

@RestController
@RequestMapping("/master")
public class MasterController {

    @Autowired
    private MasterService masterService;

    // Diretório base para os uploads das logos
    private final String UPLOAD_DIR = "flexmedia_uploads/logos/";

    // 1. Criar novo estabelecimento (Inicia a jornada de um novo cliente)
    // POST http://localhost:8080/master/hoteis
    @PostMapping("/hoteis")
    public ResponseEntity<?> criarHotel(@RequestBody HotelDTO hotelDTO) {
        try {
            Hotel novoHotel = masterService.criarNovoHotel(hotelDTO);
            return ResponseEntity.ok(novoHotel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar hotel: " + e.getMessage());
        }
    }
    
    @PutMapping("/hoteis/{hotelId}")
    public ResponseEntity<?> editarHotel(@PathVariable Integer hotelId, @RequestBody HotelDTO dto) {
        try {
            Hotel hotel = masterService.editarHotelBasico(hotelId, dto);
            return ResponseEntity.ok(hotel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao editar hotel: " + e.getMessage());
        }
    }

    // 2. Parametrização Estética Básica (Cores e Textos via JSON)
    // PUT http://localhost:8080/master/hoteis/{hotelId}/white-label
    @PutMapping("/hoteis/{hotelId}/white-label")
    public ResponseEntity<?> atualizarCoresTextos(@PathVariable Integer hotelId, 
                                                  @RequestBody ParametrosEsteticosDTO dto) {
        try {
            ConfigWhiteLabel configAtualizada = masterService.atualizarParametrosEsteticos(hotelId, dto);
            return ResponseEntity.ok(configAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar estética: " + e.getMessage());
        }
    }
    
    @PostMapping("/hoteis/{hotelId}/white-label-completo")
    public ResponseEntity<?> atualizarWhiteLabelCompleto(
            @PathVariable Integer hotelId,
            @RequestParam(required = false) String corPrimariaHex,
            @RequestParam(required = false) String corSecundariaHex,
            @RequestParam(required = false) String infoHotelTexto,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestParam(value = "splashFile", required = false) MultipartFile splashFile) {
        
        try {
            ConfigWhiteLabel config = masterService.atualizarWhiteLabelCompleto(
                    hotelId, corPrimariaHex, corSecundariaHex, infoHotelTexto, logoFile, splashFile);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro na sincronização das mídias: " + e.getMessage());
        }
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboardMaster() {
        try {
            return ResponseEntity.ok(masterService.gerarMetricasPlataforma());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao carregar métricas: " + e.getMessage());
        }
    }

    // 3. Upload de Logomarca (White Label Físico)
    // POST http://localhost:8080/master/hoteis/{hotelId}/white-label/logo
    // Multipart Form-data: Key = 'file'
    @PostMapping("/hoteis/{hotelId}/white-label/logo")
    public ResponseEntity<?> uploadLogoHotel(@PathVariable Integer hotelId, 
                                             @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("O arquivo enviado está vazio.");
            }

            // A. Sanitização: Criar um nome único e seguro para o arquivo
            String originalFilename = file.getOriginalFilename();
            // Pega a extensão (.png, .jpg, etc)
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".png"; // Fallback de segurança
            
            String newFileName = "hotel_" + hotelId + "_" + UUID.randomUUID().toString() + extension;

            // B. Preparação do Diretório Físico (se a pasta "flexmedia_uploads/logos/" não existir, o Java cria)
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // C. Escrita (I/O) - Salva o arquivo fisicamente no SSD/HD do servidor
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);

            // D. Indexação Relacional - Salva apenas o caminho textual no H2
            String caminhoRelativoBanco = "uploads/logos/" + newFileName;
            ConfigWhiteLabel configAtualizada = masterService.atualizarLogoPath(hotelId, caminhoRelativoBanco);

            // Retorna o objeto atualizado (contendo as cores e o novo caminho da logo)
            return ResponseEntity.ok(configAtualizada);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Falha ao salvar a imagem fisicamente: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro na indexação da logo: " + e.getMessage());
        }
    }
}