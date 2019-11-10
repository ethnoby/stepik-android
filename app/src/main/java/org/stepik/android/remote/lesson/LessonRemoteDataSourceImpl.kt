package org.stepik.android.remote.lesson

import io.reactivex.Single
import io.reactivex.functions.Function
import org.stepik.android.data.lesson.source.LessonRemoteDataSource
import org.stepik.android.model.Lesson
import org.stepik.android.remote.base.chunkedSingleMap
import org.stepik.android.remote.lesson.model.LessonResponse
import org.stepik.android.remote.lesson.service.LessonService
import retrofit2.Call
import javax.inject.Inject

class LessonRemoteDataSourceImpl
@Inject
constructor(
    private val lessonService: LessonService
) : LessonRemoteDataSource {
    private val lessonResponseMapper =
        Function<LessonResponse, List<Lesson>>(LessonResponse::lessons)

    override fun getLessons(vararg lessonIds: Long): Call<LessonResponse> =
        lessonService.getLessons(lessonIds)

    override fun getLessonsRx(vararg lessonIds: Long): Single<List<Lesson>> =
        lessonIds
            .chunkedSingleMap { ids ->
                lessonService.getLessonsRx(ids)
                    .map(lessonResponseMapper)
            }
}