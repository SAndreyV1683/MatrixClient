package a.sboev.matrixclient.ui

import a.sboev.matrixclient.ui.MainActivity.Companion.TAG
import a.sboev.matrixclient.viewmodels.ChatsViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import net.folivo.trixnity.core.model.RoomId

class ChatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                //HomeScreen()

                var selectedRoomId: RoomId? by remember { mutableStateOf(null) }
                var roomNavigator: Navigator? by remember { mutableStateOf(null) }
                val homeView = remember {
                    movableContentOf {
                        ChatsScreen(
                            selectedRoomId = selectedRoomId,
                            onRoomClick = { roomId ->
                                if (selectedRoomId != roomId) {
                                    roomNavigator?.popUntilRoot()
                                    roomNavigator?.push(RoomScreen(roomId))
                                }
                            }
                        )
                    }
                }
                val roomContainer = remember {
                    movableContentOf {
                        Navigator(EmptyRoomScreen) {
                            roomNavigator = it
                            selectedRoomId = (it.lastItem as? RoomScreen)?.id?.let { RoomId(it) }
                            CurrentScreen()
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    homeView()
                    roomContainer()
                }
                /*val phoneMode by LocalPhoneMode.current
                if (phoneMode) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        homeView()
                        roomContainer()
                    }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(350.dp)
                        ) {
                            homeView()
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(DividerDefaults.Thickness)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            roomContainer()
                        }
                    }
                }*/
            }
        }
    }



    @Composable
    fun ChatsScreen(
        selectedRoomId: RoomId?,
        onRoomClick: (RoomId) -> Unit
    ) {
        val chatsViewModel: ChatsViewModel = viewModel(factory = ChatsViewModel.Factory)
        val chats = chatsViewModel.chats.collectAsState()
        val syncState by chatsViewModel.client.syncState.collectAsState()
        Log.d(TAG, "chats list" + chats.value.toString())
        Scaffold(
            topBar = {
                Surface(shadowElevation = 3.dp) {
                    HomeTopBar(chatsViewModel.client, syncState)
                }
            },
            content = { innerPadding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding
                ) {
                    itemsIndexed(
                        chats.value
                    ) { _, item ->

                        val isSelected = selectedRoomId == item.id
                        item.render(
                            modifier = Modifier
                                .clickable { onRoomClick(item.id) }
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                ),
                            client = chatsViewModel.client
                        )
                    }
                }
            },
            bottomBar = {}
        )
    }

}



