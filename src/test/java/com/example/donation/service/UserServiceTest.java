package com.example.donation.service;

import com.example.donation.dto.UserRequestDTO;
import com.example.donation.dto.UserResponseDTO;
import com.example.donation.entity.User;
import com.example.donation.entity.UserType;
import com.example.donation.mapper.UserMapper;
import com.example.donation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private User userEntity;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNomeCompleto("João Silva");
        userRequestDTO.setEmail("joao@example.com");
        userRequestDTO.setSenha("senha123");
        userRequestDTO.setTipo(UserType.DOADOR);
        userRequestDTO.setCidade("CidadeX");

        userEntity = User.builder()
            .id(1L)
            .nomeCompleto("João Silva")
            .email("joao@example.com")
            .senha("senha123")
            .tipo(UserType.DOADOR)
            .cidade("CidadeX")
            .build();

        userResponseDTO = UserResponseDTO.builder()
            .id(1L)
            .nomeCompleto("João Silva")
            .email("joao@example.com")
            .tipo(UserType.DOADOR)
            .cidade("CidadeX")
            .build();
    }

    @Test
    void createUser_DeveSalvarUsuarioERetornarDTO() {
        // Mock contexto de segurança (sem role ADMIN)
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getAuthorities()).thenReturn(List.of());
        SecurityContextHolder.setContext(context);

        when(userMapper.toEntity(userRequestDTO)).thenReturn(userEntity);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaHASH");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("joao@example.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void createUser_DeveRecusarADMINSeNaoForAdminAutenticado() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getAuthorities()).thenReturn(List.of());
        SecurityContextHolder.setContext(context);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setNomeCompleto("Admin");
        dto.setEmail("admin@example.com");
        dto.setSenha("admin123");
        dto.setTipo(UserType.ADMIN);

        assertThatThrownBy(() -> userService.createUser(dto))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("Apenas ADMIN pode criar usuário ADMIN.");
    }

    @Test
    void getUserById_DeveRetornarUsuarioQuandoExiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_UsuarioNaoEncontrado_DeveLancarExcecao() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void updateUser_DeveAtualizarCamposERetornarDTO() {
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setNomeCompleto("Novo Nome");
        updateDto.setEmail("novo@example.com");
        updateDto.setSenha("novaSenha");
        updateDto.setTipo(UserType.DOADOR);
        updateDto.setCidade("NovaCidade");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        doNothing().when(userMapper).updateEntityFromDto(any(), any(User.class));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaHASH");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateUser(1L, updateDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(userEntity);
        verify(userMapper).updateEntityFromDto(updateDto, userEntity);
        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    void findByEmail_DeveRetornarUsuarioQuandoExiste() {
        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findByEmail("joao@example.com");
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    void findByEmail_UsuarioNaoExiste_DeveLancarExcecao() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByEmail("notfound@example.com"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Email não encontrado");
    }

    @Test
    void updateProfile_DeveAtualizarCamposDoUsuarioERetornarDTO() {
        UserRequestDTO profileDto = new UserRequestDTO();
        profileDto.setNomeCompleto("Novo Nome");
        profileDto.setEmail("novoemail@example.com");
        profileDto.setSenha("novasenha");
        profileDto.setTipo(UserType.DOADOR);
        profileDto.setCidade("NovaCidade");

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.encode("novasenha")).thenReturn("novasenhaHASH");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateProfile("joao@example.com", profileDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(userEntity);
        assertThat(userEntity.getNomeCompleto()).isEqualTo("Novo Nome");
        assertThat(userEntity.getEmail()).isEqualTo("novoemail@example.com");
        assertThat(userEntity.getSenha()).isEqualTo("novasenhaHASH");
    }
}
