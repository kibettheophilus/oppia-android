package org.oppia.android.domain.oppialogger.analytics

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import org.oppia.android.domain.exploration.ExplorationSessionTimerController
import org.oppia.android.domain.oppialogger.ApplicationStartupListener
import java.util.concurrent.TimeUnit

/** Application-level module that provides application-bound domain utilities. */
@Module
class ApplicationLifecycleModule {
  @Provides
  @IntoSet
  fun bindLifecycleObserver(
    applicationLifecycleObserver: ApplicationLifecycleObserver
  ): ApplicationStartupListener = applicationLifecycleObserver

  @Provides
  @LearnerAnalyticsInactivityLimitMillis
  fun provideLearnerAnalyticsInactivityLimitMillis(): Long = TimeUnit.MINUTES.toMillis(30)

  @Provides
  @IntoSet
  fun provideApplicationLifecycleListeners(
    sessionTimerController: ExplorationSessionTimerController
  ): ApplicationLifecycleListener = sessionTimerController
}
