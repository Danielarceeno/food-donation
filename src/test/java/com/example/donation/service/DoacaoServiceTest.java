package com.example.donation.service;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.entity.Categoria;
import com.example.donation.entity.Doacao;
import com.example.donation.entity.ItemSolicitado;
import com.example.donation.entity.StatusDoacao;
import com.example.donation.entity.User;
import com.example.donation.mapper.DoacaoMapper;
import com.example.donation.repository.DoacaoRepository;
import com.example.donation.repository.ItemSolicitadoRepository;
import com.example.donation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoacaoServiceTest {

    @Mock
    private DoacaoRepository doacaoRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ItemSolicitadoRepository itemRepo;

    @Mock
    private DoacaoMapper mapper;

    @InjectMocks
    private DoacaoService doacaoService;

    private User mockUser;
    private ItemSolicitado mockItem;
    private DoacaoRequestDTO requestDTO;
    private Doacao mockDoacao;
    private DoacaoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = User.builder()
            .id(1L)
            .email("doador@teste.com")
            .nomeCompleto("Doador Teste")
            .build();

        mockItem = ItemSolicitado.builder()
            .id(2L)
            .titulo("Item Teste")
            .build();

        requestDTO = new DoacaoRequestDTO();
        requestDTO.setTitulo("Doação Teste");
        requestDTO.setDescricao("Descrição Teste");
        requestDTO.setCategoria(Categoria.VESTUARIO);
        requestDTO.setItemSolicitadoId(2L);

        mockDoacao = Doacao.builder()
            .id(10L)
            .titulo(requestDTO.getTitulo())
            .descricao(requestDTO.getDescricao())
            .categoria(requestDTO.getCategoria())
            .status(StatusDoacao.PENDENTE)
            .usuario(mockUser)
            .itemSolicitado(mockItem)
            .build();

        responseDTO = DoacaoResponseDTO.builder()
            .id(10L)
            .titulo("Doação Teste")
            .descricao("Descrição Teste")
            .categoria(Categoria.VESTUARIO)
            .status(StatusDoacao.PENDENTE)
            .doadorEmail("doador@teste.com")
            .build();
    }

    @Test
    void create_deveCriarEDevolverDoacaoResponseDTO() {
        when(userRepo.findByEmail("doador@teste.com")).thenReturn(Optional.of(mockUser));
        when(itemRepo.findById(2L)).thenReturn(Optional.of(mockItem));
        when(doacaoRepo.save(any(Doacao.class))).thenReturn(mockDoacao);
        when(mapper.toDTO(any(Doacao.class))).thenReturn(responseDTO);

        DoacaoResponseDTO resultado = doacaoService.create(requestDTO, "doador@teste.com");

        assertThat(resultado)
            .isNotNull()
            .extracting(DoacaoResponseDTO::getId, DoacaoResponseDTO::getTitulo, DoacaoResponseDTO::getDoadorEmail)
            .containsExactly(10L, "Doação Teste", "doador@teste.com");

        // verifica se status está correto
        assertThat(resultado.getStatus()).isEqualTo(StatusDoacao.PENDENTE);

        // verifica se o método save foi chamado corretamente
        ArgumentCaptor<Doacao> captor = ArgumentCaptor.forClass(Doacao.class);
        verify(doacaoRepo).save(captor.capture());
        Doacao salvo = captor.getValue();
        assertThat(salvo.getTitulo()).isEqualTo(requestDTO.getTitulo());
        assertThat(salvo.getUsuario().getEmail()).isEqualTo("doador@teste.com");
    }

    @Test
    void create_quandoDoadorNaoEncontrado_deveLancarExcecao() {
        when(userRepo.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doacaoService.create(requestDTO, "inexistente@teste.com"))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("Doador não encontrado");
    }

    @Test
    void create_quandoItemSolicitadoNaoEncontrado_deveLancarExcecao() {
        when(userRepo.findByEmail("doador@teste.com")).thenReturn(Optional.of(mockUser));
        when(itemRepo.findById(999L)).thenReturn(Optional.empty());

        DoacaoRequestDTO req = new DoacaoRequestDTO();
        req.setTitulo("Doação");
        req.setDescricao("Desc");
        req.setCategoria(Categoria.ALIMENTO);
        req.setItemSolicitadoId(999L);

        assertThatThrownBy(() -> doacaoService.create(req, "doador@teste.com"))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("ItemSolicitado não encontrado");
    }
}
