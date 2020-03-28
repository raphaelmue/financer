package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.shared.domain.model.api.AttachmentDTO;
import org.financer.shared.domain.model.api.VariableTransactionDTO;
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
    public VariableTransactionApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }
    @Override
    public ResponseEntity<VariableTransactionDTO> createTransaction(@NotNull @Valid VariableTransactionDTO variableTransaction) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> updateTransaction(@NotBlank @Min(1) Long transactionId, @NotNull @Valid VariableTransactionDTO variableTransaction) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
