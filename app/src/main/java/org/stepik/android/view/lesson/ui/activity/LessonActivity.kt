package org.stepik.android.view.lesson.ui.activity

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_lesson.*
import kotlinx.android.synthetic.main.empty_login.*
import kotlinx.android.synthetic.main.error_lesson_not_found.*
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import kotlinx.android.synthetic.main.view_centered_toolbar.*
import org.stepic.droid.R
import org.stepic.droid.base.App
import org.stepic.droid.base.FragmentActivityBase
import org.stepic.droid.ui.adapters.StepFragmentAdapter
import org.stepic.droid.ui.util.initCenteredToolbar
import org.stepic.droid.util.resolvers.StepTypeResolver
import org.stepik.android.domain.last_step.model.LastStep
import org.stepik.android.model.Lesson
import org.stepik.android.model.Section
import org.stepik.android.model.Unit
import org.stepik.android.presentation.lesson.LessonPresenter
import org.stepik.android.presentation.lesson.LessonView
import org.stepik.android.view.lesson.routing.getLessonDeepLinkData
import org.stepik.android.view.lesson.ui.delegate.LessonInfoTooltipDelegate
import org.stepik.android.view.ui.delegate.ViewStateDelegate
import javax.inject.Inject

class LessonActivity : FragmentActivityBase(), LessonView {
    companion object {
        private const val EXTRA_SECTION = "section"
        private const val EXTRA_UNIT = "unit"
        private const val EXTRA_LESSON = "lesson"

        private const val EXTRA_LAST_STEP = "last_step"

        fun createIntent(context: Context, section: Section, unit: Unit, lesson: Lesson): Intent =
            Intent(context, LessonActivity::class.java)
                .putExtra(EXTRA_SECTION, section)
                .putExtra(EXTRA_UNIT, unit)
                .putExtra(EXTRA_LESSON, lesson)

        fun createIntent(context: Context, lastStep: LastStep): Intent =
            Intent(context, LessonActivity::class.java)
                .putExtra(EXTRA_LAST_STEP, lastStep)
    }

    @Inject
    internal lateinit var stepTypeResolver: StepTypeResolver

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var lessonPresenter: LessonPresenter

    private lateinit var viewStateDelegate: ViewStateDelegate<LessonView.State>
    private lateinit var viewStepStateDelegate: ViewStateDelegate<LessonView.StepsState>

    private var infoMenuItem: MenuItem? = null
    private var isInfoMenuItemVisible: Boolean = false
        set(value) {
            field = value
            infoMenuItem?.isVisible = value
        }

    private lateinit var lessonInfoTooltipDelegate: LessonInfoTooltipDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        injectComponent()
        lessonPresenter = ViewModelProviders
            .of(this, viewModelFactory)
            .get(LessonPresenter::class.java)

        initCenteredToolbar(R.string.lesson_title, showHomeButton = true)

        viewStateDelegate = ViewStateDelegate()
        viewStateDelegate.addState<LessonView.State.Idle>(lessonPlaceholder)
        viewStateDelegate.addState<LessonView.State.Loading>(lessonPlaceholder)
        viewStateDelegate.addState<LessonView.State.LessonNotFound>(lessonNotFound)
        viewStateDelegate.addState<LessonView.State.EmptyLogin>(emptyLogin)
        viewStateDelegate.addState<LessonView.State.NetworkError>(errorNoConnection)
        viewStateDelegate.addState<LessonView.State.LessonLoaded>(lessonPager)

        viewStepStateDelegate = ViewStateDelegate()
        viewStepStateDelegate.addState<LessonView.StepsState.Idle>(lessonPlaceholder)
        viewStepStateDelegate.addState<LessonView.StepsState.Loading>(lessonPlaceholder)
        viewStepStateDelegate.addState<LessonView.StepsState.NetworkError>(errorNoConnection)
        viewStepStateDelegate.addState<LessonView.StepsState.EmptySteps>(emptyLesson)
        viewStepStateDelegate.addState<LessonView.StepsState.Loaded>(lessonPager)

        lessonInfoTooltipDelegate = LessonInfoTooltipDelegate(centeredToolbar)

        tryAgain.setOnClickListener { setDataToPresenter(forceUpdate = true) }
        goToCatalog.setOnClickListener { screenManager.showCatalog(this) }
        authAction.setOnClickListener { screenManager.showLaunchScreen(this) }

        setDataToPresenter()
    }

    private fun injectComponent() {
        App.component()
            .lessonComponentBuilder()
            .build()
            .inject(this)
    }

    private fun setDataToPresenter(forceUpdate: Boolean = false) {
        val lastStep = intent.getParcelableExtra<LastStep>(EXTRA_LAST_STEP)

        val lesson = intent.getParcelableExtra<Lesson>(EXTRA_LESSON)
        val unit = intent.getParcelableExtra<Unit>(EXTRA_UNIT)
        val section = intent.getParcelableExtra<Section>(EXTRA_SECTION)

        val deepLinkData = intent.getLessonDeepLinkData()

        when {
            lastStep != null ->
                lessonPresenter.onLastStep(lastStep, forceUpdate)

            deepLinkData != null ->
                lessonPresenter.onDeepLink(deepLinkData, forceUpdate)

            lesson != null && unit != null && section != null ->
                lessonPresenter.onLesson(lesson, unit, section, forceUpdate)

            else ->
                lessonPresenter.onEmptyData()
        }
    }

    override fun onStart() {
        super.onStart()
        lessonPresenter.attachView(this)
    }

    override fun onStop() {
        lessonPresenter.detachView(this)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lesson_menu, menu)
        infoMenuItem = menu.findItem(R.id.lesson_menu_item_info)
        infoMenuItem?.isVisible = isInfoMenuItemVisible
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.lesson_menu_item_info -> {
                lessonPresenter.onShowLessonInfoClicked(lessonPager.currentItem)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }

    override fun setState(state: LessonView.State) {
        viewStateDelegate.switchState(state)
        when (state) {
            is LessonView.State.LessonLoaded -> {
                viewStepStateDelegate.switchState(state.stepsState)
                centeredToolbarTitle.text = state.lessonData.lesson.title

                lessonPager.adapter =
                    if (state.stepsState is LessonView.StepsState.Loaded) {
                        StepFragmentAdapter(supportFragmentManager, state.stepsState.steps, stepTypeResolver)
                            .apply {
                                setDataIfNotNull(state.lessonData.lesson, state.lessonData.unit, state.lessonData.section)
                            }

                    } else {
                        null
                    }
            }
        }

        isInfoMenuItemVisible =
            state is LessonView.State.LessonLoaded &&
            state.stepsState is LessonView.StepsState.Loaded
    }

    override fun showLessonInfoTooltip(stepWorth: Long, lessonTimeToComplete: Long, certificateThreshold: Long) {
        lessonInfoTooltipDelegate
            .showLessonInfoTooltip(stepWorth, lessonTimeToComplete, certificateThreshold)
    }
}