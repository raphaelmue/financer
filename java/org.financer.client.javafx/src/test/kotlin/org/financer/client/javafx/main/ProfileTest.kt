package org.financer.client.javafx.main

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXPasswordField
import com.jfoenix.controls.JFXTextField
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import org.financer.client.domain.model.user.User
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.util.Hash
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.Serializable

@Tag("integration")
class ProfileTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testChangePassword() {
        val newPassword = "newPassword"
        register(user(), password())
        clickOn(find<Node>("#profileTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#changePasswordLink") as Hyperlink)
        clickOn(find<Node>("#oldPasswordField") as JFXPasswordField)
        write(password())
        clickOn(find<Node>("#newPasswordField") as JFXPasswordField)
        write(newPassword)
        clickOn(find<Node>("#repeatNewPasswordField") as JFXPasswordField)
        write(newPassword)
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        Assertions.assertEquals(Hash.create(newPassword, (LocalStorageImpl.getInstance().readObject<Serializable>("user") as User?)!!.password!!.salt),
                (LocalStorageImpl.getInstance().readObject<Serializable>("user") as User?)!!.password!!.hashedPassword)
    }

    @Test
    fun testChangePersonalInformation() {
        val newName = "Peter"
        register(user(), password())
        clickOn(find<Node>("#profileTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#editPersonalInformationBtn") as JFXButton)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#nameField") as JFXTextField)
        eraseText(formatter!!.format(user().name!!)!!.length)
        write(newName)
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        Assertions.assertEquals(newName, (LocalStorageImpl.getInstance().readObject<Serializable>("user") as User?)!!.name!!.firstName)
        Assertions.assertEquals(newName + " " + user().name!!.surname, (find<Node>("#fullNameLabel") as Label).text)
    }
}