package com.example.donation.service;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.entity.Doacao;
import com.example.donation.entity.ItemSolicitado;
import com.example.donation.entity.User;
import com.example.donation.mapper.DoacaoMapper;
import com.example.donation.repository.DoacaoRepository;
import com.example.donation.repository.ItemSolicitadoRepository;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DoacaoService {

    private final DoacaoRepository doacaoRepo;
    private final UserRepository userRepo;
    private final ItemSolicitadoRepository itemRepo;
    private final DoacaoMapper mapper;

    public DoacaoResponseDTO create(DoacaoRequestDTO dto, String emailDoador) {
        User doador = userRepo.findByEmail(emailDoador)
                .orElseThrow(() -> new RuntimeException("Doador não encontrado: " + emailDoador));

        ItemSolicitado item = itemRepo.findById(dto.getItemSolicitadoId())
                .orElseThrow(() -> new RuntimeException("ItemSolicitado não encontrado: " + dto.getItemSolicitadoId()));

        Doacao entidade = Doacao.builder()
                .usuario(doador)
                .itemSolicitado(item)
                .descricao(dto.getComentario())
                .status("PENDENTE")
                .build();

        Doacao salvo = doacaoRepo.save(entidade);
        return mapper.toDTO(salvo);
    }
}




