package org.financer.server.domain.model.user;

import org.financer.shared.domain.model.value.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class UserTest {

    private User user;
    private VerificationToken verificationToken;

    @BeforeEach
    private void setUp() {
        verificationToken = new VerificationToken()
                .setId(1L)
                .setToken(new TokenString())
                .setExpireDate(new ExpireDate());

        user = new User()
                .setId(1L)
                .setEmail(new Email("test@test.de"))
                .setName(new Name("First Name", "Surname"))
                .setVerificationToken(verificationToken)
                .setSettings(new HashMap<>());

        verificationToken.setUser(user);
    }

    @Test
    public void testIsVerified() {
        assertThat(user.isVerified()).isFalse();

        verificationToken.setVerifyingDate(LocalDate.now().minusMonths(2));
        assertThat(user.isVerified()).isTrue();

        verificationToken.setExpireDate(new ExpireDate(LocalDate.now().minusMonths(5)));
        assertThat(user.isVerified()).isFalse();
    }

    @Test
    public void testIsPropertyOfUser() {
        assertThat(user.isPropertyOfUser(1)).isTrue();
        assertThat(user.isPropertyOfUser(2)).isFalse();
    }

    @Test
    public void testPutOrUpdateSettingProperty() {
        user.putOrUpdateSettingProperty(SettingPair.Property.CURRENCY, "EUR");
        assertThat(user.getSettings().get(SettingPair.Property.CURRENCY).getPair().getValue()).isNotEmpty().isEqualTo("EUR");

        user.getSettings().get(SettingPair.Property.CURRENCY).setId(1L);
        user.putOrUpdateSettingProperty(SettingPair.Property.CURRENCY, "USD");
        assertThat(user.getSettings().get(SettingPair.Property.CURRENCY).getPair().getValue()).isNotEmpty().isEqualTo("USD");
        assertThat(user.getSettings().get(SettingPair.Property.CURRENCY).getId()).isPositive().isEqualTo(1);

        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, "de");
        assertThat(user.getSettings().size()).isEqualTo(2);
    }


}