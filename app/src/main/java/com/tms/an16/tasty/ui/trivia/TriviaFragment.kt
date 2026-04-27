package com.tms.an16.tasty.ui.trivia

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.tms.an16.tasty.R
import com.tms.an16.tasty.ui.theme.TastyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TriviaFragment : Fragment() {

    private val viewModel: TriviaViewModel by viewModels()

    private var trivia = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TastyTheme {
                    TriviaScreen(
                        viewModel = viewModel,
                        onTriviaLoaded = { loadedTrivia ->
                            trivia = loadedTrivia
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.trivia_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.share_trivia_menu) {
                    val shareIntent = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(
                            Intent.EXTRA_TEXT,
                            "${getString(R.string.interesting_fact)} \n $trivia"
                        )
                        this.type = "text/plain"
                    }
                    startActivity(shareIntent)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.getTrivia()
    }
}