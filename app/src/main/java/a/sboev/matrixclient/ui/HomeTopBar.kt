package a.sboev.matrixclient.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.clientserverapi.client.SyncState
import net.folivo.trixnity.clientserverapi.model.rooms.DirectoryVisibility
import net.folivo.trixnity.core.model.UserId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(client: MatrixClient, syncState: SyncState) {
    val scope = rememberCoroutineScope()
    CenterAlignedTopAppBar(
        title = {
            val stateName = when (syncState) {
                SyncState.INITIAL_SYNC -> "Connecting..."
                SyncState.STARTED, SyncState.RUNNING, SyncState.TIMEOUT -> "Smalk"
                SyncState.ERROR -> "No connection..."
                SyncState.STOPPING -> "Stopping"
                SyncState.STOPPED -> "Stopped"
            }
            Text(
                stateName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            var showLogoutDialog by remember { mutableStateOf(false) }
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    text = { Text("Do you want to logout?") },
                    confirmButton = {
                        Button(onClick = {
                            showLogoutDialog = false

                        }) {
                            Text("Logout")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showLogoutDialog = false
                        }) {
                            Text("Cancel")
                        }
                    },
                )
            }
            IconButton(onClick = { showLogoutDialog = true }) {
                ProfileAvatar(client = client)
            }
        },
        actions = {
            IconButton(
                onClick = {
                    scope.launch {
                        client.api.rooms.createRoom(
                            visibility = DirectoryVisibility.PRIVATE,
                            name = "test2",
                            topic = "toTestRoom",
                            invite = setOf(UserId("@sboev2:matrix-server-dev"))
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                )
            }

        }
    )
}

@Composable
private fun ProfileAvatar(
    modifier: Modifier = Modifier,
    client: MatrixClient,
    textSize: TextUnit = 18.sp
) {
    val urlAndName by combine(client.avatarUrl, client.displayName) { u, n -> u to n }
        .collectAsState(null to null)
    val (url, name) = urlAndName
    Avatar(
        modifier = modifier,
        client = client,
        id = client.userId.full,
        url = url,
        name = name.orEmpty(),
        textSize = textSize
    )
}