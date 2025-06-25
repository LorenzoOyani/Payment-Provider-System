package org.example.paymentgateway.services.paymentServices;

import jakarta.transaction.Transactional;
import org.example.paymentgateway.dto.InitializePaymentResponse;
import org.example.paymentgateway.dto.PaymentRequest;
import org.example.paymentgateway.dto.PaymentResponse;
import org.example.paymentgateway.dto.PaymentTransactionsDto;
import org.example.paymentgateway.entities.*;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;
import org.example.paymentgateway.exception.PaymentException;
import org.example.paymentgateway.mapper.PaymentMapper;
import org.example.paymentgateway.mapper.PaymentTransactionMapper;
import org.example.paymentgateway.repositories.PaymentRepository;
import org.example.paymentgateway.repositories.PaymentTransactionRepository;
import org.example.paymentgateway.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentServiceFactory paymentServiceFactory;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;


    @Autowired
    PaymentServiceImpl(PaymentServiceFactory paymentServiceFactory, PaymentMapper paymentMapper, PaymentRepository paymentRepository, UserRepository userRepository, PaymentTransactionRepository paymentTransactionRepository, PaymentTransactionMapper paymentTransactionMapper) {
        this.paymentServiceFactory = paymentServiceFactory;
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentTransactionMapper = paymentTransactionMapper;
    }


    @Override
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public InitializePaymentResponse createPayment(PaymentRequest request) throws IOException {
        validateRequest(request);

        ///  check for idempotency
        Optional<PaymentTransaction> existingTransaction =
                paymentTransactionRepository.findByReference(request.getReference());

        if (existingTransaction.isPresent()) {
            log.info("returning existing payment with reference, {}", request.getReference());
            return currentValidPaymentResponse(existingTransaction.get());
        }

        PaymentProvider paymentProvider = Optional.of(request.getPaymentProvider())
                .orElse(PaymentProvider.PAYSTACK);

        User user = findUserByEmail(request.getCustomerEmail());
        try {
            PaymentService paymentServiceProvider = paymentServiceFactory.getPaymentProviderService(paymentProvider);

            Payment payments = createPaymentForDb(request, paymentProvider, user);
            paymentRepository.save(payments);

            PaymentTransaction transaction = createTransactions(request, paymentProvider, user);

            paymentTransactionRepository.save(transaction);

            return processPaymentCreation(request, paymentServiceProvider, transaction);


        } catch (Exception e) {
            log.error("error persisting to db {}", e.getMessage());
            throw new PaymentException("error processing payment with message  " + e.getMessage());
        }

    }

    private InitializePaymentResponse processPaymentCreation(PaymentRequest request, PaymentService paymentServiceProvider, PaymentTransaction transaction) {

        try {
            InitializePaymentResponse response = paymentServiceProvider.createPayment(request);

            transaction.setReference(response.getReference());
            transaction.setStatus(PaymentStatus.PENDING);
            paymentTransactionRepository.save(transaction);

            return response;
        } catch (IOException e) {
            log.info("Error initializing payment with provider: {}", e.getMessage());

            transaction.setStatus(PaymentStatus.FAILURE);
            paymentTransactionRepository.save(transaction);
            throw new PaymentException("failed to initialize payment: " + e.getMessage());
        }
    }

    private InitializePaymentResponse currentValidPaymentResponse(PaymentTransaction paymentTransaction) {

        return InitializePaymentResponse
                .builder()
                .authorizationUrl(paymentTransaction.getPaymentUrl())
                .reference(paymentTransaction.getReference())
                .status(paymentTransaction.getStatus() != null ? PaymentStatus.SUCCESS : PaymentStatus.PENDING)
                .build();
    }

    private PaymentTransaction createTransactions(PaymentRequest request, PaymentProvider paymentProvider, User user) {
        PaymentTransactionsDto transactionsDto = PaymentTransactionsDto.builder()
                .amount(request.getAmount())
                .reference(request.getReference())
                .paymentProvider(paymentProvider)
                .currency(request.getCurrency())
                .paymentUrl(request.getGetCallBackUrl())
                .status(PaymentStatus.PENDING.name())
                .user(user)
                .createdAt(request.getCreatedAt())
                .build();


        return paymentTransactionMapper.paymentTransactionMapper(transactionsDto);

    }

    private Payment createPaymentForDb(PaymentRequest request, PaymentProvider paymentProvider, User user) {
        PaymentDto paymentDto = PaymentDto.builder()
                .withAmount(request.getAmount())
                .withCurrency(request.getCurrency())
                .withProvider(paymentProvider)
                .withCustomerEmail(request.getCustomerEmail())
                .withProviderReference(request.getReference())
                .withMetaData(request.getProviderMetadata())
                .withUser(user)
                .build();

        return paymentMapper.toPayment(paymentDto);
    }

    private void validateRequest(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");

        }
        if (ObjectUtils.isEmpty(request.getReference()) || request.getReference().isEmpty()) {
            request.setReference(generateReference());
        }

        if (ObjectUtils.isEmpty(request.getCustomerEmail())) {
            throw new IllegalArgumentException("customer email cannot be empty");
        }
        if (ObjectUtils.isEmpty(request.getReference())) {
            throw new IllegalArgumentException("reference cannot be empty");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount cannot be empty or less than zero");
        }
        if (request.getCurrency() == null) {
            throw new IllegalArgumentException("currency field is required");
        }
    }

    private String generateReference() {
        return "PAY_" + UUID.randomUUID().toString().toLowerCase();
    }

    private User findUserByEmail(String customerEmail) {
        return userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("user with %s not found", customerEmail)));
    }

    @Override
    public boolean verification(String paymentId) {
        return false;
    }

    @Override
    public PaymentResponse verifyPayment(String paymentId) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByReference(paymentId)
                .orElseThrow(() -> new IllegalStateException(String.format("can't find a transaction with the given Id of %s ", paymentId)));


        User currentUser = getAuthenticatedUser();


        if (!hasPermissionToViewTransaction(paymentTransaction, currentUser)) {
            throw new IllegalStateException("user does not have permission to view transaction");
        }

        return null;

    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByEmail(username).orElseThrow(() -> new IllegalStateException("user with %s not found in database " + username));
    }

    private boolean hasPermissionToViewTransaction(PaymentTransaction paymentTransaction, User currentUser) {

        if (paymentTransaction.getId().equals(currentUser.getId())) {
            return true;
        }

        return currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "CUSTOMER".equals(role.getName()));

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (paymentRepository == null) {
            throw new IllegalStateException("payment repository must be configured");

        }
        if (paymentServiceFactory == null) {
            throw new IllegalStateException("payment factory must be configured");
        }

        try{
            paymentServiceFactory.validateProviders();
            log.info("All payment providers validated successfully");
        }catch(Exception e){
            log.info("validation failed for providers with message {}",e.getMessage() );
            throw e;
        }

    }
}
