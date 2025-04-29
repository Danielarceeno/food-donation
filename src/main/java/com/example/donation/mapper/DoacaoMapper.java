package com.example.donation.mapper;

import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.entity.Doacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoacaoMapper {

    @Mapping(source = "usuario.email", target = "doadorEmail")
    DoacaoResponseDTO toDTO(Doacao entity);
}
