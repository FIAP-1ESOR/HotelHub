package com.hotelhub.fiap.entities.dto;

import lombok.Data;
import java.util.Map;

@Data
public class InicializacaoResponseDTO {
    private String corPrimariaHex;
    private String corSecundariaHex;
    private String logoPath;
    private String splashImagePath;
    private String infoHotelTexto;
    
    private Map<String, String> dicionario; 
}