package com.example.donation.service;

import com.example.donation.dto.UserRequestDTO;
import com.example.donation.dto.UserResponseDTO;
import com.example.donation.entity.User;
import com.example.donation.mapper.UserMapper;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
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
        u.setCnpj(dto.getCnpj());

        User updated = userRepository.save(u);
        return userMapper.toDTO(updated);
    }

    /**
     * Faz upload de avatar e atualiza o campo avatarUrl do usuário.
     */
    public UserResponseDTO uploadAvatar(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        String filename = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
        Files.createDirectories(targetLocation.getParent());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        String avatarUrl = "/uploads/" + filename;
        user.setAvatarUrl(avatarUrl);
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }
}
