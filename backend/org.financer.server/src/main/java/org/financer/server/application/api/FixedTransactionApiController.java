package org.financer.server.application.api;

import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.server.domain.model.transaction.FixedTransactionAmount;
import org.financer.server.domain.service.TransactionDomainService;
import org.financer.shared.domain.model.api.transaction.fixed.*;
import org.financer.util.mapping.ModelMapperUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
public class FixedTransactionApiController implements FixedTransactionApi {

    private static final Logger logger = LoggerFactory.getLogger(FixedTransactionApiController.class);

    private final ModelMapper modelMapper;
    private final TransactionDomainService transactionDomainService;

    @Autowired
    public FixedTransactionApiController(ModelMapper modelMapper, TransactionDomainService transactionDomainService) {
        this.modelMapper = modelMapper;
        this.transactionDomainService = transactionDomainService;
    }

    @Override
    public ResponseEntity<FixedTransactionDTO> createFixedTransaction(@NotNull @Valid CreateFixedTransactionDTO fixedTransaction) {
        FixedTransaction fixedTransactionEntity = modelMapper.map(fixedTransaction, FixedTransaction.class);
        if (fixedTransaction.getHasVariableAmounts() && !fixedTransaction.getTransactionAmounts().isEmpty()) {
            fixedTransactionEntity.getTransactionAmounts().clear();
            long id = -1;
            for (CreateFixedTransactionAmountDTO fixedTransactionAmount : fixedTransaction.getTransactionAmounts()) {
                fixedTransactionEntity.addFixedTransactionAmount(new FixedTransactionAmount()
                        .setId(id)
                        .setValueDate(fixedTransactionAmount.getValueDate())
                        .setAmount(fixedTransactionAmount.getAmount())
                        .setFixedTransaction(fixedTransactionEntity));
                id--;
            }
        }
        fixedTransactionEntity = transactionDomainService.createFixedTransaction(fixedTransactionEntity);
        return new ResponseEntity<>(modelMapper.map(fixedTransactionEntity, FixedTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FixedTransactionDTO> updateFixedTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                                      @NotNull @Valid UpdateFixedTransactionDTO fixedTransaction) {
        FixedTransaction updateFixedTransaction = transactionDomainService.updateFixedTransaction(transactionId,
                fixedTransaction.getCategoryId(), fixedTransaction.getAmount(), fixedTransaction.getTimeRange(),
                fixedTransaction.getProduct(), fixedTransaction.getDescription(), fixedTransaction.getVendor(),
                fixedTransaction.getHasVariableAmounts(), fixedTransaction.getDay(),
                ModelMapperUtils.mapAll(fixedTransaction.getTransactionAmounts(), FixedTransactionAmount.class));
        return new ResponseEntity<>(modelMapper.map(updateFixedTransaction, FixedTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteFixedTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId) {
        transactionDomainService.deleteFixedTransaction(transactionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FixedTransactionAmountDTO> createTransactionAmount(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                                             @NotNull @Valid CreateFixedTransactionAmountDTO transactionAmount) {
        FixedTransactionAmount transactionAmountEntity = modelMapper.map(transactionAmount, FixedTransactionAmount.class);
        transactionAmountEntity = this.transactionDomainService.createFixedTransactionAmount(transactionId, transactionAmountEntity);
        return new ResponseEntity<>(modelMapper.map(transactionAmountEntity, FixedTransactionAmountDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FixedTransactionAmountDTO> updateTransactionAmount(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                                             @NotBlank @PathVariable("transactionAmountId") @Min(1) Long transactionAmountId,
                                                                             @NotNull @Valid UpdateFixedTransactionAmountDTO transactionAmount) {
        FixedTransactionAmount transactionAmountEntity = transactionDomainService.updateFixedTransactionAmount(
                transactionId, transactionAmountId, transactionAmount.getAmount(), transactionAmount.getValueDate());
        return new ResponseEntity<>(modelMapper.map(transactionAmountEntity, FixedTransactionAmountDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteTransactionAmount(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                        @NotBlank @PathVariable("transactionAmountId") @Min(1) Long transactionAmountId) {
        try {
            transactionDomainService.deleteFixedTransactionAmount(transactionId, transactionAmountId);
        } catch (NotFoundException exception) {
            logger.info(exception.getMessage(), exception);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
