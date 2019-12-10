package org.stepik.android.presentation.profile_old

import org.stepik.android.domain.achievement.model.AchievementItem
import org.stepic.droid.model.UserViewModel

interface ProfileView {
    sealed class State {
        object Idle : State()
        object NetworkError : State()
        object UserNotFoundError : State()
        object NeedAuthError : State()
        object Loading : State()
        data class ProfileLoaded(val userLocalViewModel: UserViewModel) : State()
    }

    fun setState(state: State)

    // Achievements related
    fun showAchievements(achievements: List<AchievementItem>)
    fun onAchievementsLoadingError()
    fun onAchievementsLoading()

    // Streak related
    fun showNotificationEnabledState(notificationEnabled: Boolean, notificationTimeValue: String)
    fun hideNotificationTime(needHide: Boolean)
    fun setNewTimeInterval(timePresentationString: String)
    fun onStreaksLoaded(currentStreak: Int, maxStreak: Int, haveSolvedToday: Boolean)
}