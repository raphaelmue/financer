package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.transaction.VariableTransactionEntity;
import org.financer.server.domain.service.TransactionDomainService;
import org.financer.shared.domain.model.api.AttachmentDTO;
import org.financer.shared.domain.model.api.VariableTransactionDTO;
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
    private AuthenticationService authenticationService;

    @Autowired
    private TransactionDomainService transactionDomainService;


    @Autowired
    public VariableTransactionApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<VariableTransactionDTO> createTransaction(@NotNull @Valid VariableTransactionDTO variableTransaction) {
        VariableTransactionEntity result = transactionDomainService.createVariableTransaction(
                modelMapper.map(variableTransaction, VariableTransactionEntity.class));
        return new ResponseEntity<>(modelMapper.map(result, VariableTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateTransaction(@NotBlank @Min(1) Long transactionId, @NotNull @Valid VariableTransactionDTO variableTransaction) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId) {
        if (transactionDomainService.deleteVariableTransaction(authenticationService.getAuthenticatedUser().getId(), transactionId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<AttachmentDTO> createAttachment(@NotBlank @Min(1) Long transactionId, @NotNull @Valid AttachmentDTO attachment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<AttachmentDTO> getAttachment(@NotBlank @Min(1) Long transactionId, @NotBlank @Min(1) Long attachmentId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
