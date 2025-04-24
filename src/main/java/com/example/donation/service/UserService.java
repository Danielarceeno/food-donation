package com.example.donation.service;

import com.example.donation.dto.UserRequestDTO;
import com.example.donation.dto.UserResponseDTO;
import com.example.donation.entity.User;
import com.example.donation.mapper.UserMapper;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return userMapper.toDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        userMapper.updateEntityFromDto(dto, user);
        if(dto.getSenha() != null && !dto.getSenha().isEmpty()){
            user.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    public UserResponseDTO findByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado: " + email));
        return userMapper.toDTO(u);
    }
    public UserResponseDTO updateProfile(String email, UserRequestDTO dto) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado: " + email));

        // Atualiza campos permitidos
        u.setNomeCompleto(dto.getNomeCompleto());
        u.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            u.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        u.setCidade(dto.getCidade());
        u.setBairro(dto.getBairro());
        u.setRua(dto.getRua());
        u.setNumero(dto.getNumero());
        u.setTelefone(dto.getTelefone());
        // só instituições vão ter CNPJ no DTO; em DOADOR dto.getCnpj() pode ser nulo
        u.setCnpj(dto.getCnpj());

        User updated = userRepository.save(u);
        return userMapper.toDTO(updated);
    }
}
