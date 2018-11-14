package org.stepik.android.data.course.source

import io.reactivex.Completable

interface EnrollmentCacheDataSource {
    fun addEnrollment(courseId: Long): Completable
    fun removeEnrollment(courseId: Long): Completable
}