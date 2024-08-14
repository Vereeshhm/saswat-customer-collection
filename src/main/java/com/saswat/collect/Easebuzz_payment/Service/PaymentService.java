package com.saswat.collect.Easebuzz_payment.Service;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saswat.collect.Easebuzz_payment.Utils.InitiatePayRequest;
import com.saswat.collect.Easebuzz_payment.Utils.RefundRequest;
import com.saswat.collect.Easebuzz_payment.Utils.RefundStatusRequest;
import com.saswat.collect.Easebuzz_payment.Utils.TransactionStatus;

public interface PaymentService {

	ResponseEntity<String> InitiatePay(InitiatePayRequest initiatePayRequest);

	ResponseEntity<String> getTransactionstatus(TransactionStatus statusrequest);

	ResponseEntity<String> initiateRefund(RefundRequest refundRequest) throws JsonProcessingException;

	ResponseEntity<String> getRefundstatus(RefundStatusRequest refundStatusRequest) throws JsonProcessingException;

}
