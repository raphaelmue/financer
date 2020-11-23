package org.financer.server;

import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.*;
import org.financer.server.domain.model.user.Role;
import org.financer.server.domain.model.user.Token;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.model.user.VerificationToken;
import org.financer.server.domain.repository.*;
import org.financer.shared.domain.model.value.objects.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

@ActiveProfiles(profiles = "test")
public abstract class SpringTest {

    @MockBean
    protected AuthenticationService authenticationService;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected RoleRepository roleRepository;

    @MockBean
    protected TokenRepository tokenRepository;

    @MockBean
    protected VerificationTokenRepository verificationTokenRepository;

    @MockBean
    protected CategoryRepository categoryRepository;

    @MockBean
    protected VariableTransactionRepository variableTransactionRepository;

    @MockBean
    protected FixedTransactionRepository fixedTransactionRepository;

    @MockBean
    protected FixedTransactionAmountRepository fixedTransactionAmountRepository;

    @MockBean
    protected AttachmentRepository attachmentRepository;

    @MockBean
    protected ProductRepository productRepository;

    protected void mockAnotherUserAuthenticated() {
        when(authenticationService.getUserId()).thenReturn(-1L);
    }

    protected User user() {
        return new User()
                .setId(1L)
                .setEmail(new Email("test@test.com"))
                .setName(new Name("Test", "User"))
                .setPassword(new HashedPassword(password()))
                .setTokens(new HashSet<>(Collections.singletonList(token())))
                .setVerificationToken(verificationToken())
                .setRoles(roles());
    }

    protected Set<Role> roles() {
        return Set.of(
                new Role()
                        .setId(1L)
                        .setName("USER"), userRole());
    }

    protected Role userRole() {
        return new Role()
                .setId(2L)
                .setName("ADMIN");
    }

    protected String password() {
        return "password";
    }

    protected Token token() {
        return new Token()
                .setId(1L)
                .setExpireDate(new ExpireDate())
                .setIpAddress(new IPAddress("192.168.0.1"))
                .setToken(tokenString())
                .setOperatingSystem(new OperatingSystem(OperatingSystem.Values.LINUX));
    }

    protected VerificationToken verificationToken() {
        return new VerificationToken()
                .setId(1L)
                .setExpireDate(new ExpireDate(LocalDate.now().plusDays(15)))
                .setToken(tokenString());
    }

    protected TokenString tokenString() {
        return new TokenString("Z6XCS3tyyBlhPfsv7rMxLgfdyEOlUkv0CWSdbFYHCNX2wLlwpH97lJNP69Bny3XZ");
    }

    protected Category variableCategory() {
        return new Category()
                .setId(1L)
                .setUser(user())
                .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                .setName("Variable Category")
                .setParent(null);
    }

    protected Category variableCategoryParent() {
        return new Category()
                .setId(2L)
                .setUser(user())
                .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                .setName("Variable Category Parent")
                .setParent(null);
    }

    protected Category fixedCategory() {
        return new Category()
                .setId(2L)
                .setUser(user())
                .setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES))
                .setName("Fixed Category")
                .setParent(null);
    }

    protected Product product() {
        return new Product()
                .setId(1L)
                .setName("Test Product")
                .setQuantity(new Quantity(2))
                .setAmount(new Amount(50));
    }

    protected VariableTransaction variableTransaction() {
        return new VariableTransaction()
                .setId(1L)
                .setValueDate(new ValueDate())
                .setCategory(variableCategory())
                .setDescription("Test Purpose")
                .setVendor("Test Vendor")
                .addProduct(product());
    }

    protected Attachment attachment() {
        return new Attachment()
                .setId(1L)
                .setName("test.pdf")
                .setTransaction(variableTransaction())
                .setUploadDate(LocalDate.now());
    }

    protected FixedTransactionAmount fixedTransactionAmount() {
        return new FixedTransactionAmount()
                .setId(1L)
                .setAmount(new Amount(50))
                .setValueDate(new ValueDate());
    }

    protected FixedTransaction fixedTransaction() {
        return new FixedTransaction()
                .setId(2L)
                .setCategory(fixedCategory())
                .setTimeRange(new TimeRange())
                .setHasVariableAmounts(false)
                .setAmount(new Amount(50.0))
                .setDay(1)
                .setDescription("Test Purpose")
                .setVendor("Test Vendor")
                .addFixedTransactionAmount(fixedTransactionAmount());
    }
}
