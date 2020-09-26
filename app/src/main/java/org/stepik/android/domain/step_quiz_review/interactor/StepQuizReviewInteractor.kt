package org.stepik.android.domain.step_quiz_review.interactor

import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import org.stepik.android.domain.assignment.repository.AssignmentRepository
import org.stepik.android.domain.base.DataSourceType
import org.stepik.android.domain.progress.repository.ProgressRepository
import org.stepik.android.domain.review_instruction.model.ReviewInstruction
import org.stepik.android.domain.review_instruction.repository.ReviewInstructionRepository
import org.stepik.android.domain.review_session.model.ReviewSessionData
import org.stepik.android.domain.review_session.repository.ReviewSessionRepository
import org.stepik.android.model.Progress
import javax.inject.Inject

class StepQuizReviewInteractor
@Inject
constructor(
    private val reviewSessionRepository: ReviewSessionRepository,
    private val reviewInstructionRepository: ReviewInstructionRepository,
    private val assignmentRepository: AssignmentRepository,
    private val progressRepository: ProgressRepository
) {
    fun createSession(submissionId: Long): Single<ReviewSessionData> =
        reviewSessionRepository.createReviewSession(submissionId)

    fun getReviewSession(stepId: Long, unitId: Long?, instructionId: Long, sessionId: Long): Single<Triple<ReviewInstruction, ReviewSessionData, List<Progress>>> =
        Singles
            .zip(
                getInstruction(instructionId),
                reviewSessionRepository.getReviewSession(sessionId, sourceType = DataSourceType.REMOTE),
                getAssignmentProgress(stepId, unitId)
            )

    fun getInstruction(instructionId: Long): Single<ReviewInstruction> =
        reviewInstructionRepository.getReviewInstruction(instructionId, sourceType = DataSourceType.REMOTE)

    private fun getAssignmentProgress(stepId: Long, unitId: Long?): Single<List<Progress>> =
        if (unitId != null) { // fetch progress from cache as we already load it in StepItem
            assignmentRepository
                .getAssignmentByUnitAndStep(unitId, stepId)
                .flatMapSingleElement { progressRepository.getProgresses(listOfNotNull(it.progress), primarySourceType = DataSourceType.CACHE) }
                .toSingle(emptyList())
        } else {
            Single.just(emptyList())
        }
}