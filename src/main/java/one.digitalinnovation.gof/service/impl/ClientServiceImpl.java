package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.model.Client;
import one.digitalinnovation.gof.repository.ClientRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.repository.EnderecoRepository;
import one.digitalinnovation.gof.service.ClientService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClientService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author falvojr
 * @author Gabriel
 */
@Service
public class ClientServiceImpl implements ClientService {
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	@Override
	public Iterable<Client> buscarTodos() {
		return clientRepository.findAll();
	}
	@Override
	public Client buscarPorId(Long id) {
		Optional<Client> cliente = clientRepository.findById(id);
		return cliente.get();
	}
	@Override
	public void inserir(Client cliente) {
		salvarClienteComCep(cliente);
	}
	@Override
	public void atualizar(Long id, Client cliente) {
		Optional<Client> clienteBd = clientRepository.findById(id);
		if (clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
		}
	}
	@Override
	public void deletar(Long id) {
		clientRepository.deleteById(id);
	}
	private void salvarClienteComCep(Client cliente) {
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		clientRepository.save(cliente);
	}
}
