package com.saswat.collect.Easebuzz_payment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saswat.collect.Easebuzz_payment.Entity.TransactionApiLogs;

public interface TransactionApiLogRepository extends JpaRepository<TransactionApiLogs, String>{

}
