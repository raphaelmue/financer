package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.shared.domain.model.api.FixedTransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

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
    public FixedTransactionApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<FixedTransactionDTO> createTransaction(@NotNull @Valid FixedTransactionDTO fixedTransaction) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> updateTransaction(@NotBlank @Min(1) Long transactionId, @NotNull @Valid FixedTransactionDTO fixedTransaction) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(@NotBlank @Min(1) Long transactionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
