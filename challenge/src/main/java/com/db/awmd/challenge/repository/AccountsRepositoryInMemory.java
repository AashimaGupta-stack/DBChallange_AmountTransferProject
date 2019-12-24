package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.BankTransactionException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import static org.junit.Assert.assertArrayEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.springframework.stereotype.Repository;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException (
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }


  @Override
  public synchronized void addAmount(String accountId, BigDecimal amount) throws BankTransactionException {
	// TODO Auto-generated method stub
	Account account= this.getAccount(accountId);
	BigDecimal newBalance;
	if(account == null)
		throw new BankTransactionException("Account not found " + accountId);
	else if(amount.signum()== -1) {
		throw new BankTransactionException("Negative amount not allowed");	
	}
		else {
		newBalance = amount.add(account.getBalance());
	}
	account.setBalance(newBalance);
	
  }
  
  public synchronized void subAmount(String accounId, BigDecimal amount) throws BankTransactionException {
	  Account account= this.getAccount(accounId);
		BigDecimal newBalance= new BigDecimal(0);
		if(account == null)
			throw new BankTransactionException("Account not found " + accounId);
		
		else if((amount.compareTo(account.getBalance()) == 1)) {
			throw new BankTransactionException("Insuffcient balance");
		}
		else if(amount.signum()== -1) {
			throw new BankTransactionException("Negative amount not allowed");	
		}
		else {
			newBalance = account.getBalance().subtract(amount);
		}
		account.setBalance(newBalance); 
  }

  @Override
  public synchronized void transferBetween(String accountFrom, String accountTo, BigDecimal amount) throws BankTransactionException {
		subAmount(accountFrom, amount);
		addAmount(accountTo, amount);
	  
  }


}
