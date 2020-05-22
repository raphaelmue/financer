package org.financer.client.domain.api

import org.financer.client.connection.RequestConfig
import org.financer.client.connection.ServerRequest
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.transaction.*
import org.financer.client.domain.model.user.Setting
import org.financer.client.domain.model.user.User
import org.financer.shared.domain.model.api.category.CreateCategoryDTO
import org.financer.shared.domain.model.api.category.UpdateCategoryDTO
import org.financer.shared.domain.model.api.transaction.AttachmentWithContentDTO
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionAmountDTO
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionDTO
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionAmountDTO
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionDTO
import org.financer.shared.domain.model.api.transaction.variable.CreateProductDTO
import org.financer.shared.domain.model.api.transaction.variable.CreateVariableTransactionDTO
import org.financer.shared.domain.model.api.transaction.variable.UpdateVariableTransactionDTO
import org.financer.shared.domain.model.api.user.RegisterUserDTO
import org.financer.shared.domain.model.api.user.UpdatePersonalInformationDTO
import org.financer.shared.domain.model.api.user.UpdateSettingsDTO
import org.financer.shared.domain.model.value.objects.HashedPassword
import org.financer.shared.domain.model.value.objects.SettingPair
import org.financer.shared.path.Path
import org.financer.shared.path.PathBuilder
import org.financer.util.mapping.ModelMapperUtils
import java.util.*

class RestApiImpl : RestApi {
    override suspend fun createCategory(category: Category): Category? {
        return ServerRequest(RequestConfig(
                PathBuilder.Put().categories().categoryId(category.id).build(),
                ModelMapperUtils.map(category, CreateCategoryDTO::class.java))).execute()
    }

    override suspend fun updateCategory(category: Category): Category? {
        return ServerRequest(RequestConfig(
                PathBuilder.Post().categories().categoryId(category.id).build(),
                ModelMapperUtils.map(category, UpdateCategoryDTO::class.java))).execute();
    }

    override suspend fun deleteCategory(categoryId: Long) {
        return delete(PathBuilder.Delete().categories().categoryId(categoryId).build())
    }

    override suspend fun createFixedTransaction(fixedTransaction: FixedTransaction): FixedTransaction? {
        return ServerRequest(
                RequestConfig(
                        PathBuilder.Put().fixedTransactions().build(),
                        ModelMapperUtils.map(fixedTransaction, CreateFixedTransactionDTO::class.java))).execute()
    }

    override suspend fun updateFixedTransaction(fixedTransaction: FixedTransaction): FixedTransaction? {
        return ServerRequest(RequestConfig(
                PathBuilder.Post().fixedTransactions().fixedTransactionId(fixedTransaction.id).build(),
                ModelMapperUtils.map(fixedTransaction, UpdateFixedTransactionDTO::class.java))).execute()
    }

    override suspend fun deleteFixedTransaction(transactionId: Long) {
        delete(PathBuilder.start().fixedTransactions().fixedTransactionId().build())
    }

    override suspend fun createTransactionAmount(transactionId: Long, transactionAmount: FixedTransactionAmount): FixedTransactionAmount? {
        return ServerRequest(RequestConfig(
                PathBuilder.Put().fixedTransactions().fixedTransactionId(transactionId).transactionAmounts().build(),
                ModelMapperUtils.map(transactionAmount, CreateFixedTransactionAmountDTO::class.java))).execute()
    }

    override suspend fun updateTransactionAmount(transactionId: Long, transactionAmount: FixedTransactionAmount): FixedTransactionAmount? {
        return ServerRequest(RequestConfig(PathBuilder.Put().fixedTransactions().fixedTransactionId(transactionId).transactionAmounts().transactionAmountId(transactionAmount.id).build(),
                ModelMapperUtils.map(transactionAmount, UpdateFixedTransactionAmountDTO::class.java))).execute()
    }

    override suspend fun deleteTransactionAmount(transactionId: Long, transactionAmountId: Long) {
        delete(PathBuilder.start().fixedTransactions().fixedTransactionId(transactionId).transactionAmounts().transactionAmountId(transactionAmountId).build())
    }

    override suspend fun loginUser(email: String, password: String): User? {
        return ServerRequest(RequestConfig(PathBuilder.Get().users().build(),
                parameters = mapOf("email" to email, "password" to password))).execute()
    }

    override suspend fun deleteToken(userId: Long, tokenId: Long) {
        delete(PathBuilder.start().users().userId(userId).tokens().tokenId(tokenId).build())
    }

    override suspend fun registerUser(user: User): User? {
        return ServerRequest(RequestConfig(PathBuilder.Put().users().build(),
                ModelMapperUtils.map(user, RegisterUserDTO::class.java))).execute()
    }

    override suspend fun updateUsersPassword(userId: Long, newPassword: HashedPassword): User? {
        return ServerRequest(RequestConfig(PathBuilder.Post().users().userId(userId).password().build(),
                newPassword)).execute()
    }

    override suspend fun updateUsersPersonalInformation(user: User): User? {
        return ServerRequest(
                RequestConfig(
                        PathBuilder.Put().users().userId(user.id!!).personalInformation().build(),
                        ModelMapperUtils.map(user, UpdatePersonalInformationDTO::class.java))).execute()
    }

    override suspend fun updateUsersSettings(userId: Long, settings: Map<SettingPair.Property, Setting>): User? {
        val settingsMap: MutableMap<SettingPair.Property, String> = EnumMap(SettingPair.Property::class.java)
        settings.forEach { (property: SettingPair.Property, setting: Setting) -> settingsMap[property] = setting.pair!!.value }
        return ServerRequest(
                RequestConfig(
                        PathBuilder.Post().users().userId(userId).password().build(),
                        UpdateSettingsDTO()
                                .setSettings(settingsMap))).execute()
    }

    override suspend fun getUsersCategories(userId: Long): List<Category> {
        return ServerRequest(RequestConfig(
                PathBuilder.Get().users().userId(userId).categories().build())).execute() ?: emptyList()
    }

    override suspend fun getUsersVariableTransactions(userId: Long, page: Int): List<VariableTransaction> {
        return ServerRequest(RequestConfig(PathBuilder.Get().users().userId(userId).variableTransactions().build())).execute()
                ?: emptyList()
    }

    override suspend fun getUsersFixedTransactions(userId: Long): List<FixedTransaction> {
        return ServerRequest(RequestConfig(
                PathBuilder.Get().users().userId(userId).fixedTransactions().build())).execute() ?: emptyList()
    }

    override suspend fun createVariableTransaction(variableTransaction: VariableTransaction): VariableTransaction? {
        return ServerRequest(RequestConfig(
                PathBuilder.Put().variableTransactions().build(),
                ModelMapperUtils.map(variableTransaction, CreateVariableTransactionDTO::class.java))).execute()
    }

    override suspend fun updateVariableTransaction(variableTransaction: VariableTransaction): VariableTransaction? {
        return ServerRequest(RequestConfig(
                PathBuilder.Post().variableTransactions().variableTransactionId(variableTransaction.id).build(),
                ModelMapperUtils.map(variableTransaction, UpdateVariableTransactionDTO::class.java))).execute()
    }

    override suspend fun deleteVariableTransaction(transactionId: Long) {
        delete(PathBuilder.start().variableTransactions().variableTransactionId(transactionId).build())
    }

    override suspend fun createProduct(transactionId: Long, product: Product): Product? {
        return ServerRequest(RequestConfig(
                PathBuilder.Put().variableTransactions().variableTransactionId(transactionId).products().build(),
                ModelMapperUtils.map(product, CreateProductDTO::class.java))).execute()
    }

    override suspend fun deleteProduct(transactionId: Long, productId: Long) {
        return delete(PathBuilder.Delete().variableTransactions().variableTransactionId(transactionId)
                .products().productId(productId).build())
    }

    private suspend fun delete(path: Path) {
        ServerRequest(RequestConfig(path)).execute<Any?>()
    }

    override suspend fun createAttachment(transactionId: Long, attachment: Attachment): Attachment? {
        return ServerRequest(RequestConfig(
                PathBuilder.Put().variableTransactions().variableTransactionId(1).attachments().build(),
                ModelMapperUtils.map(attachment, AttachmentWithContentDTO::class.java))).execute()
    }

    override suspend fun getAttachment(transactionId: Long, attachmentId: Long): Attachment? {
        return ServerRequest(RequestConfig(PathBuilder.Put().variableTransactions().variableTransactionId(1).attachments().build())).execute()
    }

    override suspend fun deleteAttachment(transactionId: Long, attachmentId: Long) {
        return delete(PathBuilder.Delete().variableTransactions().variableTransactionId(transactionId).attachments().attachmentId(attachmentId).build())
    }

}