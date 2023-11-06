package a.sboev.matrixclient

import android.app.Application
import io.github.aakira.napier.BuildConfig
import io.github.aakira.napier.Napier
import io.terrakok.smalk.service.getLogger
import io.terrakok.smalk.service.getPlatformSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.folivo.trixnity.client.MatrixClient
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.coroutines.suspendCoroutine

class AndroidApp : Application() {

    private var isInit = false
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var client: MatrixClient

    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        coroutineScope.launch {
            val sessionManager = SessionManager(getPlatformSettings())
            sessionManager.login("http://10.77.15.84:8008", "sboev", "sboev")
            client = sessionManager.getClient()
            client.startSync()
            client.api.sync.syncProcessing
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

        val koinApp = startKoin {
            modules(appModule)
        }


    }
}