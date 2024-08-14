package com.saswat.collect.Easebuzz_payment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saswat.collect.Easebuzz_payment.Utils.TransactionEntity;

public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, Long> {
	  
    TransactionEntity findTopByOrderByTxnCreatedAtDesc();


    
}
