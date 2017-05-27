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
    @Mock var userManager: UserManager? = null
    @Mock var view: MainPageView? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testGoToHomePage() {
        val presenter = preparePresenter()

        presenter.gotoHomePage()

        verify(presenter.view).showHomePage()
    }

    @Test
    fun testGoToAboutPage() {
        val presenter = preparePresenter()

        presenter.gotoAboutPage()

        verify(presenter.view).showAboutPage()
    }

    @Test
    fun testGoToAccountPage() {
        val presenter = preparePresenter()

        presenter.gotoAccountPage()

        verify(presenter.view).showAccountPage()
    }

    @Test
    fun testGoToCreateVotePage() {
        val presenter = preparePresenter()

        presenter.gotoCreateVotePage()

        verify(presenter.view).showCreateVotePage()
    }

    @Test
    fun testGoToMyBoxPage() {
        val presenter = preparePresenter()

        presenter.gotoMyBoxPage()

        verify(presenter.view).showMyBoxPage()
    }

    @Test
    fun testGoToMySearchPage() {
        val presenter = preparePresenter()

        presenter.gotoSearchPage("")

        verify(presenter.view).showSearchPage(anyString())
    }

    @Test
    fun testRefreshUserProfile() {
        val presenter = preparePresenter()
        val captor: ArgumentCaptor<UserManager.GetUserCallback> =
                ArgumentCaptor.forClass(UserManager.GetUserCallback::class.java)
        val mockUser: User = mock(User::class.java)

        presenter.refreshUserProfile()

        verify(userManager!!).getUser(captor.capture(), eq(false))

        captor.value.onResponse(mockUser)

        verify(presenter.view).updateUserProfile(mockUser)
    }

    fun preparePresenter(): MainPagePresenter {
        val presenter: MainPagePresenter = MainPagePresenter(userManager!!)
        presenter.attachView(view!!)
        return presenter
    }
}