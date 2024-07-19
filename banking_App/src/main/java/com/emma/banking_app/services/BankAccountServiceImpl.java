package com.emma.banking_app.services;

import com.emma.banking_app.dtos.AccountOperationsDto;
import com.emma.banking_app.dtos.ClientDto;
import com.emma.banking_app.dtos.CompteCourantDto;
import com.emma.banking_app.dtos.CompteDto;
import com.emma.banking_app.dtos.CompteEpargneDto;
import com.emma.banking_app.dtos.OperationDto;
import com.emma.banking_app.dtos.TransfertDto;
import com.emma.banking_app.entities.Client;
import com.emma.banking_app.entities.Compte;
import com.emma.banking_app.entities.CompteCourant;
import com.emma.banking_app.entities.CompteEpargne;
import com.emma.banking_app.entities.Operation;
import com.emma.banking_app.enums.OperationType;
import com.emma.banking_app.exceptions.ClientNotFoundException;
import com.emma.banking_app.exceptions.CompteNotFoundException;
import com.emma.banking_app.exceptions.SoldeInsufisantException;
import com.emma.banking_app.mappers.BankAccountMapperImpl;
import com.emma.banking_app.repositories.ClientRepository;
import com.emma.banking_app.repositories.CompteRepository;
import com.emma.banking_app.repositories.OperationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{
    private ClientRepository clientRepository;
    private CompteRepository compteRepository;
    private OperationRepository operationRepository ;

    private BankAccountMapperImpl mapperDto ;

    @Override
    public ClientDto saveClient(ClientDto clientDto) {
        log.info("Enregistrement d'un Client");
        Client client = mapperDto.fromClientDto(clientDto);
        Client savedClient = clientRepository.save(client);
        return mapperDto.fromClient(savedClient);
    }

    @Override
    public CompteCourantDto saveCurrentAccount(double soldeInitial, double decouvert, Long idClient) throws ClientNotFoundException {
        Client client = clientRepository.findById(idClient).orElse(null);
        if(client == null)
            throw new ClientNotFoundException("Le client auquel vous voulez associé ce compte n'existe pas");

        CompteCourant compteCourant = new CompteCourant();
        compteCourant.setId(UUID.randomUUID().toString());
        compteCourant.setCreatedAt(new Date());
        compteCourant.setClient(client);
        compteCourant.setDecouvert(decouvert);
        compteCourant.setSolde(soldeInitial);
        CompteCourant savedCompteCourant = compteRepository.save(compteCourant);

        return mapperDto.fromCompteCourant(savedCompteCourant);
    }

    @Override
    public CompteEpargneDto saveEpargneAccount(double soldeInitial, double taux, Long idClient) throws ClientNotFoundException {
        Client client = clientRepository.findById(idClient).orElse(null);
        if(client == null)
            throw new ClientNotFoundException("Le client auquel vous voulez associé ce compte n'existe pas");

        CompteEpargne compteEpargne = new CompteEpargne();
        compteEpargne.setId(UUID.randomUUID().toString());
        compteEpargne.setCreatedAt(new Date());
        compteEpargne.setClient(client);
        compteEpargne.setTaux(taux);
        compteEpargne.setSolde(soldeInitial);
        CompteEpargne savedCompteEpargne = compteRepository.save(compteEpargne);
        return mapperDto.fromCompteEpargne(savedCompteEpargne);
    }


    @Override
    public List<ClientDto> listClient() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream()
                .map(client -> mapperDto.fromClient(client))
                .collect(Collectors.toList());
    }

    @Override
    public CompteDto getAccount(String accountId) throws CompteNotFoundException {
        Compte compte = compteRepository.findById(accountId)
                .orElseThrow(()-> new CompteNotFoundException("Ce Compte n'existe pas"));
        if(compte instanceof CompteEpargne){
            CompteEpargne compteEpargne = (CompteEpargne) compte;
            return mapperDto.fromCompteEpargne(compteEpargne);
        }else {
            CompteCourant compteCourant = (CompteCourant) compte;
            return mapperDto.fromCompteCourant(compteCourant);
        }
    }

    @Override
    public void debit(TransfertDto transfertDto) throws CompteNotFoundException, SoldeInsufisantException {
        Compte compte = compteRepository.findById(transfertDto.getCompteId())
                .orElseThrow(()-> new CompteNotFoundException("Ce Compte n'existe pas"));
        if(compte.getSolde() < transfertDto.getMontant())
            throw new SoldeInsufisantException("Le solde de ce compte est inssufisant pour effectuer cette opération");
        Operation operation = new Operation();
        operation.setType(OperationType.DEBIT);
        operation.setDescription(transfertDto.getDescription());
        operation.setOperationDate(new Date());
        operation.setCompte(compte);
        operation.setMontant(transfertDto.getMontant());
        compte.setSolde(compte.getSolde() - transfertDto.getMontant());
        operationRepository.save(operation);
        compteRepository.save(compte);

    }

    @Override
    public void credit(TransfertDto transfertDto) throws CompteNotFoundException {
        Compte compte = compteRepository.findById(transfertDto.getCompteId())
                .orElseThrow(()-> new CompteNotFoundException("Ce Compte n'existe pas"));
        Operation operation = new Operation();
        operation.setType(OperationType.CREDIT);
        operation.setDescription(transfertDto.getDescription());
        operation.setOperationDate(new Date());
        operation.setCompte(compte);
        operation.setMontant(transfertDto.getMontant());
        compte.setSolde(compte.getSolde() + transfertDto.getMontant());
        operationRepository.save(operation);
        compteRepository.save(compte);

    }

    @Override
    public void transfert(String compteSource, String compteDestinataire, double montant)
            throws CompteNotFoundException, SoldeInsufisantException {
        TransfertDto transfertDto = new TransfertDto();
        transfertDto.setCompteId(compteSource);
        transfertDto.setMontant(montant);
        transfertDto.setDescription("Transfert vers" +compteDestinataire);
        debit(transfertDto);
        TransfertDto transfertDto1 = new TransfertDto();
        transfertDto1.setCompteId(compteDestinataire);
        transfertDto1.setMontant(montant);
        transfertDto1.setDescription("Transfert de la part de" +compteSource);
        credit(transfertDto1);
    }

    @Override
    public List<CompteDto> listeCompte(){
        List<Compte> comptes = compteRepository.findAll();
        return comptes.stream().map(compte -> {
            if (compte instanceof CompteCourant) {
                CompteCourant compteCourant = (CompteCourant) compte;
                return mapperDto.fromCompteCourant(compteCourant);
            } else {
                CompteEpargne compteEpargne = (CompteEpargne) compte;
                return mapperDto.fromCompteEpargne(compteEpargne);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public ClientDto getClient(Long idClient) throws ClientNotFoundException {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new ClientNotFoundException("Ce client n'existe pas"));
        return mapperDto.fromClient(client);
    }

    @Override
    public ClientDto updateClient(ClientDto clientDto) {
        log.info("Enregistrement d'un Client");
        Client client = mapperDto.fromClientDto(clientDto);
        Client savedClient = clientRepository.save(client);
        return mapperDto.fromClient(savedClient);
    }

    @Override
    public void deleteClient(Long clientId){
        clientRepository.deleteById(clientId);
    }

    @Override
    public List<OperationDto> historique(String compteId){
        List<Operation> operations = operationRepository.findByCompteId(compteId);
        return operations.stream().map(operation -> mapperDto.fromOperation(operation)).collect(Collectors.toList());
    }

    @Override
    public AccountOperationsDto getAccountHistory(String id, int page, int size) throws CompteNotFoundException {
        Compte compte = compteRepository.findById(id).orElseThrow(()->new CompteNotFoundException("Ce compte n'existe pas"));
        Page<Operation> operations = operationRepository.findByCompteId(id, PageRequest.of(page, size));
        AccountOperationsDto accountOperationsDto = new AccountOperationsDto() ;
        List<OperationDto> operationDtoList = operations.getContent().stream().map(operation -> mapperDto.fromOperation(operation)).collect(Collectors.toList());
        accountOperationsDto.setOperationDtos(operationDtoList);
        accountOperationsDto.setId(compte.getId());
        accountOperationsDto.setSolde(compte.getSolde());
        accountOperationsDto.setSize(size);
        accountOperationsDto.setCurrentPage(page);
        accountOperationsDto.setTotalPages(operations.getTotalPages());
        return accountOperationsDto ;

    }

    @Override
    public List<ClientDto> searchClients(String motcle) {
        List<Client> clients = clientRepository.searchClient(motcle);
        List<ClientDto> clientDtos = clients.stream().map(clt -> mapperDto.fromClient(clt)).collect(Collectors.toList());
        return clientDtos;
    }

}
