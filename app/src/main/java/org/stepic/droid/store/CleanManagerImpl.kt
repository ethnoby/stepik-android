package org.stepic.droid.store

import android.content.Context
import android.content.Intent
import android.os.Parcelable

import org.stepic.droid.model.Lesson
import org.stepic.droid.model.Section
import org.stepic.droid.model.Step
import org.stepic.droid.services.DeleteService
import org.stepic.droid.services.LoadService
import org.stepic.droid.util.AppConstants

import java.io.Serializable

class CleanManagerImpl(private val context: Context) : CleanManager {

    override fun removeSection(section: Section?) {
        if (section == null) {
            return
        }

        val loadIntent = Intent(context, DeleteService::class.java)

        loadIntent.putExtra(AppConstants.KEY_LOAD_TYPE, LoadService.LoadTypeKey.Section)
        loadIntent.putExtra(AppConstants.KEY_SECTION_BUNDLE, section as Serializable)

        context.startService(loadIntent)
    }


    override fun removeLesson(lesson: Lesson?) {
        if (lesson == null) {
            return
        }

        val loadIntent = Intent(context, DeleteService::class.java)

        loadIntent.putExtra(AppConstants.KEY_LOAD_TYPE, LoadService.LoadTypeKey.Lesson)
        loadIntent.putExtra(AppConstants.KEY_LESSON_BUNDLE, lesson as Parcelable)

        context.startService(loadIntent)

    }

    override fun removeStep(step: Step?) {
        if (step == null) {
            return
        }

        val loadIntent = Intent(context, DeleteService::class.java)

        loadIntent.putExtra(AppConstants.KEY_LOAD_TYPE, LoadService.LoadTypeKey.Step)
        loadIntent.putExtra(AppConstants.KEY_STEP_BUNDLE, step as Serializable)

        context.startService(loadIntent)
    }


}
