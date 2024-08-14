package com.saswat.collect.Easebuzz_payment.ServiceImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saswat.collect.Easebuzz_payment.Entity.InitiateApiLogs;
import com.saswat.collect.Easebuzz_payment.Entity.RefundApiLogs;
import com.saswat.collect.Easebuzz_payment.Entity.TransactionApiLogs;
import com.saswat.collect.Easebuzz_payment.Entity.TransactionEntity;
import com.saswat.collect.Easebuzz_payment.Repository.InitiateApiLogRepository;
import com.saswat.collect.Easebuzz_payment.Repository.RefundApiLogRepository;
import com.saswat.collect.Easebuzz_payment.Repository.TransactionApiLogRepository;
import com.saswat.collect.Easebuzz_payment.Repository.TransactionEntityRepository;
import com.saswat.collect.Easebuzz_payment.Service.PaymentService;
import com.saswat.collect.Easebuzz_payment.Utils.HashUtil;
import com.saswat.collect.Easebuzz_payment.Utils.InitiatePayRequest;
import com.saswat.collect.Easebuzz_payment.Utils.PropertiesConfig;
import com.saswat.collect.Easebuzz_payment.Utils.RefundRequest;
import com.saswat.collect.Easebuzz_payment.Utils.RefundStatusRequest;
import com.saswat.collect.Easebuzz_payment.Utils.TransactionStatus;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	PropertiesConfig config;

	@Autowired
	TransactionEntityRepository transactionRepository;

	@Autowired
	InitiateApiLogRepository apiLogRepository;

	@Autowired
	TransactionApiLogRepository transactionapiLogRepository2;

	@Autowired
	RefundApiLogRepository refundapiLogRepository;

	@Autowired
	RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	@Override
	public ResponseEntity<String> InitiatePay(InitiatePayRequest initiatePayRequest) {

		String UrlString = config.getInitiateUrl();

		String txnid = generateUniqueTxnId();

		logger.info("Uniquely generated txnid " + txnid);

		String hashString = config.getMerchantKey() + "|" + txnid + "|" + initiatePayRequest.getAmount() + "|"
				+ initiatePayRequest.getProductinfo() + "|" + initiatePayRequest.getFirstname() + "|"
				+ initiatePayRequest.getEmail() + "|||||||||||" + config.getMerchantSalt();

		String hash = HashUtil.generateHash(hashString);

		logger.info("Generated Hash: " + hash);
		initiatePayRequest.setHash(hash);

		String urlParameters = "key=" + config.getMerchantKey() + "&txnid=" + txnid + "&amount="
				+ initiatePayRequest.getAmount() + "&productinfo=" + initiatePayRequest.getProductinfo() + "&firstname="
				+ initiatePayRequest.getFirstname() + "&phone=" + initiatePayRequest.getPhone() + "&email="
				+ initiatePayRequest.getEmail() + "&surl=" + config.getSuccessUrl() + "&furl=" + config.getFailureUrl()
				+ "&hash=" + hash

				+ "&city=" + initiatePayRequest.getCity() + "&state=" + initiatePayRequest.getState() + "&country="
				+ initiatePayRequest.getCountry() + "&zipcode=" + initiatePayRequest.getZipcode()
				+ "&show_payment_mode=" + initiatePayRequest.getShow_payment_mode() + "&split_payments="
				+ initiatePayRequest.getSplit_payments() + "&request_flow=" + config.getRequestflow()
				+ "&sub_merchant_id=" + initiatePayRequest.getSub_merchant_id() + "&payment_category="
				+ initiatePayRequest.getPayment_category() + "&account_no=" + initiatePayRequest.getAccount_no()
				+ "&ifsc=" + initiatePayRequest.getIfsc();

		String response1 = null;
		InitiateApiLogs apiLogs = new InitiateApiLogs();

		HttpURLConnection connection = null;

		try {
			URL url = new URL(UrlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setDoOutput(true);

			logger.info("Request Url " + UrlString);
			logger.info("Requsest Body " + urlParameters);

			apiLogs.setUrl(UrlString);
			apiLogs.setRequestBody(urlParameters);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(urlParameters);
				wr.flush();
			}
			int responseCode = connection.getResponseCode();

			logger.info("Response Code " + responseCode);

			StringBuilder response = new StringBuilder();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
				}
				response1 = response.toString();
				apiLogs.setResponseBody(response1);
				apiLogs.setStatus("SUCCESS");
				apiLogs.setStatusCode(responseCode);

				logger.info("Response Body " + response1);

				TransactionEntity transaction = new TransactionEntity();
				transaction.setEmail(initiatePayRequest.getEmail());
				transaction.setPhone(initiatePayRequest.getPhone());
				transaction.setTxnid(txnid);
				transaction.setTxnCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

				transactionRepository.save(transaction);

				return ResponseEntity.ok(response1);

			} else {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
				}
				response1 = response.toString();

				apiLogs.setResponseBody(response1);
				apiLogs.setStatus("FAILURE");
				apiLogs.setStatusCode(responseCode);
				logger.error("Error Response Body: " + response1);
				return ResponseEntity.status(responseCode).body(response1);
			}

		} catch (IOException e) {
			response1 = e.getMessage();

			apiLogs.setStatus("ERROR");
			apiLogs.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLogs.setResponseBody(response1);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

			apiLogRepository.save(apiLogs);
		}
	}

	@Override
	public ResponseEntity<String> getTransactionstatus(TransactionStatus statusrequest) {

		TransactionEntity transaction = transactionRepository.findTopByOrderByTxnCreatedAtDesc();

		if (transaction == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found for the given criteria.");
		}

		String txnid = transaction.getTxnid();

		logger.info("Extracted txnid from the repository " + txnid);

		String UrlString = config.getTransactionUrl();
		String hashString = config.getMerchantKey() + "|" + txnid + "|" + config.getMerchantSalt();
		String hash = HashUtil.generateHash(hashString);

		logger.info("Generated hash for Transaction status " + hash);
		statusrequest.setHash(hash);
		statusrequest.setTxnid(txnid);
		statusrequest.setKey(config.getMerchantKey());

		TransactionApiLogs apiLog = new TransactionApiLogs();

		String responseBody = null;
		String errorResponse = null;

		HttpURLConnection connection = null;

		try {
			URL url = new URL(UrlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);

			StringBuilder result = new StringBuilder();
			Field[] fields = statusrequest.getClass().getDeclaredFields();

			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				Object value = field.get(statusrequest);
				if (value != null) {
					if (result.length() > 0) {
						result.append("&");
					}
					result.append(URLEncoder.encode(name, "UTF-8"));
					result.append("=");
					result.append(URLEncoder.encode(value.toString(), "UTF-8"));
				}
			}

			String urlParameters = result.toString();

			logger.info("Request Url: " + UrlString);
			logger.info("Request Body: " + urlParameters);

			apiLog.setUrl(UrlString);
			apiLog.setRequestBody(urlParameters);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(urlParameters);
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("Response Code: " + responseCode);

			StringBuilder response = new StringBuilder();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
				}
				responseBody = response.toString();
				logger.info("Response Body: " + responseBody);

				apiLog.setResponseBody(responseBody);
				apiLog.setStatusCode(responseCode);
				apiLog.setStatus("SUCCESS");
				return ResponseEntity.ok(responseBody);

			} else {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
				}
				errorResponse = response.toString();
				logger.error("Error Response Body: " + errorResponse);
				apiLog.setResponseBody(errorResponse);
				apiLog.setStatusCode(responseCode);
				apiLog.setStatus("FAILURE");
				return ResponseEntity.status(responseCode).body(errorResponse);
			}

		} catch (IOException | IllegalAccessException e) {

			errorResponse = e.getMessage();
			logger.error("Exception occurred: " + e.getMessage(), e);
			apiLog.setStatus("ERROR");
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setResponseBody(errorResponse);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

			transactionapiLogRepository2.save(apiLog);

		}

	}

	@Override
	public ResponseEntity<String> initiateRefund(RefundRequest refundRequest) throws JsonProcessingException {

		TransactionEntity transaction = transactionRepository.findTopByOrderByTxnCreatedAtDesc();

		if (transaction == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found for the given criteria.");
		}

		String txnid = transaction.getTxnid();
		logger.info("Extracted txnid from the repository " + txnid);

		String hashString = config.getMerchantKey() + "|" + txnid + "|" + refundRequest.getAmount() + "|"
				+ refundRequest.getRefund_amount() + "|" + refundRequest.getEmail() + "|" + refundRequest.getPhone()
				+ "|" + config.getMerchantSalt();

		String hash = HashUtil.generateHash(hashString);
		logger.info("Generated hash for Transaction status " + hash);

		refundRequest.setKey(config.getMerchantKey());
		refundRequest.setTxnid(txnid);
		refundRequest.setHash(hash);

		RefundApiLogs refundapilogs = new RefundApiLogs();

		ResponseEntity<String> responseEntity;
		try {
			String url = config.getRefundUrl();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(refundRequest);
			logger.info("Request Body: " + requestBody);

			refundapilogs.setUrl(url);
			refundapilogs.setRequestBody(requestBody);

			HttpEntity<RefundRequest> entity = new HttpEntity<>(refundRequest, headers);

			responseEntity = restTemplate.postForEntity(url, entity, String.class);

			String responseBody = responseEntity.getBody();
			HttpStatusCode statusCode = responseEntity.getStatusCode();

			refundapilogs.setResponseBody(responseBody);
			refundapilogs.setStatusCode(statusCode.value());
			refundapilogs.setStatus(statusCode.is2xxSuccessful() ? "SUCCESS" : "FAILED");

			logger.info("Response Code: " + statusCode.value());
			logger.info("Response Body: " + responseBody);

		} catch (HttpClientErrorException e) {
			logger.error("HttpClientErrorException: " + e.getMessage(), e);

			refundapilogs.setResponseBody(e.getResponseBodyAsString());
			refundapilogs.setStatusCode(e.getStatusCode().value());
			refundapilogs.setStatus("FAILURE");

			responseEntity = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			logger.error("HttpServerErrorException: " + e.getMessage(), e);

			refundapilogs.setResponseBody(e.getResponseBodyAsString());
			refundapilogs.setStatusCode(e.getStatusCode().value());
			refundapilogs.setStatus("FAILURE");
			responseEntity = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		} catch (RestClientException e) {
			logger.error("RestClientException: " + e.getMessage(), e);

			refundapilogs.setResponseBody(e.getMessage());
			refundapilogs.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			refundapilogs.setStatus("ERROR");

			responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}

		finally {
			refundapiLogRepository.save(refundapilogs);
		}

		return responseEntity;
	}

	
	@Override
	public ResponseEntity<String> getRefundstatus(RefundStatusRequest refundStatusRequest)
			throws JsonProcessingException {

		String hashString = config.getMerchantKey() + "|" + config.getEasebuzzId() + "|" + config.getMerchantSalt();

		String hash = HashUtil.generateHash(hashString);
		logger.info("Generated Hash sequence " + hash);

		refundStatusRequest.getEasebuzz_id();
		refundStatusRequest.setKey(config.getMerchantKey());
		refundStatusRequest.setHash(hash);

		ResponseEntity<String> responseEntity;

		try {

			String url = config.getRefundStatusUrl();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(refundStatusRequest);
			logger.info("Request Body: " + requestBody);

			HttpEntity<RefundStatusRequest> entity = new HttpEntity<>(refundStatusRequest,headers);

			responseEntity = restTemplate.postForEntity(url, entity, String.class);
			logger.info("Response Body: " + responseEntity);

		} catch (HttpClientErrorException e) {
			logger.error("HttpClientErrorException: " + e.getMessage(), e);
			responseEntity = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

		} catch (HttpServerErrorException e) {
			logger.error("HttpServerErrorException: " + e.getMessage(), e);
			responseEntity = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

		} catch (RestClientException e) {
			logger.error("RestClientException: " + e.getMessage(), e);
			responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}

		return responseEntity;
	}

	private String generateUniqueTxnId() {

		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String digits = "0123456789";

		Random random = new Random();

		Set<Character> uniqueLettersPart1 = new LinkedHashSet<>();
		while (uniqueLettersPart1.size() < 3) {
			uniqueLettersPart1.add(letters.charAt(random.nextInt(letters.length())));
		}

		StringBuilder letterPart1 = new StringBuilder();
		for (Character letter : uniqueLettersPart1) {
			letterPart1.append(letter);
		}

		Set<Character> uniqueDigitsPart1 = new LinkedHashSet<>();
		while (uniqueDigitsPart1.size() < 2) {
			uniqueDigitsPart1.add(digits.charAt(random.nextInt(digits.length())));
		}

		StringBuilder digitPart1 = new StringBuilder();
		for (Character digit : uniqueDigitsPart1) {
			digitPart1.append(digit);
		}

		char uniqueLetterPart2 = letters.charAt(random.nextInt(letters.length()));

		Set<Character> uniqueDigitsPart2 = new LinkedHashSet<>();
		while (uniqueDigitsPart2.size() < 2) {
			uniqueDigitsPart2.add(digits.charAt(random.nextInt(digits.length())));
		}

		StringBuilder digitPart2 = new StringBuilder();
		for (Character digit : uniqueDigitsPart2) {
			digitPart2.append(digit);
		}

		return letterPart1.toString() + digitPart1.toString() + uniqueLetterPart2 + digitPart2.toString();

	}

}