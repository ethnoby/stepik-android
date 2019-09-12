package org.stepik.android.view.injection.step

import dagger.Subcomponent
import org.stepik.android.view.injection.attempt.AttemptDataModule
import org.stepik.android.view.injection.step_content.StepContentModule
import org.stepik.android.view.injection.step_quiz.StepQuizModule
import org.stepik.android.view.injection.step_quiz.StepQuizPresentationModule
import org.stepik.android.view.injection.submission.SubmissionDataModule
import org.stepik.android.view.step.ui.fragment.StepFragment
import org.stepik.android.view.step_quiz.ui.fragment.DefaultStepQuizFragment
import org.stepik.android.view.step_quiz_code.ui.fragment.CodeStepQuizFragment
import org.stepik.android.view.step_quiz_sql.ui.fragment.SqlStepQuizFragment
import org.stepik.android.view.step_quiz_fullscreen_code.ui.dialog.CodeStepQuizFullScreenDialogFragment
import org.stepik.android.view.step_quiz_unsupported.ui.fragment.UnsupportedStepQuizFragment

@Subcomponent(modules = [
    StepModule::class,
    StepContentModule::class,
    StepQuizModule::class,

    StepQuizPresentationModule::class,
    AttemptDataModule::class,
    SubmissionDataModule::class
])
interface StepComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): StepComponent
    }

    fun inject(stepFragment: StepFragment)

    fun inject(codeStepQuizFragment: CodeStepQuizFragment)
    fun inject(defaultStepQuizFragment: DefaultStepQuizFragment)
    fun inject(unsupportedStepQuizFragment: UnsupportedStepQuizFragment)
    fun inject(sqlStepQuizFragment: SqlStepQuizFragment)

    fun inject(codeStepQuizFullScreenDialogFragment: CodeStepQuizFullScreenDialogFragment)
}