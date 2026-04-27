package com.tms.an16.tasty.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.network.NetworkResult
import com.tms.an16.tasty.ui.components.ErrorState
import com.tms.an16.tasty.util.toRecipeEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel,
    onRecipeClick: (RecipeEntity) -> Unit,
    onFabClick: () -> Unit,
) {
    val recipesResponse by viewModel.recipesResponse.collectAsStateWithLifecycle()
    val searchedRecipesResponse by viewModel.searchedRecipesResponse.collectAsStateWithLifecycle()
    val localRecipes by viewModel.readRecipes.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_restaurant),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
        ) {
            // Priority: Search Results -> API Results -> Local Database
            val displayList = when {
                searchedRecipesResponse is NetworkResult.Success && searchedRecipesResponse.data?.recipes?.isNotEmpty() == true -> {
                    searchedRecipesResponse.data!!.recipes.map { it.toRecipeEntity() }
                }

                recipesResponse is NetworkResult.Success -> {
                    recipesResponse.data?.recipes?.map { it.toRecipeEntity() } ?: localRecipes
                }

                else -> localRecipes
            }

            val isLoading =
                recipesResponse is NetworkResult.Loading || searchedRecipesResponse is NetworkResult.Loading
            val isError =
                recipesResponse is NetworkResult.Error || searchedRecipesResponse is NetworkResult.Error
            val isIdle =
                recipesResponse is NetworkResult.Idle && searchedRecipesResponse is NetworkResult.Idle

            if ((isLoading || isIdle) && displayList.isEmpty()) {
                LazyColumn {
                    items(10) {
                        ShimmerRecipeItem()
                    }
                }
            } else if (isError && displayList.isEmpty()) {
                ErrorState()
            } else {
                LazyColumn {
                    items(displayList) { recipe ->
                        RecipeCard(recipe = recipe, onRecipeClick = onRecipeClick)
                    }
                }
            }
        }
    }
}
