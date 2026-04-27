package com.tms.an16.tasty.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.SelectedRecipeController
import com.tms.an16.tasty.ui.theme.TastyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    private val viewModel: FavoriteRecipesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TastyTheme {
                    FavoriteRecipesScreen(
                        viewModel = viewModel,
                        onRecipeClick = { recipe ->
                            SelectedRecipeController.selectedRecipeEntity = recipe
                            findNavController().navigate(
                                FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsFragment(),
                            )
                        },
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.fav_recipes_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.delete_all) {
                        viewModel.deleteAllFavoriteRecipes()

                        Snackbar.make(
                            requireView(),
                            getString(R.string.all_recipes_removed),
                            Snackbar.LENGTH_SHORT,
                        ).setAction(getString(R.string.OK)) {}
                            .show()
                    }
                    return true
                }
            },
            viewLifecycleOwner, Lifecycle.State.RESUMED,
        )
    }
}
