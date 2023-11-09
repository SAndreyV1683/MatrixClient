package a.sboev.matrixclient

import a.sboev.matrixclient.service.SessionManager
import android.app.Application
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import io.github.aakira.napier.BuildConfig
import io.github.aakira.napier.Napier
import io.terrakok.smalk.service.getLogger
import io.terrakok.smalk.service.getPlatformSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.clientserverapi.client.SyncState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module


class MatrixApp : Application(), KoinComponent {

    private var isInit = false
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var client: MatrixClient
    private val sessionManager by inject<SessionManager>()
    companion object {
        lateinit var INSTANCE: MatrixApp
        private val TAG = MatrixApp::class.simpleName
    }
    private val clientSyncStateFlow = MutableStateFlow(SyncState.STOPPED)
    val clientSyncState = clientSyncStateFlow.asStateFlow()
    override fun onCreate() {
        coroutineScope.launch {
            if (!sessionManager.tryRestoreSession())
                sessionManager.login("http://10.77.15.84:8008", "sboev", "sboev")
            client = sessionManager.getClient()
            Log.d(TAG,"Matrix client start sync")
            client.startSync()
            client.syncState.collect {
                Log.d(TAG,"Matrix client sync $it")
                clientSyncStateFlow.value = it
            }
        }
        super.onCreate()
        INSTANCE = this
        initApp()
    }

    private fun initApp() {
        if (isInit) {
            Napier.e("Second initialization!")
            return
        }
        isInit = true

        if (BuildConfig.DEBUG) {
            Napier.base(getLogger("Smalk"))
        }

        val appModule = module {
            single { getPlatformSettings() }
            single { SessionManager(get()) }
            factory { get<SessionManager>().getClient() }
        }

        koinApplication {
            modules(appModule)
        }

        startKoin {
            modules(appModule)
        }



    }
}