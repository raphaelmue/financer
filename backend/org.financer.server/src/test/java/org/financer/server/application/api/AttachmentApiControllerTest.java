package org.financer.server.application.api;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.security.WebSecurityConfiguration;
import org.financer.server.application.service.AdminConfigurationService;
import org.financer.server.domain.model.transaction.Attachment;
import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentWithContentDTO;
import org.financer.shared.domain.model.api.transaction.CreateAttachmentDTO;
import org.financer.shared.path.PathBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest(classes = {FinancerServer.class, AdminConfigurationService.class, WebSecurityConfiguration.class, RestExceptionHandler.class, AttachmentApiController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class AttachmentApiControllerTest extends ApiTest {

    @Test
    public void testCreateAttachment() throws Exception {
        when(transactionDomainService.createAttachment(anyLong(), any(Attachment.class)))
                .thenAnswer(i -> ((Attachment) i.getArguments()[1])
                        .setId(1)
                        .setUploadDate(LocalDate.now()));

        CreateAttachmentDTO dto = new CreateAttachmentDTO()
                .setContent(new byte[16])
                .setName("Test Attachment");
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Put().variableTransactions().variableTransactionId(1).attachments().build(), dto))
                .andExpect(status().isOk()).andReturn();

        AttachmentDTO attachment = objectMapper.readValue(result.getResponse().getContentAsString(), AttachmentDTO.class);
        assertThat(attachment.getId()).isEqualTo(1);
        assertThat(attachment.getName()).isEqualTo(dto.getName());
        assertThat(attachment.getUploadDate()).isEqualTo(LocalDate.now());

        verify(transactionDomainService, times(1)).createAttachment(eq(1L), any(Attachment.class));
    }

    @Test
    public void testGetAttachment() throws Exception {
        when(transactionDomainService.getAttachmentById(anyLong(), anyLong()))
                .thenAnswer(i -> new Attachment()
                        .setId(1)
                        .setContent(new byte[16])
                        .setName("Test Attachment")
                        .setUploadDate(LocalDate.now()));

        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Get().variableTransactions().variableTransactionId(1).attachments().attachmentId(1).build()))
                .andExpect(status().isOk()).andReturn();

        AttachmentWithContentDTO attachment = objectMapper.readValue(result.getResponse().getContentAsString(), AttachmentWithContentDTO.class);
        assertThat(attachment.getId()).isEqualTo(1);
        assertThat(attachment.getContent()).hasSize(16);
        assertThat(attachment.getName()).isEqualTo("Test Attachment");
        assertThat(attachment.getUploadDate()).isEqualTo(LocalDate.now());

        verify(transactionDomainService, times(1)).getAttachmentById(eq(1L), eq(1L));
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        mockMvc.perform(buildRequest(PathBuilder.Delete().variableTransactions().variableTransactionId(1).attachments().attachmentId(1).build()))
                .andExpect(status().isOk());

        verify(transactionDomainService, times(1)).deleteAttachment(eq(1L), eq(1L));
    }
}