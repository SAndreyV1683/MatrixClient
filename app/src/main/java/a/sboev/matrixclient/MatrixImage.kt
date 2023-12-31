package a.sboev.matrixclient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import io.github.aakira.napier.Napier
import io.terrakok.smalk.service.asImageBitmap
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.client.media
import net.folivo.trixnity.utils.toByteArray

@Composable
fun MatrixImage(
    modifier: Modifier = Modifier,
    client: MatrixClient,
    mxcUri: String
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(size) {
        if (size != IntSize.Zero) {
            val bytes = client.media
                .getThumbnail(mxcUri, size.width.toLong(), size.height.toLong())
                .getOrNull()
                ?.toByteArray()
            bitmap = try {
                bytes?.asImageBitmap()
            } catch (e: Exception) {
                Napier.e("Error with image bytes: $mxcUri")
                null
            }
        }
    }
    Box(
        modifier = modifier.onGloballyPositioned { size = it.size }
    ) {
        bitmap?.let {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}