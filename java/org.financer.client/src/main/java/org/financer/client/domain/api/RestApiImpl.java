package org.financer.client.domain.api;

import org.financer.client.connection.*;
import org.financer.client.domain.api.path.PathBuilder;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.FixedTransactionAmount;
import org.financer.client.domain.model.transaction.Product;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.client.domain.model.user.Setting;
import org.financer.client.domain.model.user.User;
import org.financer.shared.domain.model.api.category.CreateCategoryDTO;
import org.financer.shared.domain.model.api.category.UpdateCategoryDTO;
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionAmountDTO;
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionAmountDTO;
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.variable.CreateProductDTO;
import org.financer.shared.domain.model.api.transaction.variable.CreateVariableTransactionDTO;
import org.financer.shared.domain.model.api.transaction.variable.UpdateVariableTransactionDTO;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;
import org.financer.shared.domain.model.api.user.UpdatePersonalInformationDTO;
import org.financer.shared.domain.model.api.user.UpdateSettingsDTO;
import org.financer.shared.domain.model.value.objects.SettingPair;
import org.financer.util.concurrency.FinancerExecutor;
import org.financer.util.mapping.ModelMapperUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class RestApiImpl implements RestApi {

    private final ExecutorService executor;

    public RestApiImpl() {
        this(FinancerExecutor.getExecutor());
    }

    public RestApiImpl(ExecutorService executorService) {
        this.executor = executorService;
    }

    @Override
    public ServerRequestHandler createCategory(Category category, RestCallback<Category> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().categories().categoryId(category.getId()).build(),
                        ModelMapperUtils.map(category, CreateCategoryDTO.class)),
                Category.class,
                callback));
    }

    @Override
    public ServerRequestHandler updateCategory(Category category, RestCallback<Category> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Post(),
                        PathBuilder.start().categories().categoryId(category.getId()).build(),
                        ModelMapperUtils.map(category, UpdateCategoryDTO.class)),
                Category.class,
                callback));
    }

    @Override
    public ServerRequestHandler deleteCategory(Long categoryId, RestCallback<Void> callback) {
        return delete(PathBuilder.start().categories().categoryId(categoryId).build(), callback);
    }

    @Override
    public ServerRequestHandler createFixedTransaction(FixedTransaction fixedTransaction, RestCallback<FixedTransaction> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().fixedTransactions().build(),
                        ModelMapperUtils.map(fixedTransaction, CreateFixedTransactionDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler updateFixedTransaction(Long transactionId, FixedTransaction fixedTransaction,
                                                       RestCallback<FixedTransaction> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Post(),
                        PathBuilder.start().fixedTransactions().build(),
                        ModelMapperUtils.map(fixedTransaction, UpdateFixedTransactionDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler deleteFixedTransaction(Long transactionId, RestCallback<Void> callback) {
        return delete(PathBuilder.start().fixedTransactions().fixedTransactionId().build(), callback);
    }

    @Override
    public ServerRequestHandler createTransactionAmount(Long transactionId, FixedTransactionAmount transactionAmount,
                                                        RestCallback<FixedTransactionAmount> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().fixedTransactions().fixedTransactionId(transactionId).transactionAmounts().build(),
                        ModelMapperUtils.map(transactionAmount, CreateFixedTransactionAmountDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler updateTransactionAmount(Long transactionId, FixedTransactionAmount transactionAmount,
                                                        RestCallback<FixedTransactionAmount> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().fixedTransactions().fixedTransactionId(transactionId).
                                transactionAmounts().transactionAmountId(transactionAmount.getId()).build(),
                        ModelMapperUtils.map(transactionAmount, UpdateFixedTransactionAmountDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler deleteTransactionAmount(Long transactionId, Long transactionAmountId, RestCallback<Void> callback) {
        return delete(PathBuilder.start().fixedTransactions().fixedTransactionId(transactionId)
                .transactionAmounts().transactionAmountId(transactionAmountId).build(), callback);
    }

    @Override
    public ServerRequestHandler loginUser(String email, String password, RestCallback<User> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Get(),
                        PathBuilder.start().users().build(),
                        Map.of("email", email,
                                "password", password)),
                callback));
    }

    @Override
    public ServerRequestHandler deleteToken(Long userId, Long tokenId, RestCallback<Void> callback) {
        return delete(PathBuilder.start().users().userId(userId).tokens().tokenId(tokenId).build(), callback);
    }

    @Override
    public ServerRequestHandler registerUser(User user, RestCallback<User> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().users().build(),
                        ModelMapperUtils.map(user, RegisterUserDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler updateUsersPassword(Long userId, String oldPassword, String newPassword,
                                                    RestCallback<User> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Post(),
                        PathBuilder.start().users().userId(userId).password().build(),
                        Map.of("oldPassword", oldPassword,
                                "newPassword", newPassword)),
                callback));
    }

    @Override
    public ServerRequestHandler updateUsersPersonalInformation(User user, RestCallback<User> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().users().userId(user.getId()).personalInformation().build(),
                        ModelMapperUtils.map(user, UpdatePersonalInformationDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler updateUsersSettings(Long userId, Map<SettingPair.Property, Setting> settings,
                                                    RestCallback<User> callback) {
        Map<SettingPair.Property, String> settingsMap = new EnumMap<>(SettingPair.Property.class);
        settings.forEach((property, setting) -> settingsMap.put(property, setting.getPair().getValue()));
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Post(),
                        PathBuilder.start().users().userId(userId).password().build(),
                        new UpdateSettingsDTO()
                                .setSettings(settingsMap)),
                callback));
    }

    @Override
    public ServerRequestHandler getUsersCategories(Long userId, RestCallback<List<Category>> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Get(),
                        PathBuilder.start().users().userId(userId).categories().build()),
                callback));
    }

    @Override
    public ServerRequestHandler getUsersVariableTransactions(Long userId, int page, RestCallback<List<VariableTransaction>> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Get(),
                        PathBuilder.start().users().userId(userId).variableTransactions().build()),
                callback));
    }

    @Override
    public ServerRequestHandler getUsersFixedTransactions(Long userId, RestCallback<List<FixedTransaction>> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Get(),
                        PathBuilder.start().users().userId(userId).fixedTransactions().build()),
                callback));
    }

    @Override
    public ServerRequestHandler createVariableTransaction(VariableTransaction variableTransaction, RestCallback<VariableTransaction> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().variableTransactions().build(),
                        ModelMapperUtils.map(variableTransaction, CreateVariableTransactionDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler updateVariableTransaction(VariableTransaction variableTransaction,
                                                          RestCallback<VariableTransaction> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Post(),
                        PathBuilder.start().variableTransactions().variableTransactionId(variableTransaction.getId()).build(),
                        ModelMapperUtils.map(variableTransaction, UpdateVariableTransactionDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler deleteVariableTransaction(Long transactionId, RestCallback<Void> callback) {
        return delete(PathBuilder.start().variableTransactions().variableTransactionId(transactionId).build(), callback);
    }

    @Override
    public ServerRequestHandler createProduct(Long transactionId, Product product, RestCallback<Product> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(
                        new HttpMethod.Put(),
                        PathBuilder.start().variableTransactions().variableTransactionId(transactionId)
                                .products().build(),
                        ModelMapperUtils.map(product, CreateProductDTO.class)),
                callback));
    }

    @Override
    public ServerRequestHandler deleteProduct(Long transactionId, Long productId, RestCallback<Void> callback) {
        return delete(PathBuilder.start().variableTransactions().variableTransactionId(transactionId)
                .products().productId(productId).build(), callback);
    }

    private ServerRequestHandler delete(String path, RestCallback<Void> callback) {
        return new ServerRequestHandler(this.executor, new ServerRequest<>(
                new RequestConfig(new HttpMethod.Delete(), path), callback));
    }
}
