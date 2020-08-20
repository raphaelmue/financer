package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.Settings;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;

@Embeddable
@Immutable
@Schema(description = "Value object for birth date")
public class BirthDate implements Serializable, Formattable {
    private static final long serialVersionUID = -4072522792982525094L;


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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public String format(Settings settings) {
        Locale locale = settings.getValue(SettingPair.Property.LANGUAGE);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        return this.getBirthDate().format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BirthDate birthDate1 = (BirthDate) o;
        return Objects.equals(birthDate, birthDate1.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthDate);
    }

    @Override
    public String toString() {
        return "BirthDate [" +
                "birthDate=" + birthDate +
                ']';
    }


}
