package com.example.donation.service;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.entity.Doacao;
import com.example.donation.entity.User;
import com.example.donation.mapper.DoacaoMapper;
import com.example.donation.repository.DoacaoRepository;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final UserRepository userRepository;
    private final DoacaoMapper doacaoMapper;

    public DoacaoResponseDTO create(DoacaoRequestDTO dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Doacao doacao = doacaoMapper.toEntity(dto);
        doacao.setUsuario(user);
        doacao.setStatus("PENDENTE");

        return doacaoMapper.toDTO(doacaoRepository.save(doacao));
    }

    public List<DoacaoResponseDTO> getAll() {
        return doacaoRepository.findAll().stream()
                .map(doacaoMapper::toDTO)
                .toList();
    }

    public DoacaoResponseDTO getById(Long id) {
        return doacaoRepository.findById(id)
                .map(doacaoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
    }

    public DoacaoResponseDTO update(Long id, DoacaoRequestDTO dto) {
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        doacao.setTitulo(dto.getTitulo());
        doacao.setDescricao(dto.getDescricao());
        doacao.setCategoria(dto.getCategoria());
        return doacaoMapper.toDTO(doacaoRepository.save(doacao));
    }

    public void delete(Long id) {
        doacaoRepository.deleteById(id);
    }
    public Long countByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        return doacaoRepository.countByUsuarioId(u.getId());
    }
}




