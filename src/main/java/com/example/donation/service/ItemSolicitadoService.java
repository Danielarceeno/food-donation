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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Join;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemSolicitadoService {

    private final ItemSolicitadoRepository repo;
    private final UserRepository userRepo;
    private final DoacaoRepository doacaoRepo;

    public ItemSolicitadoResponseDTO create(ItemSolicitadoRequestDTO dto, String emailInstituicao) {
        User inst = userRepo.findByEmail(emailInstituicao)
            .orElseThrow(() -> new UsernameNotFoundException("Instituição não encontrada: " + emailInstituicao));
        ItemSolicitado item = new ItemSolicitado();
        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(Categoria.valueOf(dto.getCategoria()));
        item.setSolicitante(inst);
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
     * Busca paginada e filtrada por vários parâmetros.
     */
    public Page<ItemSolicitadoResponseDTO> search(
        String title,
        Categoria categoria,
        LocalDateTime dataFrom,
        LocalDateTime dataTo,
        String cidade,
        Pageable pageable
    ) {
        Specification<ItemSolicitado> spec = Specification.where(null);
        if (title != null && !title.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("titulo")), "%" + title.toLowerCase() + "%")
            );
        }
        if (categoria != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("categoria"), categoria)
            );
        }
        if (dataFrom != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("dataCriacao"), dataFrom)
            );
        }
        if (dataTo != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("dataCriacao"), dataTo)
            );
        }
        if (cidade != null && !cidade.isBlank()) {
            spec = spec.and((root, query, cb) -> {
                Join<ItemSolicitado, User> join = root.join("solicitante");
                return cb.equal(cb.lower(join.get("cidade")), cidade.toLowerCase());
            });
        }
        return repo.findAll(spec, pageable)
            .map(this::toDTO);
    }

    public ItemSolicitadoResponseDTO getById(Long id) {
        ItemSolicitado item = repo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));
        return toDTO(item);
    }

    public ItemSolicitadoResponseDTO update(Long id, ItemSolicitadoRequestDTO dto, String emailInstituicao) {
        ItemSolicitado item = repo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));
        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(Categoria.valueOf(dto.getCategoria()));
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

    public void delete(Long id, String emailInstituicao) {
        ItemSolicitado item = repo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));
        if (!item.getSolicitante().getEmail().equals(emailInstituicao)) {
            throw new SecurityException("Você não pode excluir esse item");
        }
        repo.delete(item);
    }

    private ItemSolicitadoResponseDTO toDTO(ItemSolicitado i) {
        long count = doacaoRepo.countByItemSolicitadoId(i.getId());
        return ItemSolicitadoResponseDTO.builder()
            .id(i.getId())
            .titulo(i.getTitulo())
            .descricao(i.getDescricao())
            .categoria(i.getCategoria().name())
            .solicitanteNome(i.getSolicitante().getNomeCompleto())
            .dataCriacao(i.getDataCriacao())
            .pontosArrecadacao(
                i.getPontosArrecadacao().stream()
                    .map(PontoArrecadacao::getEndereco)
                    .collect(Collectors.toList())
            )
            .doacoesRecebidas(count)
            .build();
    }

    public boolean isOwner(Long itemId, String userEmail) {
        ItemSolicitado item = repo.findById(itemId)
            .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + itemId));
        return item.getSolicitante().getEmail().equals(userEmail);
    }
}
