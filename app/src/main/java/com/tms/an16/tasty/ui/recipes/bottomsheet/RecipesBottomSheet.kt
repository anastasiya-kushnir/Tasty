package com.tms.an16.tasty.ui.recipes.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tms.an16.tasty.repository.MealAndDietType
import com.tms.an16.tasty.ui.recipes.RecipesViewModel
import com.tms.an16.tasty.ui.theme.TastyTheme
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_MEAL_TYPE

class RecipesBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: RecipesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TastyTheme {
                    val mealAndDiet by viewModel.mealAndDietType.collectAsState(
                        initial = MealAndDietType(DEFAULT_MEAL_TYPE, 0, DEFAULT_DIET_TYPE, 0),
                    )

                    RecipesBottomSheetScreen(
                        initialMealAndDiet = mealAndDiet,
                        onApplyClick = { mealType, mealTypeId, dietType, dietTypeId ->
                            viewModel.saveMealAndDietType(
                                mealType, mealTypeId, dietType, dietTypeId,
                            )
                            val action =
                                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment()
                            action.backFromBottomSheet = true
                            findNavController().navigate(action)
                        },
                    )
                }
            }
        }
    }
}
