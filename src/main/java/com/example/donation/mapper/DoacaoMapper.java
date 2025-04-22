package com.example.donation.mapper;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.entity.Doacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoacaoMapper {
    Doacao toEntity(DoacaoRequestDTO dto);
    DoacaoResponseDTO toDTO(Doacao entity);
}
