package com.tms.an16.tasty.ui.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.ui.recipes.RecipeCard

@Composable
fun FavoriteRecipesScreen(
    viewModel: FavoriteRecipesViewModel,
    onRecipeClick: (RecipeEntity) -> Unit
) {
    val favoriteRecipes by viewModel.readFavoriteRecipes.collectAsStateWithLifecycle()
    var recipeToDelete by remember { mutableStateOf<FavoritesEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        if (favoriteRecipes.isEmpty()) {
            NoFavoritesState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(favoriteRecipes, key = { it.recipeEntity.recipeId }) { favorite ->
                    RecipeCard(
                        recipe = favorite.recipeEntity,
                        onRecipeClick = onRecipeClick,
                        onRecipeLongClick = { recipeToDelete = favorite }
                    )
                }
            }
        }

        recipeToDelete?.let { favorite ->
            DeleteFavoriteDialog(
                onDismiss = { recipeToDelete = null },
                onConfirm = {
                    viewModel.deleteFavoriteRecipe(favorite)
                    recipeToDelete = null
                }
            )
        }
    }
}

@Composable
fun NoFavoritesState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_menu_book),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .alpha(0.5f)
        )
        Text(
            text = stringResource(id = R.string.no_favorite_recipes),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(0.5f),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun DeleteFavoriteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.delete_from_favorites)) },
        text = { Text(text = stringResource(id = R.string.this_action_cannot_be_undone)) },
        icon = { Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = null) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.no))
            }
        }
    )
}
