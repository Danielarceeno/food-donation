package com.example.donation.mapper;

import com.example.donation.dto.UserRequestDTO;
import com.example.donation.dto.UserResponseDTO;
import com.example.donation.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDTO dto);

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "avatarUrl", target = "avatarUrl"),
        @Mapping(source = "nomeCompleto", target = "nomeCompleto"),
        @Mapping(source = "email", target = "email"),
        @Mapping(source = "tipo", target = "tipo"),
        @Mapping(source = "cidade", target = "cidade"),
        @Mapping(source = "bairro", target = "bairro"),
        @Mapping(source = "rua", target = "rua"),
        @Mapping(source = "numero", target = "numero"),
        @Mapping(source = "telefone", target = "telefone"),
        @Mapping(source = "cnpj", target = "cnpj")
    })
    UserResponseDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "avatarUrl", ignore = true)
    })
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User user);
}
