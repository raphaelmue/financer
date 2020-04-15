package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.FixedTransactionAmount;
import org.financer.client.domain.model.transaction.Product;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.client.domain.model.user.User;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.api.category.CreateCategoryDTO;
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionAmountDTO;
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionAmountDTO;
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionDTO;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;
import org.financer.shared.domain.model.api.user.UpdatePersonalInformationDTO;
import org.financer.shared.domain.model.api.user.UpdateSettingsDTO;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.Quantity;

import java.util.List;

public class RestApiImpl implements RestApi {

    private final String baseUrl;

    public RestApiImpl(LocalStorage localStorage) {
        this.baseUrl = (String) localStorage.readObject("baseUrl");
    }

    public RestApiImpl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void createCategory(CreateCategoryDTO category, RestCallback<Category> callback) {

    }

    @Override
    public void updateCategory(Long categoryId, String name, CategoryClass.Values categoryClass, long parentId, RestCallback<Category> callback) {

    }

    @Override
    public void deleteCategory(Long categoryId, RestCallback<Void> callback) {

    }

    @Override
    public void createFixedTransaction(CreateFixedTransactionDTO fixedTransaction, RestCallback<FixedTransaction> callback) {

    }

    @Override
    public void updateFixedTransaction(Long transactionId, UpdateFixedTransactionDTO fixedTransaction, RestCallback<FixedTransaction> callback) {

    }

    @Override
    public void deleteFixedTransaction(Long transactionId, RestCallback<Void> callback) {

    }

    @Override
    public void createTransactionAmount(Long transactionId, CreateFixedTransactionAmountDTO transactionAmount, RestCallback<FixedTransactionAmount> callback) {

    }

    @Override
    public void updateTransactionAmount(Long transactionId, Long transactionAmountId, UpdateFixedTransactionAmountDTO transactionAmount, RestCallback<FixedTransactionAmount> callback) {

    }

    @Override
    public void deleteTransactionAmount(Long transactionId, Long transactionAmountId, RestCallback<Void> callback) {

    }

    @Override
    public void loginUser(String email, String password, RestCallback<User> callback) {

    }

    @Override
    public void deleteToken(Long userId, Long tokenId, RestCallback<User> callback) {

    }

    @Override
    public void registerUser(RegisterUserDTO registerUserDTO, RestCallback<User> callback) {

    }

    @Override
    public void updateUsersPassword(Long userId, String oldPassword, String newPassword, RestCallback<User> callback) {

    }

    @Override
    public void updateUsersPersonalInformation(Long userId, UpdatePersonalInformationDTO personalInformation, RestCallback<User> callback) {

    }

    @Override
    public void updateUsersSettings(Long userId, UpdateSettingsDTO setting, RestCallback<User> callback) {

    }

    @Override
    public void verifyUser(Long userId, String verificationToken, RestCallback<User> callback) {

    }

    @Override
    public void getUsersCategories(Long userId, RestCallback<List<Category>> callback) {

    }

    @Override
    public void getUsersVariableTransactions(Long userId, int page, RestCallback<List<VariableTransaction>> callback) {

    }

    @Override
    public void getUsersFixedTransactions(Long userId, RestCallback<List<FixedTransaction>> callback) {

    }

    @Override
    public void createVariableTransaction(VariableTransaction variableTransaction, RestCallback<VariableTransaction> callback) {

    }

    @Override
    public void updateVariableTransaction(Long transactionId, VariableTransaction variableTransaction, RestCallback<VariableTransaction> callback) {

    }

    @Override
    public void deleteVariableTransaction(Long transactionId, RestCallback<Void> callback) {

    }

    @Override
    public void createProduct(Long transactionId, String name, Amount amount, Quantity quantity, RestCallback<Product> callback) {

    }

    @Override
    public void deleteProduct(Long transactionId, Long productId, RestCallback<Void> callback) {

    }
}
