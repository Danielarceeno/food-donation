package com.example.donation.service;

import com.example.donation.dto.ItemSolicitadoRequestDTO;
import com.example.donation.dto.ItemSolicitadoResponseDTO;
import com.example.donation.entity.*;
import com.example.donation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class ItemSolicitadoService {

    private final ItemSolicitadoRepository repo;
    private final UserRepository userRepo;

    public ItemSolicitadoResponseDTO create(ItemSolicitadoRequestDTO dto, String email) {
        User inst = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Instituição não encontrada: " + email));

        ItemSolicitado item = new ItemSolicitado();
        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(Categoria.valueOf(dto.getCategoria()));
        item.setSolicitante(inst);

        // monta lista de pontos
        dto.getPontosArrecadacao().forEach(end -> {
            item.getPontosArrecadacao()
                    .add(new PontoArrecadacao(null, end, item));
        });

        ItemSolicitado saved = repo.save(item);
        return toDTO(saved);
    }

    public ItemSolicitadoResponseDTO getById(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));
    }

    public ItemSolicitadoResponseDTO update(Long id, ItemSolicitadoRequestDTO dto, String email) {
        ItemSolicitado item = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + id));

        // opcional: validar se item.getSolicitante().getEmail().equals(email)

        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(Categoria.valueOf(dto.getCategoria()));

        // sincroniza pontos: remove antigos e adiciona novos
        item.getPontosArrecadacao().clear();
        dto.getPontosArrecadacao().forEach(end -> {
            item.getPontosArrecadacao().add(new PontoArrecadacao(null, end, item));
        });

        ItemSolicitado updated = repo.save(item);
        return toDTO(updated);
    }

    public List<ItemSolicitadoResponseDTO> listAll(String search) {
        List<ItemSolicitado> items = (search == null || search.isBlank())
                ? repo.findAll()
                : repo.findByTituloContainingIgnoreCase(search);

        return items.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ItemSolicitadoResponseDTO toDTO(ItemSolicitado i) {
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
                .build();
    }
}
