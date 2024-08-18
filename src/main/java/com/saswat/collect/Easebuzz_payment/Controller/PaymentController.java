package com.saswat.collect.Easebuzz_payment.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saswat.collect.Easebuzz_payment.Service.PaymentService;
import com.saswat.collect.Easebuzz_payment.Utils.InitiatePayRequest;
import com.saswat.collect.Easebuzz_payment.Utils.RefundRequest;
import com.saswat.collect.Easebuzz_payment.Utils.RefundStatusRequest;
import com.saswat.collect.Easebuzz_payment.Utils.TransactionStatus;

@RestController
public class PaymentController {

	@Autowired
	PaymentService paymentService;

	@PostMapping("/v1/payment/initiate")
	public ResponseEntity<String> InitiatePay(@ModelAttribute InitiatePayRequest initiatePayRequest) {
		return paymentService.InitiatePay(initiatePayRequest);
	}

	@PostMapping("/v1/payment/transaction/retrieve")
	public ResponseEntity<String> getTransactionstatus(@ModelAttribute TransactionStatus statusrequest) {

		return paymentService.getTransactionstatus(statusrequest);
	}

	@PostMapping("/v1/payment/refund")
	public ResponseEntity<String> initiateRefund(@RequestBody RefundRequest refundRequest)
			throws JsonProcessingException {
		return paymentService.initiateRefund(refundRequest);
	}
	
	@PostMapping("/v1/payment/refund/status")
	public ResponseEntity<String> getRefundstatus(@RequestBody RefundStatusRequest refundStatusRequest) throws JsonProcessingException
	{
		return paymentService.getRefundstatus(refundStatusRequest);
	}
	
	

}
