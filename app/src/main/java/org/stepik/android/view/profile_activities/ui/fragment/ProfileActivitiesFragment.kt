package org.stepik.android.view.profile_activities.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.nobird.android.view.base.ui.extension.argument
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import org.stepic.droid.R
import org.stepic.droid.base.App
import org.stepik.android.presentation.profile_activities.ProfileActivitiesPresenter
import org.stepik.android.presentation.profile_activities.ProfileActivitiesView
import org.stepik.android.view.ui.delegate.ViewStateDelegate
import javax.inject.Inject

class ProfileActivitiesFragment : Fragment(), ProfileActivitiesView {
    companion object {
        fun newInstance(userId: Long): Fragment =
            ProfileActivitiesFragment()
                .apply {
                    this.userId = userId
                }
    }

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var userId by argument<Long>()

    private lateinit var profileActivitiesPresenter: ProfileActivitiesPresenter


    private lateinit var viewStateDelegate: ViewStateDelegate<ProfileActivitiesView.State>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectComponent()

        profileActivitiesPresenter = ViewModelProviders
            .of(this, viewModelFactory)
            .get(ProfileActivitiesPresenter::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_profile_activities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    private fun injectComponent() {
        App.componentManager()
            .profileComponent(userId)
            .inject(this)
    }

    override fun onStart() {
        super.onStart()

        profileActivitiesPresenter.attachView(this)
    }

    override fun onStop() {
        profileActivitiesPresenter.detachView(this)
        super.onStop()
    }

    override fun setState(state: ProfileActivitiesView.State) {
        when (state) {

        }
    }

    override fun showNetworkError() {

    }
}