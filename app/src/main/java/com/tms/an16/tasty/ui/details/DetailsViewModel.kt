package com.tms.an16.tasty.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.controller.SelectedRecipeController
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val recipe: RecipeEntity? = SelectedRecipeController.selectedRecipeEntity

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        checkIsFavorite()
    }

    private fun checkIsFavorite() {
        recipe?.let { selectedRecipe ->
            viewModelScope.launch {
                repository.local.readFavoriteRecipes().collectLatest { favorites ->
                    _isFavorite.value = favorites.any { it.recipeEntity.recipeId == selectedRecipe.recipeId }
                }
            }
        }
    }

    fun toggleFavorite() {
        recipe?.let { selectedRecipe ->
            viewModelScope.launch(Dispatchers.IO) {
                if (_isFavorite.value) {
                    repository.local.deleteFavoriteRecipe(FavoritesEntity(selectedRecipe))
                } else {
                    repository.local.insertFavoriteRecipe(FavoritesEntity(selectedRecipe, System.currentTimeMillis()))
                }
            }
        }
    }
}
