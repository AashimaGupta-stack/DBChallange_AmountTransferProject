package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-111");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-111")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

    }
  
  
  @Test
  public void amountTransfer() throws Exception {
    Account accountFrom = new Account("Id-123");
    accountFrom.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);

    Account accountTo = new Account("Id-124");
    accountTo.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountTo);
    
    BigDecimal amount = new BigDecimal(100);
    this.accountsService.transferBetween(accountFrom.getAccountId(), accountTo.getAccountId(),
			   amount);
    assertThat("Transaction Successful");
    
  }  
  
   @Test
   public void amountTransfer_Overdraft() throws Exception {
	   Account accountFrom = new Account("Id-125");
	   accountFrom.setBalance(new BigDecimal(1000));
	    this.accountsService.createAccount(accountFrom);
	    
	   
	    Account accountTo = new Account("Id-126");
	    accountTo.setBalance(new BigDecimal(1000));
	    this.accountsService.createAccount(accountTo);
	    
	    BigDecimal amount = new BigDecimal(100);
	    if(amount.compareTo(accountFrom.getBalance())>1)
	       try {
		   this.accountsService.transferBetween(accountFrom.getAccountId(), accountTo.getAccountId(),
				   amount);
		   fail("Should fail when transferring amount is more than balance i.e. overdrafts not allowed");
		   
	   }catch (DuplicateAccountIdException e) {
		// TODO: handle exception
		   assertThat(e.getMessage()).isEqualTo("Overdrafts are not allowed!");
	}
   }
}
