package org.oppia.android.app.story

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.oppia.android.app.activity.InjectableAppCompatActivity
import org.oppia.android.app.activity.route.ActivityRouter
import org.oppia.android.app.home.RouteToExplorationListener
import org.oppia.android.app.model.DestinationScreen
import org.oppia.android.app.model.ExplorationActivityParams
import org.oppia.android.app.model.ExplorationCheckpoint
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.model.ResumeLessonActivityParams
import org.oppia.android.app.model.ScreenName.STORY_ACTIVITY
import org.oppia.android.app.topic.RouteToResumeLessonListener
import org.oppia.android.util.logging.CurrentAppScreenNameIntentDecorator.decorateWithScreenName
import javax.inject.Inject

/** Activity for stories. */
class StoryActivity :
  InjectableAppCompatActivity(),
  RouteToExplorationListener,
  RouteToResumeLessonListener {
  @Inject lateinit var storyActivityPresenter: StoryActivityPresenter
  @Inject lateinit var activityRouter: ActivityRouter

  private var internalProfileId: Int = -1
  private lateinit var topicId: String
  private lateinit var storyId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activityComponent as Injector).inject(this)
    internalProfileId = intent.getIntExtra(STORY_ACTIVITY_INTENT_EXTRA_INTERNAL_PROFILE_ID, -1)
    topicId = checkNotNull(intent.getStringExtra(STORY_ACTIVITY_INTENT_EXTRA_TOPIC_ID)) {
      "Expected extra topic ID to be included for StoryActivity."
    }
    storyId = checkNotNull(intent.getStringExtra(STORY_ACTIVITY_INTENT_EXTRA_STORY_ID)) {
      "Expected extra story ID to be included for StoryActivity."
    }
    storyActivityPresenter.handleOnCreate(internalProfileId, topicId, storyId)
  }

  override fun routeToExploration(
    profileId: ProfileId,
    topicId: String,
    storyId: String,
    explorationId: String,
    parentScreen: ExplorationActivityParams.ParentScreen,
    isCheckpointingEnabled: Boolean
  ) {
    activityRouter.routeToScreen(
      DestinationScreen.newBuilder().apply {
        explorationActivityParams = ExplorationActivityParams.newBuilder().apply {
          this.profileId = profileId
          this.topicId = topicId
          this.storyId = storyId
          this.explorationId = explorationId
          this.parentScreen = parentScreen
          this.isCheckpointingEnabled = isCheckpointingEnabled
        }.build()
      }.build()
    )
  }

  override fun routeToResumeLesson(
    profileId: ProfileId,
    topicId: String,
    storyId: String,
    explorationId: String,
    parentScreen: ExplorationActivityParams.ParentScreen,
    explorationCheckpoint: ExplorationCheckpoint
  ) {
    activityRouter.routeToScreen(
      DestinationScreen.newBuilder().apply {
        resumeLessonActivityParams = ResumeLessonActivityParams.newBuilder().apply {
          this.profileId = profileId
          this.topicId = topicId
          this.storyId = storyId
          this.explorationId = explorationId
          this.parentScreen = parentScreen
          this.checkpoint = explorationCheckpoint
        }.build()
      }.build()
    )
  }

  override fun onBackPressed() {
    finish()
  }

  interface Injector {
    fun inject(activity: StoryActivity)
  }

  companion object {
    const val STORY_ACTIVITY_INTENT_EXTRA_INTERNAL_PROFILE_ID = "StoryActivity.internal_profile_id"
    const val STORY_ACTIVITY_INTENT_EXTRA_TOPIC_ID = "StoryActivity.topic_id"
    const val STORY_ACTIVITY_INTENT_EXTRA_STORY_ID = "StoryActivity.story_id"

    /** Returns a new [Intent] to route to [StoryActivity] for a specified story. */
    fun createIntent(
      context: Context,
      internalProfileId: Int,
      topicId: String,
      storyId: String
    ): Intent {
      return Intent(context, StoryActivity::class.java).apply {
        putExtra(STORY_ACTIVITY_INTENT_EXTRA_INTERNAL_PROFILE_ID, internalProfileId)
        putExtra(STORY_ACTIVITY_INTENT_EXTRA_TOPIC_ID, topicId)
        putExtra(STORY_ACTIVITY_INTENT_EXTRA_STORY_ID, storyId)
        decorateWithScreenName(STORY_ACTIVITY)
      }
    }
  }
}
