package org.stepik.android.domain.auth.interactor

import io.reactivex.Completable
import io.reactivex.Single
import org.stepic.droid.analytic.AmplitudeAnalytic
import org.stepic.droid.analytic.Analytic
import org.stepic.droid.model.Credentials
import org.stepic.droid.util.AppConstants
import org.stepic.droid.util.DateTimeHelper
import ru.nobird.android.domain.rx.doCompletableOnSuccess
import org.stepik.android.domain.auth.model.SocialAuthType
import org.stepik.android.domain.auth.repository.AuthRepository
import org.stepik.android.domain.course.repository.CourseRepository
import org.stepik.android.domain.user_profile.repository.UserProfileRepository
import org.stepik.android.model.user.RegistrationCredentials
import javax.inject.Inject

class AuthInteractor
@Inject
constructor(
    private val analytic: Analytic,
    private val authRepository: AuthRepository,

    private val userProfileRepository: UserProfileRepository,
    private val courseRepository: CourseRepository
) {
    companion object {
        private const val MINUTES_TO_CONSIDER_REGISTRATION = 5
    }

    fun createAccount(credentials: RegistrationCredentials): Single<Credentials> =
        authRepository
            .createAccount(credentials)
            .toSingleDefault(Credentials(credentials.email, credentials.password))

    fun authWithCredentials(credentials: Credentials, isRegistration: Boolean): Single<Credentials> =
        authRepository
            .authWithLoginPassword(credentials.login, credentials.password)
            .map { credentials }
            .doOnSuccess {
                val event = if (isRegistration) AmplitudeAnalytic.Auth.REGISTERED else AmplitudeAnalytic.Auth.LOGGED_ID
                analytic.reportAmplitudeEvent(event, mapOf(AmplitudeAnalytic.Auth.PARAM_SOURCE to AmplitudeAnalytic.Auth.VALUE_SOURCE_EMAIL))
            }
            .doCompletableOnSuccess { courseRepository.removeCachedCourses() }

    fun authWithNativeCode(code: String, type: SocialAuthType, email: String? = null): Completable =
        authRepository
            .authWithNativeCode(code, type, email)
            .flatMapCompletable {
                reportSocialAuthAnalytics(type)
            }
            .andThen(courseRepository.removeCachedCourses())

    fun authWithCode(code: String, type: SocialAuthType): Completable =
        authRepository
            .authWithCode(code)
            .flatMapCompletable {
                reportSocialAuthAnalytics(type)
            }
            .andThen(courseRepository.removeCachedCourses())

    private fun reportSocialAuthAnalytics(type: SocialAuthType): Completable =
        userProfileRepository
            .getUserProfile()
            .map { (user, _) ->
                user?.joinDate
                    ?.let {
                        if (DateTimeHelper.nowUtc() - it.time < MINUTES_TO_CONSIDER_REGISTRATION * AppConstants.MILLIS_IN_1MINUTE) {
                            AmplitudeAnalytic.Auth.REGISTERED
                        } else {
                            AmplitudeAnalytic.Auth.LOGGED_ID
                        }
                    }
                    ?: AmplitudeAnalytic.Auth.LOGGED_ID
            }
            .onErrorReturnItem(AmplitudeAnalytic.Auth.LOGGED_ID)
            .doOnSuccess { event ->
                analytic.reportAmplitudeEvent(event, mapOf(AmplitudeAnalytic.Auth.PARAM_SOURCE to type.identifier))
            }
            .ignoreElement()
}