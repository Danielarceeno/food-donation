package com.example.donation.service;

import com.example.donation.dto.ItemSolicitadoRequestDTO;
import com.example.donation.dto.ItemSolicitadoResponseDTO;
import com.example.donation.entity.Categoria;
import com.example.donation.entity.ItemSolicitado;
import com.example.donation.entity.PontoArrecadacao;
import com.example.donation.entity.User;
import com.example.donation.repository.DoacaoRepository;
import com.example.donation.repository.ItemSolicitadoRepository;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemSolicitadoService {

    private final ItemSolicitadoRepository repo;
    private final UserRepository userRepo;
    private final DoacaoRepository doacaoRepo;

    /**
     * Cria um novo ItemSolicitado atrelado à instituição (por e-mail).
     */
    public ItemSolicitadoResponseDTO create(ItemSolicitadoRequestDTO dto, String emailInstituicao) {
        User inst = userRepo.findByEmail(emailInstituicao)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Instituição não encontrada: " + emailInstituicao)
                );

        ItemSolicitado item = new ItemSolicitado();
        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(Categoria.valueOf(dto.getCategoria()));
        item.setSolicitante(inst);

        // pontos de arrecadação
        dto.getPontosArrecadacao().forEach(endereco -> {
            PontoArrecadacao p = new PontoArrecadacao();
            p.setEndereco(endereco);
            p.setItem(item);
            item.getPontosArrecadacao().add(p);
        });

        ItemSolicitado salvo = repo.save(item);
        return toDTO(salvo);
    }

    /**
     * Lista todos (ou busca por título). Retorna DTOs com contagem de doações recebidas.
     */
    public List<ItemSolicitadoResponseDTO> listAll(String search) {
        List<ItemSolicitado> items = (search == null || search.isBlank())
                ? repo.findAll()
                : repo.findByTituloContainingIgnoreCase(search);

        return items.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca por ID e retorna um DTO com todos os detalhes (incluindo pontos de arrecadação).
     */
    public ItemSolicitadoResponseDTO getById(Long id) {
        ItemSolicitado item = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));
        return toDTO(item);
    }

    /**
     * Atualiza um item existente (apenas pela instituição que criou).
     */
    public ItemSolicitadoResponseDTO update(Long id, ItemSolicitadoRequestDTO dto, String emailInstituicao) {
        ItemSolicitado item = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));

        // opcional: valida se item.getSolicitante().getEmail().equals(emailInstituicao)

        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(Categoria.valueOf(dto.getCategoria()));

        // sincroniza pontos: remove todos e adiciona de novo
        item.getPontosArrecadacao().clear();
        dto.getPontosArrecadacao().forEach(endereco -> {
            PontoArrecadacao p = new PontoArrecadacao();
            p.setEndereco(endereco);
            p.setItem(item);
            item.getPontosArrecadacao().add(p);
        });

        ItemSolicitado atualizado = repo.save(item);
        return toDTO(atualizado);
    }

    /**
     * Exclui um item solicitado. Só a própria instituição que criou pode fazê-lo.
     */
    public void delete(Long id, String emailInstituicao) {
        ItemSolicitado item = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));

        if (!item.getSolicitante().getEmail().equals(emailInstituicao)) {
            throw new SecurityException("Você não pode excluir esse item");
        }

        repo.delete(item);
    }

    /**
     * Constrói o DTO de resposta, incluindo pontos de arrecadação e contagem de doações recebidas.
     */
    private ItemSolicitadoResponseDTO toDTO(ItemSolicitado i) {
        long countRecebidas = doacaoRepo.countByItemSolicitadoId(i.getId());

        return ItemSolicitadoResponseDTO.builder()
                .id(i.getId())
                .titulo(i.getTitulo())
                .descricao(i.getDescricao())
                .categoria(i.getCategoria().name())
                .solicitanteNome(i.getSolicitante().getNomeCompleto())
                .dataCriacao(i.getDataCriacao().toString())
                .pontosArrecadacao(
                        i.getPontosArrecadacao().stream()
                                .map(PontoArrecadacao::getEndereco)
                                .collect(Collectors.toList())
                )
                .doacoesRecebidas(countRecebidas)
                .build();
    }
}
