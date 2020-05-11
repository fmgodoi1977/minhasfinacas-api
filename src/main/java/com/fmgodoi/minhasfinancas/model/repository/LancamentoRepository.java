package com.fmgodoi.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fmgodoi.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
