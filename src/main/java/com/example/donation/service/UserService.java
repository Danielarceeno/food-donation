package com.example.donation.service;

import com.example.donation.dto.UserRequestDTO;
import com.example.donation.dto.UserResponseDTO;
import com.example.donation.entity.User;
import com.example.donation.entity.UserType;
import com.example.donation.exception.EmailAlreadyExistsException;
import com.example.donation.exception.EmailNotFoundException;
import com.example.donation.exception.FileUploadException;
import com.example.donation.exception.UserNotFoundException;
import com.example.donation.mapper.UserMapper;
import com.example.donation.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
        "image/jpeg",
        "image/png",
        "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
        ".jpg", ".jpeg", ".png", ".webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB em bytes

    @Value("${file.upload-dir}")
    private String uploadDir;

    public UserResponseDTO createUser(@Valid UserRequestDTO dto) {
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {
            throw new EmailAlreadyExistsException(dto.getEmail());
        });
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (dto.getTipo() == UserType.ADMIN && !isAdmin) {
            throw new SecurityException("Apenas ADMIN pode criar usuário ADMIN.");
        }

        User user = userMapper.toEntity(dto);
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        if (!user.getEmail().equals(dto.getEmail())) {
            userRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
                throw new EmailAlreadyExistsException(dto.getEmail());
            });
        }
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
            .orElseThrow(() -> new EmailNotFoundException(email));
        return userMapper.toDTO(u);
    }

    public UserResponseDTO updateProfile(String email, UserRequestDTO dto) {
        User u = userRepository.findByEmail(email)
            .orElseThrow(() -> new EmailNotFoundException(email));

        if (!email.equals(dto.getEmail())) {
            userRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
                throw new EmailAlreadyExistsException(dto.getEmail());
            });
        }

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
        validateFile(file);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EmailNotFoundException(email));

        String fileExtension = extractSafeExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + fileExtension;

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetLocation = uploadPath.resolve(filename);

        Files.createDirectories(uploadPath);

        // 4. Copia o arquivo com segurança
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 5. Atualiza URL do avatar no usuário
        String avatarUrl = "/uploads/" + filename;
        user.setAvatarUrl(avatarUrl);

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Valida o arquivo enviado quanto a tipo MIME, tamanho e extensão
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("Arquivo não pode estar vazio");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException(
                "Arquivo muito grande. Tamanho máximo permitido: 5MB"
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new FileUploadException(
                "Tipo de arquivo não permitido. Permitidos: JPG, PNG, WEBP"
            );
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidExtension(originalFilename)) {
            throw new FileUploadException(
                "Extensão de arquivo não permitida. Permitidas: .jpg, .jpeg, .png, .webp"
            );
        }

        if (!extensionMatchesMimeType(originalFilename, contentType)) {
            throw new FileUploadException(
                "Extensão do arquivo não corresponde ao tipo de conteúdo detectado"
            );
        }
    }

    private String extractSafeExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg"; // padrão seguro
        }

        String extension = originalFilename.substring(
            originalFilename.lastIndexOf(".")
        ).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return ".jpg"; // fallback seguro
        }

        return extension;
    }

    private boolean hasValidExtension(String filename) {
        return ALLOWED_EXTENSIONS.stream()
            .anyMatch(ext -> filename.toLowerCase().endsWith(ext));
    }

    private boolean extensionMatchesMimeType(String filename, String mimeType) {
        if (filename == null || mimeType == null) return false;

        String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();

        return switch (mimeType.toLowerCase()) {
            case "image/jpeg" -> ext.equals(".jpg") || ext.equals(".jpeg");
            case "image/png" -> ext.equals(".png");
            case "image/webp" -> ext.equals(".webp");
            default -> false;
        };
    }
}
