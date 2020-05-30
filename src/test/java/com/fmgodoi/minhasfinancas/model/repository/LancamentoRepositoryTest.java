package com.fmgodoi.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fmgodoi.minhasfinancas.model.entity.Lancamento;

import model.enums.StatusLancamento;
import model.enums.TipoLancamento;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmLancamento() {

		Lancamento lancamento = criarLancamento();

		lancamento = repository.save(lancamento);

		assertThat(lancamento.getId()).isNotNull();

	}

	@Test
	public void deveDeletarUmLancamento() {

		Lancamento lancamento = criarEPersistirUmLancamento();

		lancamento = entityManager.find(Lancamento.class, lancamento.getId());

		repository.delete(lancamento);

		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

		assertThat(lancamentoInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmLamcamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		lancamento.setAno(2018);
		lancamento.setDescricao("Teste atualizador");
		lancamento.setStatus(StatusLancamento.CANCELADO);

		repository.save(lancamento);

		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste atualizador");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}

	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}

	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();

		entityManager.persist(lancamento);
		return lancamento;
	}

	public static Lancamento criarLancamento() {
		return Lancamento.builder().ano(2019).mes(1).descricao("lançamento qualquer").valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now()).build();
	}

}
