package com.tms.an16.tasty.ui.recipes.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tms.an16.tasty.R
import com.tms.an16.tasty.repository.MealAndDietType
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesBottomSheetScreen(
    initialMealAndDiet: MealAndDietType,
    onApplyClick: (String, Int, String, Int) -> Unit
) {
    var selectedMealType by remember(initialMealAndDiet) { mutableStateOf(initialMealAndDiet.selectedMealType) }
    var selectedMealTypeId by remember(initialMealAndDiet) { mutableStateOf(initialMealAndDiet.selectedMealTypeId) }
    var selectedDietType by remember(initialMealAndDiet) { mutableStateOf(initialMealAndDiet.selectedDietType) }
    var selectedDietTypeId by remember(initialMealAndDiet) { mutableStateOf(initialMealAndDiet.selectedDietTypeId) }

    val mealTypes = listOf(
        R.string.main_course, R.string.side_dish, R.string.dessert, R.string.appetizer,
        R.string.salad, R.string.bread, R.string.breakfast, R.string.soup,
        R.string.beverage, R.string.sauce, R.string.marinade, R.string.finger_food,
        R.string.snack, R.string.drink
    )

    val dietTypes = listOf(
        R.string.gluten_free, R.string.ketogenic, R.string.vegetarian, R.string.vegan,
        R.string.pescetarian, R.string.paleo, R.string.primal, R.string.whole_30
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.meal_type),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            mealTypes.forEach { mealResId ->
                val mealLabel = stringResource(id = mealResId)
                val mealValue = mealLabel.lowercase(Locale.ROOT)
                FilterChip(
                    selected = selectedMealType == mealValue,
                    onClick = {
                        selectedMealType = mealValue
                        selectedMealTypeId = mealResId
                    },
                    label = { Text(text = mealLabel) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Text(
            text = stringResource(id = R.string.diet_type),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 12.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dietTypes.forEach { dietResId ->
                val dietLabel = stringResource(id = dietResId)
                val dietValue = dietLabel.lowercase(Locale.ROOT)
                FilterChip(
                    selected = selectedDietType == dietValue,
                    onClick = {
                        selectedDietType = dietValue
                        selectedDietTypeId = dietResId
                    },
                    label = { Text(text = dietLabel) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Button(
            onClick = {
                onApplyClick(selectedMealType, selectedMealTypeId, selectedDietType, selectedDietTypeId)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(id = R.string.apply), color = Color.White)
        }
    }
}
