package com.example.donation.repository;

import com.example.donation.entity.ItemSolicitado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemSolicitadoRepository extends JpaRepository<ItemSolicitado, Long> {
    List<ItemSolicitado> findByTituloContainingIgnoreCase(String titulo);
    List<ItemSolicitado> findBySolicitanteEmail(String email);
}