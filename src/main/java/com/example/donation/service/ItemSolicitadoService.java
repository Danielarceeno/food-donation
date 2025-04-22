package com.example.donation.service;

import com.example.donation.dto.ItemSolicitadoRequestDTO;
import com.example.donation.dto.ItemSolicitadoResponseDTO;
import com.example.donation.entity.Categoria;
import com.example.donation.entity.ItemSolicitado;
import com.example.donation.entity.User;
import com.example.donation.repository.ItemSolicitadoRepository;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemSolicitadoService {

    private final ItemSolicitadoRepository repo;
    private final UserRepository userRepo;

    public ItemSolicitadoResponseDTO create(ItemSolicitadoRequestDTO dto, String emailInstituicao) {
        User inst = userRepo.findByEmail(emailInstituicao)
                .orElseThrow(() -> new UsernameNotFoundException("Instituição não encontrada: " + emailInstituicao));

        ItemSolicitado item = ItemSolicitado.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(Categoria.valueOf(dto.getCategoria()))
                .solicitante(inst)
                .build();

        ItemSolicitado saved = repo.save(item);
        return toDTO(saved);
    }

    public List<ItemSolicitadoResponseDTO> listAll(String search) {
        List<ItemSolicitado> items = (search == null || search.isBlank())
                ? repo.findAll()
                : repo.findByTituloContainingIgnoreCase(search);

        return items.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ItemSolicitadoResponseDTO toDTO(ItemSolicitado i) {
        return ItemSolicitadoResponseDTO.builder()
                .id(i.getId())
                .titulo(i.getTitulo())
                .descricao(i.getDescricao())
                .categoria(i.getCategoria().name())
                .solicitanteNome(i.getSolicitante().getNomeCompleto())
                .dataCriacao(i.getDataCriacao().toString())
                .build();
    }
}