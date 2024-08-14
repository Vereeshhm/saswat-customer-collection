package com.saswat.collect.Easebuzz_payment.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:config/application.properties")
public class PropertiesConfig {

	@Value("${Initiate.Url}")
	private String InitiateUrl;

	@Value("${Key}")
	private String MerchantKey;
	
	@Value("${Salt}")
	private String MerchantSalt;
	
	@Value("${surl}")
	private String SuccessUrl;
	
	@Value("${furl}")
	private String FailureUrl;
	
	@Value("${request_flow}")
	private String requestflow;
	
	@Value("${Transaction.Url}")
	private String TransactionUrl;
	
	@Value("${Refund.Url}")
	private String RefundUrl;
	
	@Value("${easebuzz_id}")
	private String easebuzzId;
	
	@Value("${RefundStatus.Url}")
	private String RefundStatusUrl;
	
	
	public String getRefundStatusUrl() {
		return RefundStatusUrl;
	}

	public void setRefundStatusUrl(String refundStatusUrl) {
		RefundStatusUrl = refundStatusUrl;
	}

	public String getEasebuzzId() {
		return easebuzzId;
	}

	public void setEasebuzzId(String easebuzzId) {
		this.easebuzzId = easebuzzId;
	}

	public String getRefundUrl() {
		return RefundUrl;
	}

	public void setRefundUrl(String refundUrl) {
		RefundUrl = refundUrl;
	}

	public String getTransactionUrl() {
		return TransactionUrl;
	}

	public void setTransactionUrl(String transactionUrl) {
		TransactionUrl = transactionUrl;
	}

	public String getRequestflow() {
		return requestflow;
	}

	public void setRequestflow(String requestflow) {
		this.requestflow = requestflow;
	}

	public String getSuccessUrl() {
		return SuccessUrl;
	}

	public void setSuccessUrl(String successUrl) {
		SuccessUrl = successUrl;
	}

	public String getFailureUrl() {
		return FailureUrl;
	}

	public void setFailureUrl(String failureUrl) {
		FailureUrl = failureUrl;
	}

	public String getInitiateUrl() {
		return InitiateUrl;
	}

	public void setInitiateUrl(String initiateUrl) {
		InitiateUrl = initiateUrl;
	}

	public String getMerchantKey() {
		return MerchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		MerchantKey = merchantKey;
	}

	public String getMerchantSalt() {
		return MerchantSalt;
	}

	public void setMerchantSalt(String merchantSalt) {
		MerchantSalt = merchantSalt;
	}
	
	
	
}
