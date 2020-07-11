package org.financer.client.javafx.main

import javafx.scene.Node
import org.financer.client.domain.model.user.User
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.javafx.local.LocalStorageImpl.LocalStorageFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
class LoginApplicationTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testInvalidLogin() {
        login(user(), "wrong")
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertTrue(find<Node>("#loginErrorLabel").isVisible)
    }

    @Test
    fun testRegisterUser() {
        val user = user()
        register(user!!, password())
        val storedUser = LocalStorageImpl.getInstance().readObject<User>("user")
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertEquals(user.email, storedUser!!.email)
        Assertions.assertEquals(user.name, storedUser.name)
        Assertions.assertEquals(user.birthDate, storedUser.birthDate)
        Assertions.assertTrue(user.getId() > 0)
    }

    @Test
    @Throws(Exception::class)
    fun testLogin() {
        val user = user()
        register(user!!, password())
        logout()
        login(user, password())
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        val loggedInUser = LocalStorageImpl.getInstance().readObject<User>("user")
        Assertions.assertNotNull(loggedInUser)
        Assertions.assertEquals(user.email, loggedInUser!!.email)
    }

    @Test
    @Throws(Exception::class)
    fun testLogout() {
        val user = user()
        register(user!!, password())
        logout()
        for (file in LocalStorageFile.values()) {
            for (key in file.keys) {
                if (key != "localSettings") {
                    Assertions.assertNull(LocalStorageImpl.getInstance().readObject(key))
                }
            }
        }
    }
}