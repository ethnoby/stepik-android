package org.stepik.android.view.course_reviews.ui.dialog

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.View
import org.stepic.droid.R
import org.stepic.droid.base.App
import org.stepic.droid.util.setTextColor
import org.stepik.android.domain.course_reviews.model.CourseReview
import org.stepik.android.presentation.course_reviews.ComposeCourseReviewPresenter
import org.stepik.android.presentation.course_reviews.ComposeCourseReviewView
import org.stepik.android.view.ui.delegate.ViewStateDelegate
import javax.inject.Inject

class ComposeCourseReviewDialogFragment : DialogFragment(), ComposeCourseReviewView {
    companion object {
        const val TAG = "ComposeCourseReviewDialogFragment"
        const val REQUEST_CODE = 3412

        const val ARG_COURSE_REVIEW = "course_review"

        fun newInstance(courseReview: CourseReview?): DialogFragment =
            ComposeCourseReviewDialogFragment().apply {
                arguments = Bundle(1)
                    .also {
                        it.putParcelable(ARG_COURSE_REVIEW, courseReview)
                    }
            }
    }

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var composeCourseReviewPresenter: ComposeCourseReviewPresenter
    private lateinit var viewStateDelegate: ViewStateDelegate<ComposeCourseReviewView.State>

    private val courseReview: CourseReview? by lazy { arguments?.getParcelable<CourseReview>(ARG_COURSE_REVIEW) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectComponent()

        composeCourseReviewPresenter = ViewModelProviders
            .of(this, viewModelFactory)
            .get(ComposeCourseReviewPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewStateDelegate = ViewStateDelegate()
    }

    private fun injectComponent() {
        App.component()
            .composeCourseReviewComponent()
            .build()
            .inject(this)
    }

    override fun setState(state: ComposeCourseReviewView.State) {
        viewStateDelegate.switchState(state)

        if (state is ComposeCourseReviewView.State.Complete) {
            targetFragment
                ?.onActivityResult(
                    REQUEST_CODE,
                    Activity.RESULT_OK,
                    Intent().putExtra(ARG_COURSE_REVIEW, state.courseReview)
                )
        }
    }

    override fun showNetworkError() {
        val view = view
            ?: return

        Snackbar
            .make(view, R.string.connectionProblems, Snackbar.LENGTH_SHORT)
            .setTextColor(ContextCompat.getColor(view.context, R.color.white))
            .show()
    }
}