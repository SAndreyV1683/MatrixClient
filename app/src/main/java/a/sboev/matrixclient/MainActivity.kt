package a.sboev.matrixclient

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.terrakok.smalk.service.getPlatformSettings
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.folivo.trixnity.api.client.MatrixApiClient
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.client.room


class MainActivity : AppCompatActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            /*val sessionManager = SessionManager(getPlatformSettings())
            sessionManager.login("http://10.77.15.84:8008", "sboev", "sboev")
            val client = sessionManager.getClient()
            client.startSync()
            client.api.sync.syncProcessing
            */
        }

        setContentView(R.layout.activity_main)
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction().replace(R.id.content, ChatsFragment()).commit()
        }, 5000)

    }
}