package com.saswat.collect.Easebuzz_payment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saswat.collect.Easebuzz_payment.Entity.InitiateApiLogs;

public interface InitiateApiLogRepository extends JpaRepository<InitiateApiLogs, String> {
	

}
