package com.saswat.collect.Easebuzz_payment.Entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


@Entity
@Table(name = "transactions")
public class TransactionEntity {

	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String email;
	    private Long phone;
	    private String txnid;

	    @Temporal(TemporalType.TIMESTAMP)
	    private LocalDateTime txnCreatedAt=LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public Long getPhone() {
			return phone;
		}

		public void setPhone(Long phone) {
			this.phone = phone;
		}

		public String getTxnid() {
			return txnid;
		}

		public void setTxnid(String txnid) {
			this.txnid = txnid;
		}

		public LocalDateTime getTxnCreatedAt() {
			return txnCreatedAt;
		}

		public void setTxnCreatedAt(LocalDateTime txnCreatedAt) {
			this.txnCreatedAt = txnCreatedAt;
		}


	    
	    
	    
}
