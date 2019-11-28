package org.stepik.android.view.profile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.empty_login.*
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.header_profile.*
import org.stepic.droid.R
import org.stepic.droid.base.App
import org.stepic.droid.core.ScreenManager
import org.stepic.droid.ui.util.snackbar
import org.stepik.android.presentation.profile.ProfilePresenter
import org.stepik.android.presentation.profile.ProfileView
import org.stepik.android.view.ui.delegate.ViewStateDelegate
import ru.nobird.android.view.base.ui.extension.argument
import javax.inject.Inject
import kotlin.math.min

class ProfileFragment : Fragment(), ProfileView {
    companion object {
        fun newInstance(userId: Long): Fragment =
            ProfileFragment()
                .apply {
                    this.userId = userId
                }
    }

    @Inject
    internal lateinit var screenManager: ScreenManager

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var profilePresenter: ProfilePresenter

    private var userId by argument<Long>()

    private lateinit var viewStateDelegate: ViewStateDelegate<ProfileView.State>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        injectComponent()

        profilePresenter = ViewModelProviders
            .of(this, viewModelFactory)
            .get(ProfilePresenter::class.java)
        profilePresenter.onData(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewStateDelegate = ViewStateDelegate()
        viewStateDelegate.addState<ProfileView.State.Idle>()
        viewStateDelegate.addState<ProfileView.State.Loading>(profileLoading)
        viewStateDelegate.addState<ProfileView.State.Content>(scrollContainer)
        viewStateDelegate.addState<ProfileView.State.Empty>(profileEmpty)
        viewStateDelegate.addState<ProfileView.State.EmptyLogin>(profileEmptyLogin)
        viewStateDelegate.addState<ProfileView.State.NetworkError>(profileNetworkError)

        (activity as? AppCompatActivity)
            ?.apply { setSupportActionBar(toolbar) }
            ?.supportActionBar
            ?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(false)
            }

        ViewCompat.setElevation(header, resources.getDimension(R.dimen.profile_header_elevation))

        scrollContainer.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            ViewCompat.setElevation(appbar, if (scrollY > header.height) ViewCompat.getElevation(header) else 0f)

            val scroll = min(toolbar.height, scrollY)
            toolbarTitle.translationY = toolbar.height.toFloat() - scroll
        }

        profileKnowledgeRank.isVisible = false
        profileReputationRank.isVisible = false

        tryAgain.setOnClickListener { profilePresenter.onData(userId, forceUpdate = true) }
        authAction.setOnClickListener { screenManager.showLaunchScreen(context) }

//        profileKnowledgeRank.text =
//            buildSpannedString {
//                append("Знания ")
//                bold { append("2132") }
//                append(" (25й по знаниям)")
//            }
//        profileReputationRank.text =
//            buildSpannedString {
//                append("Репутация ")
//                bold { append("132") }
//                append(" (250й по репутации)")
//            }
    }

    private fun injectComponent() {
        App.component()
            .profileComponentBuilderNew()
            .build()
            .inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onStart() {
        super.onStart()
        profilePresenter.attachView(this)
    }

    override fun onStop() {
        profilePresenter.detachView(this)
        super.onStop()
    }

    override fun setState(state: ProfileView.State) {
        viewStateDelegate.switchState(state)

        when (state) {
            is ProfileView.State.Content -> {
                with(state.profileData) {
                    Glide
                        .with(this@ProfileFragment)
                        .load(user.avatar)
                        .placeholder(R.drawable.general_placeholder)
                        .into(profileImage)

                    profileName.text = user.fullName
                    profileBio.text = user.shortBio
                    profileBio.isVisible = !user.shortBio.isNullOrBlank()

                    toolbarTitle.text = user.fullName
                    toolbarTitle.translationY = 1000f
                }
            }

            else -> {
                toolbarTitle.setText(R.string.profile_title)
                toolbarTitle.translationY = 0f
            }
        }
    }

    override fun showNetworkError() {
        view?.snackbar(messageRes = R.string.connectionProblems)
    }
}