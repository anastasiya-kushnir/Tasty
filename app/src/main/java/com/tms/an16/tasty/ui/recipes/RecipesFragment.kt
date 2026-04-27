package com.tms.an16.tasty.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.NetworkState
import com.tms.an16.tasty.controller.SelectedRecipeController
import com.tms.an16.tasty.ui.theme.TastyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel: RecipesViewModel by activityViewModels()

    private val args by navArgs<RecipesFragmentArgs>()

    private var dataRequested = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TastyTheme {
                    RecipesScreen(
                        viewModel = viewModel,
                        onRecipeClick = { recipe ->
                            SelectedRecipeController.selectedRecipeEntity = recipe
                            findNavController().navigate(
                                RecipesFragmentDirections.actionRecipesFragmentToDetailsFragment(),
                            )
                        },
                        onFabClick = {
                            if (viewModel.isNetworkConnected.value == NetworkState.CONNECTED) {
                                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
                            } else {
                                Toast.makeText(
                                    context,
                                    getString(R.string.no_internet_connection),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        },
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIsBackOnline()

        monitorNetworkStatus()

        val menu: MenuHost = requireActivity()
        menu.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.recipes_menu, menu)

                    val search = menu.findItem(R.id.menu_search)
                    val searchView = search.actionView as? SearchView
                    searchView?.isSubmitButtonEnabled = true
                    searchView?.setOnQueryTextListener(this@RecipesFragment)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return true
                }
            },
            viewLifecycleOwner, Lifecycle.State.RESUMED,
        )

        loadInitialData()
    }

    private fun loadInitialData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val database = viewModel.readRecipes.value
                if (database.isEmpty() && !dataRequested && viewModel.isNetworkConnected.value == NetworkState.CONNECTED) {
                    viewModel.getRecipes(viewModel.applyQueries())
                    dataRequested = true
                } else if (args.backFromBottomSheet) {
                    viewModel.getRecipes(viewModel.applyQueries())
                }
            }
        }
    }

    private fun monitorNetworkStatus() {
        lifecycleScope.launch {
            viewModel.isNetworkConnected.collectLatest { networkStatus ->
                when (networkStatus) {
                    NetworkState.CONNECTED -> {
                        if (viewModel.backOnline) {
                            Toast.makeText(
                                context,
                                getString(R.string.we_are_back_online),
                                Toast.LENGTH_SHORT,
                            ).show()
                            viewModel.saveBackOnline(false)
                        }
                    }

                    NetworkState.DISCONNECTED -> {
                        Toast.makeText(
                            context,
                            getString(R.string.no_internet_connection),
                            Toast.LENGTH_SHORT,
                        ).show()
                        viewModel.saveBackOnline(true)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun checkIsBackOnline() {
        lifecycleScope.launch {
            viewModel.readBackOnline.collectLatest {
                viewModel.backOnline = it
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            viewModel.searchRecipes(viewModel.applySearchQuery(query))
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}
