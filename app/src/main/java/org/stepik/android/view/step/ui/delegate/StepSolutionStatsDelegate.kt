package org.stepik.android.view.step.ui.delegate

import android.view.View
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.view_step_solution_stats.view.*
import org.stepic.droid.R
import org.stepik.android.model.Step

class StepSolutionStatsDelegate(
    containerView: View,
    step: Step,
    isHasQuiz: Boolean

) {
    private val context = containerView.context
    private val solvedAmount     = containerView.stepAmountPassed
    private val solvedPercentage = containerView.stepSolvedPercentage

    init {
        containerView.isVisible = false

        run {
            if (!isHasQuiz) return@run

            val correctRatio = step.correctRatio ?: return@run

            containerView.isVisible = true

            solvedAmount.text = buildSpannedString {
                append(context.resources.getString(R.string.step_amount_passed))
                bold { append(step.passedBy.toString()) }
            }

            solvedPercentage.text = buildSpannedString {
                append(context.resources.getString(R.string.step_correct_submissions_percentage))
                bold {
                    append(context.resources.getString(R.string.percent_symbol, (correctRatio * 100).toInt()))
                }
            }
        }
    }
}