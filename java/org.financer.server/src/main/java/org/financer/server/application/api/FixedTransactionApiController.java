package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.server.domain.model.transaction.FixedTransactionAmount;
import org.financer.server.domain.service.TransactionDomainService;
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.fixed.FixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.fixed.TransactionAmountDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Controller
public class FixedTransactionApiController implements FixedTransactionApi {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransactionDomainService transactionDomainService;

    @Autowired
    public FixedTransactionApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<FixedTransactionDTO> createFixedTransaction(@NotNull @Valid CreateFixedTransactionDTO fixedTransaction) {
        FixedTransaction fixedTransactionEntity = modelMapper.map(fixedTransaction, FixedTransaction.class);
        fixedTransactionEntity.setId(-1);
        for (FixedTransactionAmount transactionAmount : fixedTransactionEntity.getTransactionAmounts()) {
            transactionAmount.setFixedTransaction(fixedTransactionEntity);
        }
        fixedTransactionEntity = transactionDomainService.createFixedTransaction(fixedTransactionEntity);

        return new ResponseEntity<>(modelMapper.map(fixedTransactionEntity, FixedTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateFixedTransaction(@NotBlank @Min(1) Long transactionId, @NotNull @Valid FixedTransactionDTO fixedTransaction) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteFixedTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId) {
        transactionDomainService.deleteFixedTransaction(transactionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TransactionAmountDTO> createTransactionAmount(@NotBlank @Min(1) Long transactionId, @NotNull @Valid TransactionAmountDTO transactionAmount) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<TransactionAmountDTO> createTransactionAmount(@NotBlank @Min(1) Long transactionId, @NotBlank @Min(1) Long transactionAmountId, @NotNull @Valid TransactionAmountDTO transactionAmount) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteTransactionAmount(@NotBlank @Min(1) Long transactionId, @NotBlank @Min(1) Long transactionAmountId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
