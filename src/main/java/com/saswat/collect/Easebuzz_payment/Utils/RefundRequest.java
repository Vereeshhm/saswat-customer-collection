package com.saswat.collect.Easebuzz_payment.Utils;

public class RefundRequest {

	private String key;
	private String txnid;
	private Float refund_amount;
	private String phone;
	private String email;
	private Float amount;
	private String hash;

	// Getter and Setter methods

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	public Float getRefund_amount() {
		return refund_amount;
	}

	public void setRefund_amount(Float refund_amount) {
		this.refund_amount = refund_amount;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}