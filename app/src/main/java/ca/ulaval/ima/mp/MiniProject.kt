package ca.ulaval.ima.mp

import android.app.Application
import android.content.Context
import android.widget.Toast


/**
 * static class allowing to access to the application context and toasting with  the application context
 */
class MiniProject : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {

        /**
         * context of the Application
         */
        var appContext: Context? = null
            private set
    }

}