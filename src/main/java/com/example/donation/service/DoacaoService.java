package com.example.donation.service;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.entity.Doacao;
import com.example.donation.entity.StatusDoacao;
import com.example.donation.entity.User;
import com.example.donation.mapper.DoacaoMapper;
import com.example.donation.repository.DoacaoRepository;
import com.example.donation.repository.ItemSolicitadoRepository;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class DoacaoService {

    private final DoacaoRepository doacaoRepo;
    private final UserRepository userRepo;
    private final ItemSolicitadoRepository itemRepo;
    private final DoacaoMapper mapper;

    public DoacaoResponseDTO create(DoacaoRequestDTO dto, String emailDoador) {
        User doador = userRepo.findByEmail(emailDoador)
                .orElseThrow(() -> new NoSuchElementException("Doador não encontrado: " + emailDoador));

        var item = itemRepo.findById(dto.getItemSolicitadoId())
                .orElseThrow(() -> new NoSuchElementException(
                        "ItemSolicitado não encontrado: " + dto.getItemSolicitadoId()));

        Doacao entidade = Doacao.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(dto.getCategoria())
                .status(StatusDoacao.PENDENTE)
                .usuario(doador)
                .itemSolicitado(item)
                .build();

        return mapper.toDTO(doacaoRepo.save(entidade));
    }

    @Transactional(readOnly = true)
    public Page<DoacaoResponseDTO> listByDoador(String email, Pageable pageable) {
        User doador = userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Doador não encontrado: " + email));

        return doacaoRepo.findByUsuarioId(doador.getId(), pageable)
                .map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<DoacaoResponseDTO> listAll(Pageable pageable) {
        return doacaoRepo.findAll(pageable).map(mapper::toDTO);
    }

    public DoacaoResponseDTO confirm(Long id, String emailDoador) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doação não encontrada: " + id));

        if (!d.getUsuario().getEmail().equals(emailDoador))
            throw new SecurityException("Apenas o doador pode confirmar");

        d.setStatus(StatusDoacao.CONFIRMADA);
        return mapper.toDTO(doacaoRepo.save(d));
    }

    public void delete(Long id, String emailDoador) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doação não encontrada: " + id));

        boolean isOwner = d.getUsuario().getEmail().equals(emailDoador);
        boolean isAdmin = userRepo.findByEmail(emailDoador)
                .map(u -> u.getTipo().name().equals("ADMIN"))
                .orElse(false);

        if (!isOwner && !isAdmin)
            throw new SecurityException("Você não pode excluir esta doação");

        doacaoRepo.delete(d);
    }
}
