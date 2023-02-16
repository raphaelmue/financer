package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.Settings;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for birth date")
public class BirthDate implements Serializable, Formattable {
    private static final long serialVersionUID = -4072522792982525094L;

    @EqualsAndHashCode.Include
    @Column(name = "birth_date")
    @Schema(description = "Birth date", required = true, example = "1980-01-01")
    private LocalDate birthDate;

    public BirthDate() {
    }

    public BirthDate(String birthDateString) {
        this.birthDate = LocalDate.parse(birthDateString);
    }

    public BirthDate(LocalDate birthDate) {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future.");
        }
        this.birthDate = birthDate;
    }

    @Override
    public String format(Settings settings) {
        Locale locale = settings.getValue(SettingPair.Property.LANGUAGE);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        return this.getBirthDate().format(formatter);
    }
}
