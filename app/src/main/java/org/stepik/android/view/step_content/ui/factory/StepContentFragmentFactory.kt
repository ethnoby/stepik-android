package org.stepik.android.view.step_content.ui.factory

import androidx.fragment.app.Fragment
import org.stepic.droid.persistence.model.StepPersistentWrapper

interface StepContentFragmentFactory {
    fun createStepContentFragment(stepPersistentWrapper: StepPersistentWrapper): Fragment
}