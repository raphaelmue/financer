package org.financer.server.application.api;

import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.domain.model.transaction.Product;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.service.TransactionDomainService;
import org.financer.shared.domain.model.api.transaction.variable.*;
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
import java.util.List;

@RestController
public class VariableTransactionApiController implements VariableTransactionApi {

    private static final Logger logger = LoggerFactory.getLogger(VariableTransactionApiController.class);

    private final ModelMapper modelMapper;
    private final TransactionDomainService transactionDomainService;

    @Autowired
    public VariableTransactionApiController(ModelMapper modelMapper, TransactionDomainService transactionDomainService) {
        this.modelMapper = modelMapper;
        this.transactionDomainService = transactionDomainService;
    }

    @Override
    public ResponseEntity<VariableTransactionDTO> createVariableTransaction(@NotNull @Valid CreateVariableTransactionDTO variableTransaction) {
        VariableTransaction variableTransactionEntity = modelMapper.map(variableTransaction, VariableTransaction.class);
        variableTransactionEntity = transactionDomainService.createVariableTransaction(variableTransactionEntity);
        return new ResponseEntity<>(modelMapper.map(variableTransactionEntity, VariableTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<VariableTransactionDTO> getVariableTransactionById(@NotBlank @Min(1) Long transactionId) {
        VariableTransaction variableTransaction = (VariableTransaction) transactionDomainService.findTransactionById(transactionId);
        return new ResponseEntity<>(modelMapper.map(variableTransaction, VariableTransactionDTO.class), HttpStatus.OK);
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

    @Override
    public ResponseEntity<ProductDTO> createProduct(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId, @NotNull @Valid CreateProductDTO product) {
        Product productEntity = modelMapper.map(product, Product.class);
        productEntity = transactionDomainService.createProduct(transactionId, productEntity);
        return new ResponseEntity<>(modelMapper.map(productEntity, ProductDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteProduct(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId, @NotBlank @PathVariable("productId") @Min(1) Long productId) {
        try {
            transactionDomainService.deleteProduct(transactionId, productId);
        } catch (NotFoundException exception) {
            logger.error(exception.getMessage(), exception);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteProducts(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId, @NotBlank @Min(1) List<Long> productIds) {
        try {
            transactionDomainService.deleteProducts(transactionId, productIds);
        } catch (NotFoundException exception) {
            logger.error(exception.getMessage(), exception);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
