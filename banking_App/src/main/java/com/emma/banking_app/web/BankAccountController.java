package com.emma.banking_app.web;

import com.emma.banking_app.dtos.AccountTransfertDto;
import com.emma.banking_app.dtos.TransfertDto;
import com.emma.banking_app.exceptions.CompteNotFoundException;
import com.emma.banking_app.exceptions.SoldeInsufisantException;
import com.emma.banking_app.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class BankAccountController {
    private BankAccountService bankAccountService ;

    @GetMapping("/comptes")
    public String listeComptes(Model model){
        model.addAttribute("listComptes", bankAccountService.listeCompte());
        return "comptes";
    }

    @GetMapping("/debit")
    public String debit(Model model, String id) {
        TransfertDto transfertDto = new TransfertDto();
        transfertDto.setCompteId(id);
        model.addAttribute("transfertDto", transfertDto);
        return "debit";
    }

    @PostMapping("debiterCompte")
    public String debiterCompte(Model model, @ModelAttribute("transfertDto") @Valid TransfertDto transfertDto,
                                BindingResult bindingResult)
            throws CompteNotFoundException, SoldeInsufisantException {
        if (bindingResult.hasErrors()) {
            return "debit";
        }
        bankAccountService.debit(transfertDto);
        return "redirect:/comptes";
    }

    @GetMapping("/credit")
    public String credit(Model model, String id) {
        TransfertDto transfertDto = new TransfertDto();
        transfertDto.setCompteId(id);
        model.addAttribute("transfertDto", transfertDto);
        return "credit";
    }

    @PostMapping("crediterCompte")
    public String crediterCompte(Model model, @ModelAttribute("transfertDto") @Valid TransfertDto transfertDto,
                                BindingResult bindingResult)
            throws CompteNotFoundException {
        if (bindingResult.hasErrors()) {
            return "credit";
        }
        bankAccountService.credit(transfertDto);
        return "redirect:/comptes";
    }

    @GetMapping("/historique")
    public String historique(Model model, String id) {
        model.addAttribute("listOperations", bankAccountService.historique(id));
        return "historique";
    }

    @GetMapping("/transfert")
    public String transfert(Model model){
        model.addAttribute("transfert", new AccountTransfertDto());
        return "transfert";
    }

    @PostMapping("/saveTransfert")
    public String saveTransfert(Model model,
                                @ModelAttribute("transfert") @Valid AccountTransfertDto accountTransfertDto,
                                BindingResult bindingResult)
            throws CompteNotFoundException, SoldeInsufisantException {
        if (bindingResult.hasErrors()) {
            return "transfert";
        }
        bankAccountService.transfert(accountTransfertDto.getCompteSource(),
                accountTransfertDto.getCompteDestinataire(), accountTransfertDto.getMontant());
        return "redirect:/comptes";
    }

}
