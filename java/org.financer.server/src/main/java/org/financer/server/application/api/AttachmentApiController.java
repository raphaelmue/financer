package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.financer.server.domain.model.transaction.Attachment;
import org.financer.server.domain.service.TransactionDomainService;
import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentWithContentDTO;
import org.financer.shared.domain.model.api.transaction.CreateAttachmentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
public class AttachmentApiController implements AttachmentApi {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransactionDomainService transactionDomainService;

    @Autowired
    public AttachmentApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<AttachmentDTO> createAttachment(@NotBlank @Min(1) Long transactionId, @NotNull @Valid CreateAttachmentDTO attachment) {
        Attachment attachmentEntity = modelMapper.map(attachment, Attachment.class);
        attachmentEntity = transactionDomainService.createAttachment(transactionId, attachmentEntity);
        return new ResponseEntity<>(modelMapper.map(attachmentEntity, AttachmentDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AttachmentWithContentDTO> getAttachment(@NotBlank @Min(1) Long transactionId, @NotBlank @Min(1) Long attachmentId) {
        Attachment attachment = transactionDomainService.getAttachmentById(transactionId, attachmentId);
        return new ResponseEntity<>(modelMapper.map(attachment, AttachmentWithContentDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(@NotBlank @Min(1) Long transactionId, @NotBlank @Min(1) Long attachmentId) {
        transactionDomainService.deleteAttachment(transactionId, attachmentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
