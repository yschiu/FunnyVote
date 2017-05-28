package com.heaton.funnyvote

import com.heaton.funnyvote.data.user.UserManager
import com.heaton.funnyvote.database.User
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.*
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class MainPagePresenterTest {
    @Mock lateinit var userManager: UserManager
    @Mock lateinit var view: MainPageView
    lateinit var presenter: MainPagePresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = MainPagePresenter(userManager)
        presenter.attachView(view)
    }

    @Test
    fun testGoToHomePage() {
        presenter.gotoHomePage()

        verify(presenter.view).showHomePage()
    }

    @Test
    fun testGoToAboutPage() {
        presenter.gotoAboutPage()

        verify(presenter.view).showAboutPage()
    }

    @Test
    fun testGoToAccountPage() {
        presenter.gotoAccountPage()

        verify(presenter.view).showAccountPage()
    }

    @Test
    fun testGoToCreateVotePage() {
        presenter.gotoCreateVotePage()

        verify(presenter.view).showCreateVotePage()
    }

    @Test
    fun testGoToMyBoxPage() {
        presenter.gotoMyBoxPage()

        verify(presenter.view).showMyBoxPage()
    }

    @Test
    fun testGoToMySearchPage() {
        presenter.gotoSearchPage("")

        verify(presenter.view).showSearchPage(anyString())
    }

    @Test
    fun testRefreshUserProfile() {
        val captor: ArgumentCaptor<UserManager.GetUserCallback> =
                ArgumentCaptor.forClass(UserManager.GetUserCallback::class.java)
        val mockUser: User = mock(User::class.java)

        presenter.refreshUserProfile()

        verify(userManager).getUser(captor.capture(), eq(false))

        captor.value.onResponse(mockUser)

        verify(presenter.view).updateUserProfile(mockUser)
    }
}