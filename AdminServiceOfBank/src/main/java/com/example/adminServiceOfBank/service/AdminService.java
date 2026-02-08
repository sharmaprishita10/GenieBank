package com.example.adminServiceOfBank.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.adminServiceOfBank.clients.*;
import com.example.adminServiceOfBank.model.*;
import com.example.adminServiceOfBank.payload.request.*;
import com.example.adminServiceOfBank.repository.*;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

import jakarta.transaction.Transactional;
import com.example.adminServiceOfBank.payload.response.*;

@Service
@Transactional
public class AdminService {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private EmpRepository empRepo;

	@Autowired
	private BranchRepository branchRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private AuthenticationClient authClient;

	@Autowired
	private CustomerClient customerClient;

	@Autowired
	private AccountClient accountClient;

	@Autowired
	private TransactionClient transactionClient;

	@Value("${twilio.verify-service-sid}")
	private String verifyServiceSid;

	@Autowired
	private SendGrid sendGrid;

	private static final String TEMPLATE_ID = "d-62492161d84c469aa65127e7536bf701";

	private WelcomeEmailRequest welcomeEmail = new WelcomeEmailRequest();

	private String verifiedMobile = null;
	private String verifiedEmail = null;

	public void addEmployee(NewEmpRequest empDto) {

		// Mapping all the simple fields
		Employee emp = mapper.map(empDto, Employee.class);

		// Set Branch
		if (empDto.getBranchId() != 0) {

			Branch branch = branchRepo.findById(empDto.getBranchId()).get();
			emp.setBranch(branch);

		}

		// Set Roles
		List<Role> roles = roleRepo.findAllById(empDto.getRoles());
		emp.setRoles(roles);

		empRepo.save(emp);

		NewUserRequest userDto = new NewUserRequest();
		userDto.setUsername(emp.getEmpId());
		userDto.setPassword(encoder.encode(empDto.getPassword()));
		userDto.setRoles(empDto.getRoles());

		authClient.addNewUser(userDto);

	}

	public void sendSmsOtp(String mobileNumber) {

		Verification.creator(verifyServiceSid, "+91" + mobileNumber, "sms").create();
	}

	public void sendEmailOtp(String email) {

		Verification.creator(verifyServiceSid, email, "email").create();
	}

	public String verifySmsOtp(String mobileNumber, String code) {

		VerificationCheck check = VerificationCheck.creator(verifyServiceSid).setTo("+91" + mobileNumber).setCode(code)
				.create();

		if ("approved".equals(check.getStatus())) {
			verifiedMobile = mobileNumber;
			return "Mobile number verified successfully.";
		} else {
			return "Mobile number verification failed.";
		}
	}

	public String verifyEmailOtp(String email, String code) {
		VerificationCheck check = VerificationCheck.creator(verifyServiceSid).setTo(email).setCode(code).create();

		if ("approved".equals(check.getStatus())) {
			verifiedEmail = email;
			return "Email address verified successfully.";
		} else {
			return "Email address verification failed.";
		}
	}

	public String addCustomer(String employee, NewCustomerRequest custDto) {

		String message;
		// Is mobile number verified?
		if (custDto.getMobileNumber().equals(verifiedMobile)) {
			// Is email address verified?
			if (custDto.getEmail().equals(verifiedEmail)) {
				// Verify age
				int age = Period.between(custDto.getDob(), LocalDate.now()).getYears();
				if (age >= 18) {
					// Fetch the branch of the manager registering the customer
					Employee emp = empRepo.findByEmpId(employee);
					int branchCode = emp.getBranch().getCode();

					// Generate customer id
					custDto.setCustId("GENB" + branchCode);
					custDto.setBranchId(emp.getBranch().getId());

					// Customer registration
					ApiResponse response = customerClient.addCustomer(custDto);
					String finalCustId = response.getData().toString();
					NewUserRequest userDto = new NewUserRequest();
					userDto.setUsername(finalCustId);

					// Password is any random string
					String password = UUID.randomUUID().toString().replace("-", "");

					userDto.setPassword(encoder.encode(password));
					userDto.setRoles(custDto.getRoles());

					// Corresponding user creation
					authClient.addNewUser(userDto);
					message = "Customer registered and corresponding user added successfully!";
					welcomeEmail.setName(custDto.getName());
					welcomeEmail.setCustId(finalCustId);
					welcomeEmail.setPassword(password);
					welcomeEmail.setAddress(custDto.getAddress());
					welcomeEmail.setBranchName(emp.getBranch().getName());
					welcomeEmail.setBranchCode(emp.getBranch().getCode());
					welcomeEmail.setBranchAddress(emp.getBranch().getAddress());
					welcomeEmail.setMobileNumber(custDto.getMobileNumber());
					welcomeEmail.setEmail(custDto.getEmail());
				} else {
					message = "Age should be 18 or above to open a bank account.";
				}
			} else {
				message = "Email verification failed.";
			}
		} else {
			message = "Mobile number verification failed.";
		}

		// Reset the variables
		verifiedMobile = null;
		verifiedEmail = null;
		return message;
	}

	public void openAccount(String employee, String custId) throws IOException {

		// Fetch the branch of the manager registering the customer
		Employee emp = empRepo.findByEmpId(employee);
		int branchCode = emp.getBranch().getCode();

		// Fetch customer's id
		ApiResponse response = customerClient.getIdByCustId(custId);
		int customerId = Integer.parseInt(response.getData().toString());

		NewAccountRequest newAccount = new NewAccountRequest(branchCode + "", customerId, emp.getId());
		response = accountClient.addAccount(newAccount);
		String accountNumber = response.getData().toString();

		welcomeEmail.setAccountNumber(accountNumber);

		// Sending welcome email
		Mail mail = new Mail();
		mail.setFrom(new Email("ps@gmail.com", "GenieBank"));
		mail.setTemplateId(TEMPLATE_ID);

		Personalization personalization = new Personalization();
		personalization.addTo(new Email(welcomeEmail.getEmail()));

		// Filling data in the welcome email
		personalization.addDynamicTemplateData("name", welcomeEmail.getName());
		personalization.addDynamicTemplateData("custId", welcomeEmail.getCustId());
		personalization.addDynamicTemplateData("password", welcomeEmail.getPassword());
		personalization.addDynamicTemplateData("address", welcomeEmail.getAddress());
		personalization.addDynamicTemplateData("branchName", welcomeEmail.getBranchName());
		personalization.addDynamicTemplateData("branchCode", welcomeEmail.getBranchCode());
		personalization.addDynamicTemplateData("branchAddress", welcomeEmail.getBranchAddress());
		personalization.addDynamicTemplateData("mobileNumber", welcomeEmail.getMobileNumber());
		personalization.addDynamicTemplateData("accountNumber", welcomeEmail.getAccountNumber());

		mail.addPersonalization(personalization);

		Request request = new Request();
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		request.setBody(mail.build());

		Response res = sendGrid.api(request);
		if (res.getStatusCode() >= 400) {
			throw new IOException("Failed to send welcome email: " + res.getBody());
		}

	}

	public String depositAmount(String employee, String accountNumber, double amount) {

		ApiResponse response = accountClient.getIdByAccNum(accountNumber);
		if(response.getData() == null)
		{
			return null;
		}
		int toAccount = Integer.parseInt(response.getData().toString());
		TransactionRequest transactionRequest = new TransactionRequest();
		transactionRequest.setToAccount(toAccount);
		transactionRequest.setAmount(amount);
		transactionRequest.setTransactionType("DEPOSIT");
		transactionRequest.setCreatedBy(employee);
		transactionRequest.setModeOfTransaction("OFFLINE");

		transactionClient.addTransaction(transactionRequest);

		UpdateBalanceRequest updateBalRequest = new UpdateBalanceRequest(toAccount, "Credit", amount);
		accountClient.updateBalance(updateBalRequest);
		
		return "Amount deposited successfully!";
	}

	public String withdrawAmount(String employee, String accountNumber, double amount) {
		
		ApiResponse response = accountClient.getIdByAccNum(accountNumber);
		if(response.getData() == null)
		{
			return null;
		}
		
		int fromAccount = Integer.parseInt(response.getData().toString());

		response = accountClient.getBalance(fromAccount);
		double availBal = Double.parseDouble(response.getData().toString());

		String message;
		if ((availBal - amount) >= 0) {
			TransactionRequest transactionRequest = new TransactionRequest();
			transactionRequest.setFromAccount(fromAccount);
			transactionRequest.setAmount(amount);
			transactionRequest.setTransactionType("WITHDRAWAL");
			transactionRequest.setCreatedBy(employee);
			transactionRequest.setModeOfTransaction("OFFLINE");

			transactionClient.addTransaction(transactionRequest);

			UpdateBalanceRequest updateBalRequest = new UpdateBalanceRequest(fromAccount, "Debit", amount);
			accountClient.updateBalance(updateBalRequest);
			message = "Amount withdrawn successfully!";
		} else {
			message = "Withdrawal failed due to insufficient balance.";
		}

		return message;
	}

	public Object getStatement(String custId, LocalDate from, LocalDate to) {

		ApiResponse response = customerClient.getIdByCustId(custId);
		if(response.getData() == null)
		{
			return null;
		}
		int customerId = Integer.parseInt(response.getData().toString());
		
		response = accountClient.getIdByCustId(customerId);
		int accountId = Integer.parseInt(response.getData().toString());

		response = transactionClient.getStatement(accountId, from, to);
		return response.getData();
	}

	public Double getBalance(String custId) {

		ApiResponse response = customerClient.getIdByCustId(custId);
		if(response.getData() == null)
		{
			return null;
		}
		int customerId = Integer.parseInt(response.getData().toString());
		
		response = accountClient.getIdByCustId(customerId);
		if(response.getData() == null)
		{
			return null;
		}
		int accountId = Integer.parseInt(response.getData().toString());
		
		response = accountClient.getBalance(accountId);
		double availBal = Double.parseDouble(response.getData().toString());
		
		return availBal;
	}

	public Object findCustomerByCustId(String custId) {
		ApiResponse response = customerClient.getByCustId(custId);
		return response.getData();
	}

}
