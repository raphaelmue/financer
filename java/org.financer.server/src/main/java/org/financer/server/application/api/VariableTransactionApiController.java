package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.service.TransactionDomainService;
import org.financer.shared.domain.model.api.transaction.variable.CreateVariableTransactionDTO;
import org.financer.shared.domain.model.api.transaction.variable.UpdateVariableTransactionDTO;
import org.financer.shared.domain.model.api.transaction.variable.VariableTransactionDTO;
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
public class VariableTransactionApiController implements VariableTransactionApi {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransactionDomainService transactionDomainService;


    @Autowired
    public VariableTransactionApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<VariableTransactionDTO> createTransaction(@NotNull @Valid CreateVariableTransactionDTO variableTransaction) {
        VariableTransaction variableTransactionEntity = modelMapper.map(variableTransaction, VariableTransaction.class);
        variableTransactionEntity = transactionDomainService.createVariableTransaction(variableTransactionEntity);
        return new ResponseEntity<>(modelMapper.map(variableTransactionEntity, VariableTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<VariableTransactionDTO> updateTransaction(@NotBlank @Min(1) Long transactionId,
                                                                    @NotNull @Valid UpdateVariableTransactionDTO variableTransaction) {
        VariableTransaction updateTransaction = transactionDomainService.updateVariableTransaction(transactionId,
                variableTransaction.getCategoryId(), variableTransaction.getValueDate(), variableTransaction.getDescription(),
                variableTransaction.getVendor());
        return new ResponseEntity<>(modelMapper.map(updateTransaction, VariableTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId) {
        transactionDomainService.deleteVariableTransaction(transactionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
