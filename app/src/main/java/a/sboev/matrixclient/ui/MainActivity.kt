package a.sboev.matrixclient.ui

import a.sboev.matrixclient.MatrixApp
import a.sboev.matrixclient.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.folivo.trixnity.clientserverapi.client.SyncState


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {

            val sync = MatrixApp.INSTANCE.clientSyncState
            sync.collect{
                Log.d(TAG, "sync state $it")
                when (it) {
                    SyncState.RUNNING -> {
                        supportFragmentManager.beginTransaction().replace(R.id.content, ChatsFragment()).commit()
                    }
                    else -> {}
                }
            }
        }



    }
}