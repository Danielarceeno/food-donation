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
            @Mapping(target = "cnpj", source = "cnpj")
    })
    UserResponseDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "avatarUrl", ignore = true)
    })
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User user);
}
