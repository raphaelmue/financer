package org.financer.shared.domain.model.api.transaction;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@Schema(name = "Attachment", description = "Schema for an attachment")
public class AttachmentDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the attachment", required = true, minimum = "1")
    private int id;

    @NotNull
    @Schema(description = "Name of the attachment", example = "file.pdf", required = true)
    private String name;

    @NotNull
    @PastOrPresent
    @Schema(description = "Upload date of the attachment", example = "2020-01-01", required = true)
    private LocalDate uploadDate;

}
