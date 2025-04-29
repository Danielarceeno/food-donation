package com.example.donation.repository;

import com.example.donation.entity.Doacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
    Long countByItemSolicitadoId(Long itemId);

    Page<Doacao> findByUsuarioId(Long usuarioId, Pageable pageable);
}
