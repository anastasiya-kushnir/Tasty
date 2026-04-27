package com.tms.an16.tasty.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.NetworkController
import com.tms.an16.tasty.controller.NetworkState
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.model.FoodRecipes
import com.tms.an16.tasty.network.NetworkResult
import com.tms.an16.tasty.repository.DataStoreRepository
import com.tms.an16.tasty.repository.MealAndDietType
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.tms.an16.tasty.util.Constants.Companion.QUERY_DIET
import com.tms.an16.tasty.util.Constants.Companion.QUERY_TYPE
import com.tms.an16.tasty.util.toRecipeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    networkController: NetworkController,
) : ViewModel() {

    private val _recipesResponse =
        MutableStateFlow<NetworkResult<FoodRecipes>>(NetworkResult.Idle())
    val recipesResponse: StateFlow<NetworkResult<FoodRecipes>> = _recipesResponse.asStateFlow()

    val readRecipes: StateFlow<List<RecipeEntity>> = repository.local.readRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isNetworkConnected = MutableStateFlow(NetworkState.UNKNOWN)
    val isNetworkConnected: StateFlow<NetworkState> = _isNetworkConnected.asStateFlow()

    val mealAndDietType: StateFlow<MealAndDietType> = dataStoreRepository.readMealAndDietType
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MealAndDietType(DEFAULT_MEAL_TYPE, 0, DEFAULT_DIET_TYPE, 0),
        )

    var backOnline = false

    val readBackOnline: Flow<Boolean> = dataStoreRepository.readBackOnline

    private val _searchedRecipesResponse =
        MutableStateFlow<NetworkResult<FoodRecipes>>(NetworkResult.Idle())
    val searchedRecipesResponse: StateFlow<NetworkResult<FoodRecipes>> =
        _searchedRecipesResponse.asStateFlow()

    init {
        viewModelScope.launch {
            networkController.isNetworkConnected.collectLatest {
                _isNetworkConnected.value = it
            }
        }
    }

    fun getRecipes(queries: Map<String, String>) {
        viewModelScope.launch {
            _recipesResponse.value = NetworkResult.Loading()
            if (_isNetworkConnected.value == NetworkState.CONNECTED) {
                try {
                    val response = repository.remote.getRecipes(queries)
                    _recipesResponse.value = handleFoodRecipesResponse(response)

                    val foodRecipe = _recipesResponse.value.data
                    if (foodRecipe != null) {
                        offlineCacheRecipes(foodRecipe)
                    }
                } catch (e: Exception) {
                    _recipesResponse.value =
                        NetworkResult.Error(messageId = R.string.recipes_not_found)

                }
            } else {
                _recipesResponse.value =
                    NetworkResult.Error(messageId = R.string.no_internet_connection)
            }
        }
    }

    fun searchRecipes(searchQuery: Map<String, String>) {
        viewModelScope.launch {
            _searchedRecipesResponse.value = NetworkResult.Loading()
            if (_isNetworkConnected.value == NetworkState.CONNECTED) {
                try {
                    val response = repository.remote.searchRecipes(searchQuery)
                    _searchedRecipesResponse.value = handleFoodRecipesResponse(response)
                } catch (e: Exception) {
                    _searchedRecipesResponse.value =
                        NetworkResult.Error(messageId = R.string.recipes_not_found)
                }
            } else {
                _searchedRecipesResponse.value =
                    NetworkResult.Error(messageId = R.string.no_internet_connection)
            }
        }
    }

    fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int,
    ) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.saveMealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId,
        )
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = dataStoreRepository.applyQueries()
        val currentMealAndDiet = mealAndDietType.value

        queries[QUERY_TYPE] = currentMealAndDiet.selectedMealType
        queries[QUERY_DIET] = currentMealAndDiet.selectedDietType

        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        return dataStoreRepository.applySearchQuery(searchQuery)
    }

    fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    private fun offlineCacheRecipes(foodRecipes: FoodRecipes) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllRecipes()
            repository.local.insertRecipes(foodRecipes.recipes.map { it.toRecipeEntity() })
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipes>): NetworkResult<FoodRecipes> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(messageId = R.string.timeout)
            }

            response.code() == 402 -> {
                NetworkResult.Error(messageId = R.string.api_key_limited)
            }

            response.body()?.recipes.isNullOrEmpty() -> {
                NetworkResult.Error(messageId = R.string.recipes_not_found)
            }

            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }

            else -> {
                NetworkResult.Error(message = response.message())
            }
        }
    }
}
