package a.sboev.matrixclient

import a.sboev.matrixclient.viewmodels.ChatsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel

class ChatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ChatsScreen()
            }
        }
    }
}


@Composable
fun ChatsScreen() {
    val chatsViewModel: ChatsViewModel = viewModel(factory = ChatsViewModel.Factory)
    val chats = chatsViewModel.chats.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            chats.value
        ) {
            _, item -> item.render(client = chatsViewModel.client)
        }
    }
}



