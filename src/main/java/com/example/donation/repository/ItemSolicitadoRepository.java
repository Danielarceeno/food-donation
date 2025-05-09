package com.example.donation.repository;

import com.example.donation.entity.ItemSolicitado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ItemSolicitadoRepository
    extends JpaRepository<ItemSolicitado, Long>,
    JpaSpecificationExecutor<ItemSolicitado> {
    List<ItemSolicitado> findByTituloContainingIgnoreCase(String titulo);
    List<ItemSolicitado> findBySolicitanteEmail(String email);
}
