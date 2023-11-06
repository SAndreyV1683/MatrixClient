package io.terrakok.smalk.service

import a.sboev.matrixclient.AndroidApp
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.aakira.napier.DebugAntilog

import kotlinx.datetime.Instant
import net.folivo.trixnity.client.media.MediaStore
import net.folivo.trixnity.client.media.okio.OkioMediaStore
import net.folivo.trixnity.client.store.repository.realm.createRealmRepositoriesModule
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import java.text.SimpleDateFormat

fun getPlatformSettings(): Settings = SharedPreferencesSettings(
    AndroidApp.INSTANCE.getSharedPreferences("SmalkPreferences", Context.MODE_PRIVATE)
)

fun ByteArray.asImageBitmap(): ImageBitmap =
    BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()

fun getLogger(defaultTag: String): DebugAntilog = DebugAntilog(defaultTag)

fun createDateFormat(pattern: String) = object : (Instant) -> String {
    private val formatter = SimpleDateFormat(pattern)
    override fun invoke(instant: Instant) = formatter.format(instant.toEpochMilliseconds())
}

private fun getCacheDirectoryPath(): Path =
    AndroidApp.INSTANCE.cacheDir.absolutePath.toPath().resolve("cache")

suspend fun getPlatformRepositoryModule(): Module = createRealmRepositoriesModule {
    directory(getCacheDirectoryPath().resolve("realm").toString())
}

suspend fun getPlatformMediaStore(): MediaStore = OkioMediaStore(
    getCacheDirectoryPath().resolve("media")
)