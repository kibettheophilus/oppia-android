package org.oppia.android.data.backends.gae.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.oppia.android.data.backends.gae.NetworkApiKey
import javax.inject.Inject

/**
 * Data class for the feedback report sent by the Android app to remote storage.
 * TODO(#2801): Link backend domain model
 */
@JsonClass(generateAdapter = true)
data class GaeFeedbackReport @Inject constructor(

  @NetworkApiKey @Json(name = "api_key") val api_key: String,
  @Json(name = "report_creation_timestamp") val report_creation_timestamp_ms: Double?,
  @Json(name = "user_feedback") val user_feedback: GaeUserSuppliedFeedback?,
  @Json(name = "system_context") val system_context: GaeFeedbackReportingSystemContext?,
  @Json(name = "device_context") val device_context: GaeFeedbackReportingDeviceContext?,
  @Json(name = "app_context") val app_context: GaeFeedbackReportingAppContext?

)