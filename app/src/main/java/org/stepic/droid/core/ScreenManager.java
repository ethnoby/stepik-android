package org.stepic.droid.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepic.droid.model.CertificateViewItem;
import org.stepic.droid.model.CollectionDescriptionColors;
import org.stepic.droid.social.SocialMedia;
import org.stepik.android.domain.auth.model.SocialAuthType;
import org.stepik.android.domain.course.analytic.CourseViewSource;
import org.stepik.android.domain.course_list.model.CourseListQuery;
import org.stepik.android.domain.feedback.model.SupportEmailData;
import org.stepik.android.domain.last_step.model.LastStep;
import org.stepik.android.model.Course;
import org.stepik.android.model.CourseCollection;
import org.stepik.android.model.Lesson;
import org.stepik.android.model.Section;
import org.stepik.android.model.Step;
import org.stepik.android.model.Tag;
import org.stepik.android.model.Unit;
import org.stepik.android.model.comments.DiscussionThread;
import org.stepik.android.model.user.Profile;
import org.stepik.android.view.auth.model.AutoAuth;
import org.stepik.android.view.course.routing.CourseScreenTab;
import org.stepik.android.view.routing.deeplink.BranchRoute;
import org.stepik.android.view.video_player.model.VideoPlayerMediaData;

public interface ScreenManager {

    void showLaunchFromSplash(Activity activity);

    void showLaunchScreen(Context context);

    void showLaunchScreenAfterLogout(Context context);

    void showLaunchScreen(Context context, boolean fromMainFeed, int indexInMenu);

    void showRegistration(Activity sourceActivity, @Nullable Course course);

    void showLogin(Activity sourceActivity, @Nullable String email, @Nullable String password, AutoAuth autoAuth, @Nullable Course course);

    void showMainFeedAfterLogin(Activity sourceActivity, @Nullable Course course);

    void showMainFeedFromSplash(Activity sourceActivity);

    void showMainFeed(Context sourceActivity, int indexOfMenu);

    void showPdfInBrowserByGoogleDocs(Activity activity, String fullPath);

    void openComments(Activity context, @NonNull DiscussionThread discussionThread, @NonNull Step step, @Nullable Long discussionId, boolean needOpenForm);

    void showSteps(Activity sourceActivity, @NotNull Unit unit, @NotNull Lesson lesson, @NotNull Section section);

    void showSteps(Activity sourceActivity, @NotNull Unit unit, @NotNull Lesson lesson, @NotNull Section section, boolean backAnimation, boolean isAutoplayEnabled);

    void showTrialLesson(Activity sourceActivity, Long lessonId);

    void openStepInWeb(Context context, Step step);

    void openRemindPassword(AppCompatActivity context);

    void showCourseDescription(Context context, long courseId, @NotNull CourseViewSource viewSource);
    void showCourseDescription(Context context, @NotNull Course course, @NotNull CourseViewSource viewSource);
    void showCourseDescription(Context context, @NotNull Course course, @NotNull CourseViewSource viewSource, boolean autoEnroll);
    void showCourseModules(Context context, @NotNull Course course, @NotNull CourseViewSource viewSource);
    void showCourseScreen(Context context, @NotNull Course course, @NotNull CourseViewSource viewSource, boolean autoEnroll, CourseScreenTab tab);

    void showStoreWithApp(Activity sourceActivity);

    void showDownloads(Context context);

    void showCatalog(Context context);

    Intent getCatalogIntent(Context context);

    void showVideo(@NotNull Fragment sourceFragment, @NotNull VideoPlayerMediaData videoPlayerMediaData, @Nullable Intent lessonMoveNextIntent);

    void showSettings(Activity sourceActivity);

    void showNotificationSettings(Activity sourceActivity);

    void showStorageManagement(Activity activity);

    void openInWeb(Activity context, String path);

    void addCertificateToLinkedIn(CertificateViewItem certificateViewItem);

    void showCertificates(Context context);

    void showCertificates(Context context, long userId);

    Intent getCertificateIntent();

    Intent getOpenInWebIntent(String path);

    /**
     * Redirects to external web browser in case if app intercepts wrong deeplink (if uri contains `from_mobile_app=true` query param)
     * @param context - activity context
     * @param uri - Intent::data
     */
    void redirectToWebBrowserIfNeeded(@NotNull Context context, @NotNull Uri uri);

    void openLinkInWebBrowser(@NotNull Context context, @NotNull Uri uri);

    void openProfile(@NonNull Context context, long userId);

    void openFeedbackActivity(Activity activity);

    Intent getMyCoursesIntent(@NotNull Context context);

    Intent getProfileIntent(@NotNull Context context);

    Intent getMyProfileIntent(@NotNull Context context);

    void openSplash(Context context);

    void openAboutActivity(Activity activity);

    void openPrivacyPolicyWeb(Activity activity);

    void openTermsOfServiceWeb(Activity activity);

    void continueAdaptiveCourse(Activity activity, Course course);

    void continueCourse(Activity activity, long courseId, @NotNull CourseViewSource viewSource, @NotNull LastStep lastStep);
    void continueCourse(Activity activity, @NotNull LastStep lastStep);

    void showLaunchScreen(FragmentActivity activity, @NotNull Course course);

    void openImage(Context context, String path);

    void showAdaptiveStats(Context context, long courseId);

    void showOnboarding(@NotNull Activity activity);

    void showAchievementsList(Context context, long userId, boolean isMyProfile);

    void openDeepLink(Context context, BranchRoute route);

    void showProfileEdit(Context context);
    void showProfileEditInfo(Activity activity, Profile profile);
    void showProfileEditPassword(Activity activity, long profileId);

    void openTextFeedBack(Context context, SupportEmailData supportEmailData);

    void openSocialMediaLink(Context context, String link);
    void openSocialMediaLink(Context context, SocialMedia socialLink);

    void loginWithSocial(FragmentActivity activity, SocialAuthType type);

    void showCachedAttempts(@NotNull Context context, long courseId);

    void showCoursesByQuery(Context context, String courseListTitle, CourseListQuery courseListQuery);
    void showCoursesCollection(Context context, CourseCollection courseCollection, CollectionDescriptionColors collectionDescriptionColors);
    void showUserCourses(Context context);
    void showCoursesByTag(Context context, Tag tag);
}
