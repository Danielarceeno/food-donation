package com.example.donation.repository;

import com.example.donation.entity.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
    List<Doacao> findByUsuarioId(Long usuarioId);
    Long countByUsuarioId(Long usuarioId);
}
