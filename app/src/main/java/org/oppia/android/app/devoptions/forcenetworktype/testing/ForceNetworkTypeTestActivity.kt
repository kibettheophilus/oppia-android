package org.oppia.android.app.devoptions.forcenetworktype.testing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.oppia.android.R
import org.oppia.android.app.activity.InjectableAppCompatActivity
import org.oppia.android.app.devoptions.forcenetworktype.ForceNetworkTypeFragment

/** Activity for testing [ForceNetworkTypeFragment]. */
class ForceNetworkTypeTestActivity : InjectableAppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activityComponent as Injector).inject(this)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
    setContentView(R.layout.force_network_type_activity)
    if (getForceNetworkTypeFragment() == null) {
      val forceNetworkTypeFragment = ForceNetworkTypeFragment.newInstance()
      supportFragmentManager.beginTransaction().add(
        R.id.force_network_type_container,
        forceNetworkTypeFragment
      ).commitNow()
    }
  }

  private fun getForceNetworkTypeFragment(): ForceNetworkTypeFragment? {
    return supportFragmentManager
      .findFragmentById(R.id.force_network_type_container) as ForceNetworkTypeFragment?
  }

  /** Dagger injector for [ForceNetworkTypeTestActivity]. */
  interface Injector {
    /** Injects dependencies into the [activity]. */
    fun inject(activity: ForceNetworkTypeTestActivity)
  }

  companion object {
    /** Returns an [Intent] for opening new instances of [ForceNetworkTypeTestActivity]. */
    fun createIntent(context: Context): Intent =
      Intent(context, ForceNetworkTypeTestActivity::class.java)
  }
}
