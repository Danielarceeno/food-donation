package com.example.donation.service;

import com.example.donation.dto.ItemSolicitadoRequestDTO;
import com.example.donation.entity.Categoria;
import com.example.donation.entity.ItemSolicitado;
import com.example.donation.entity.PontoArrecadacao;
import com.example.donation.entity.User;
import com.example.donation.repository.DoacaoRepository;
import com.example.donation.repository.ItemSolicitadoRepository;
import com.example.donation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemSolicitadoServiceTest {

    @Mock
    private ItemSolicitadoRepository itemRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private DoacaoRepository doacaoRepo;

    @InjectMocks
    private ItemSolicitadoService service;

    private User instituicao;
    private ItemSolicitadoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        instituicao = User.builder()
            .id(1L)
            .email("instituicao@teste.com")
            .nomeCompleto("Instituição Teste")
            .build();

        requestDTO = new ItemSolicitadoRequestDTO();
        requestDTO.setTitulo("Campanha de Roupas");
        requestDTO.setDescricao("Precisamos de roupas de inverno");
        requestDTO.setCategoria(Categoria.VESTUARIO.name());
        requestDTO.setPontosArrecadacao(List.of("Rua A, 123", "Rua B, 456"));
    }

    @Test
    void create_DeveCriarNovoItemSolicitado() {
        when(userRepo.findByEmail("instituicao@teste.com")).thenReturn(Optional.of(instituicao));
        when(itemRepo.save(any(ItemSolicitado.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(requestDTO, "instituicao@teste.com");

        // Verifica que campos principais estão corretos
        assertThat(response.getTitulo()).isEqualTo(requestDTO.getTitulo());
        assertThat(response.getDescricao()).isEqualTo(requestDTO.getDescricao());
        assertThat(response.getCategoria()).isEqualTo(Categoria.VESTUARIO.name());
        assertThat(response.getSolicitanteNome()).isEqualTo("Instituição Teste");
        assertThat(response.getPontosArrecadacao()).containsExactlyInAnyOrder("Rua A, 123", "Rua B, 456");

        // Verifica que save foi chamado e dados do objeto salvo
        ArgumentCaptor<ItemSolicitado> captor = ArgumentCaptor.forClass(ItemSolicitado.class);
        verify(itemRepo).save(captor.capture());
        ItemSolicitado salvo = captor.getValue();
        assertThat(salvo.getSolicitante().getEmail()).isEqualTo("instituicao@teste.com");
        assertThat(salvo.getPontosArrecadacao()).hasSize(2);
        assertThat(salvo.getPontosArrecadacao())
            .extracting(PontoArrecadacao::getEndereco)
            .containsExactlyInAnyOrder("Rua A, 123", "Rua B, 456");
    }

    @Test
    void create_quandoInstituicaoNaoExiste_deveLancarExcecao() {
        when(userRepo.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        ItemSolicitadoRequestDTO req = new ItemSolicitadoRequestDTO();
        req.setTitulo("Item");
        req.setDescricao("Desc");
        req.setCategoria(Categoria.ALIMENTO.name());
        req.setPontosArrecadacao(List.of("Rua C, 789"));

        assertThatThrownBy(() -> service.create(req, "inexistente@teste.com"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Instituição não encontrada");
    }
}
