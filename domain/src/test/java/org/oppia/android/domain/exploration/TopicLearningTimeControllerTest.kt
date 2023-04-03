package org.oppia.android.domain.exploration

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oppia.android.app.model.ProfileId
import org.oppia.android.domain.oppialogger.analytics.ApplicationLifecycleModule
import org.oppia.android.domain.topic.TEST_TOPIC_ID_0
import org.oppia.android.testing.FakeExceptionLogger
import org.oppia.android.testing.TestLogReportingModule
import org.oppia.android.testing.data.DataProviderTestMonitor
import org.oppia.android.testing.robolectric.RobolectricModule
import org.oppia.android.testing.threading.TestCoroutineDispatchers
import org.oppia.android.testing.threading.TestDispatcherModule
import org.oppia.android.testing.time.FakeOppiaClock
import org.oppia.android.testing.time.FakeOppiaClockModule
import org.oppia.android.util.data.DataProvidersInjector
import org.oppia.android.util.data.DataProvidersInjectorProvider
import org.oppia.android.util.locale.testing.LocaleTestModule
import org.oppia.android.util.logging.EnableConsoleLog
import org.oppia.android.util.logging.EnableFileLog
import org.oppia.android.util.logging.GlobalLogLevel
import org.oppia.android.util.logging.LogLevel
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import javax.inject.Inject
import javax.inject.Singleton

/** Tests for [TopicLearningTimeController]. */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TopicLearningTimeControllerTest.TestApplication::class)
class TopicLearningTimeControllerTest {
  @Inject lateinit var fakeExceptionLogger: FakeExceptionLogger
  @Inject lateinit var testCoroutineDispatchers: TestCoroutineDispatchers
  @Inject lateinit var monitorFactory: DataProviderTestMonitor.Factory
  @Inject lateinit var oppiaClock: FakeOppiaClock
  @Inject lateinit var topicLearningTimeController: TopicLearningTimeController

  private val profileId = ProfileId.newBuilder().setInternalId(0).build()
  private val sessionDuration = 5000L

  @Before
  fun setUp() {
    setUpTestApplicationComponent()
  }

  @Test
  fun testExplorationSessionStarted_setsStartExplorationTimestampToCurrentTime() {
    oppiaClock.setFakeTimeMode(FakeOppiaClock.FakeTimeMode.MODE_FIXED_FAKE_TIME)
    topicLearningTimeController.setExplorationSessionStarted()
    val currentTime = oppiaClock.getCurrentTimeMs()
    assertThat(topicLearningTimeController.startExplorationTimestampMs).isEqualTo(currentTime)
  }

  @Test
  fun testRecordAggregateTopicLearningTime_returnsSuccess() {
    val recordAggregateTimeProvider = topicLearningTimeController.recordAggregateTopicLearningTime(
      profileId, TEST_TOPIC_ID_0, sessionDuration
    )
    monitorFactory.waitForNextSuccessfulResult(recordAggregateTimeProvider)
  }

  private fun setUpTestApplicationComponent() {
    ApplicationProvider.getApplicationContext<TestApplication>()
      .inject(this)
  }

  @Module
  class TestModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
      return application
    }

    @EnableConsoleLog
    @Provides
    fun provideEnableConsoleLog(): Boolean = true

    @EnableFileLog
    @Provides
    fun provideEnableFileLog(): Boolean = false

    @GlobalLogLevel
    @Provides
    fun provideGlobalLogLevel(): LogLevel = LogLevel.VERBOSE
  }

  @Singleton
  @Component(
    modules = [
      TestModule::class, RobolectricModule::class, TestLogReportingModule::class,
      FakeOppiaClockModule::class, ApplicationLifecycleModule::class, TestDispatcherModule::class,
      LocaleTestModule::class, ExplorationProgressModule::class
    ]
  )

  interface TestApplicationComponent : DataProvidersInjector {
    @Component.Builder
    interface Builder {
      @BindsInstance
      fun setApplication(application: Application): Builder

      fun build(): TestApplicationComponent
    }

    fun inject(topicLearningTimeControllerTest: TopicLearningTimeControllerTest)
  }

  class TestApplication : Application(), DataProvidersInjectorProvider {
    private val component: TestApplicationComponent by lazy {
      DaggerTopicLearningTimeControllerTest_TestApplicationComponent.builder()
        .setApplication(this)
        .build()
    }

    fun inject(topicLearningTimeControllerTest: TopicLearningTimeControllerTest) {
      component.inject(topicLearningTimeControllerTest)
    }

    override fun getDataProvidersInjector(): DataProvidersInjector = component
  }
}