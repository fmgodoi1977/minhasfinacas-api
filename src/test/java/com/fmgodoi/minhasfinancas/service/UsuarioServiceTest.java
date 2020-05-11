package com.fmgodoi.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fmgodoi.minhasfinancas.exception.ErroAutenticacao;
import com.fmgodoi.minhasfinancas.exception.RegraNegocioException;
import com.fmgodoi.minhasfinancas.model.entity.Usuario;
import com.fmgodoi.minhasfinancas.model.repository.UsuarioRepository;
import com.fmgodoi.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuario() {

		// Cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().nome("nome").email("email@email.com").senha("senha").id(1l).build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		// Acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

		// Verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");

	}

	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUsuarioComEmailJaCadastrado() {

		// Cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

		// Acao
		service.salvarUsuario(usuario);

		// Verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);

	}

	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		// Cenario
		String email = "email@email.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

		// Ação
		Usuario result = service.autenticar(email, senha);

		// Verificação
		Assertions.assertThat(result).isNotNull();

	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {

		// Cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// Acao
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado.");

	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {

		// Cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// Acao
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");

	}

	@Test(expected = Test.None.class)
	public void deveValidarEmail() {

		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// ação
		service.validarEmail("email@email.com");

	}

	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// Ação
		service.validarEmail("email@email.com");
	}

}
