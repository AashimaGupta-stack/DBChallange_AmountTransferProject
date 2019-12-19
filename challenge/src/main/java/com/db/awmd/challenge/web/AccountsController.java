package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransfer;
import com.db.awmd.challenge.exception.BankTransactionException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;
import javax.xml.ws.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController implements NotificationService {

  private final AccountsService accountsService;
  //private final NotificationService notification;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  

  
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
  
 // If we create different model class for Account transfer
 /* @RequestMapping(value = "/transferAmount", method = RequestMethod.POST)
  public void betweenAccounts(MoneyTransfer sendMoneyFrom) throws BankTransactionException {

            accountsService.transferBetween(sendMoneyFrom.getFrom(), 
    		  sendMoneyFrom.getTo(), 
    		  sendMoneyFrom.getAmount());
      
  }*/
  
  // Passing the parameters within body
  @PostMapping(value = "/transferAmount")
  public void betweenAccounts(@RequestParam(value="accountFrom") String accountFrom, 
		  @RequestParam(value="accountTo") String accountTo, 
		  @RequestParam(value="amount") BigDecimal amount) throws BankTransactionException {

            accountsService.transferBetween(accountFrom, accountTo, amount);
            
            notifyAboutTransfer(accountsService.getAccount(accountFrom), "Amount credited to your account from above account holder");
            notifyAboutTransfer(accountsService.getAccount(accountTo), "Amount debited from your account and credited to above account holder");
  }




@Override
public void notifyAboutTransfer(Account account, String transferDescription) {
	// TODO Auto-generated method stub
	
}
  
}
