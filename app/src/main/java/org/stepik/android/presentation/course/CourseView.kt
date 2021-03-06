package org.stepik.android.presentation.course

import org.stepik.android.domain.course.model.CourseHeaderData
import org.stepik.android.model.Course
import org.stepik.android.presentation.billing.BillingView
import org.stepik.android.presentation.course.model.EnrollmentError
import org.stepik.android.presentation.course_continue.CourseContinueView
import org.stepik.android.presentation.user_courses.model.UserCourseAction

interface CourseView : CourseContinueView, BillingView {
    sealed class State {
        object Idle : State()
        object Loading : State()
        object NetworkError : State()
        object EmptyCourse : State()

        class CourseLoaded(val courseHeaderData: CourseHeaderData) : State()
        class BlockingLoading(val courseHeaderData: CourseHeaderData) : State()
    }

    fun setState(state: State)

    fun showEmptyAuthDialog(course: Course)
    fun showEnrollmentError(errorType: EnrollmentError)

    fun showSaveUserCourseSuccess(userCourseAction: UserCourseAction)
    fun showSaveUserCourseError(userCourseAction: UserCourseAction)

    fun shareCourse(course: Course)
    fun showCourseShareTooltip()

    fun openCoursePurchaseInWeb(courseId: Long, queryParams: Map<String, List<String>>? = null)

    fun showTrialLesson(lessonId: Long)
}
