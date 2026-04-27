package com.tms.an16.tasty.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.ui.theme.Green
import com.tms.an16.tasty.ui.theme.Yellow
import com.tms.an16.tasty.util.parseHtml

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OverviewTab(
    recipe: RecipeEntity,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            AsyncImage(
                model = recipe.image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = if (isFavorite) Yellow else Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = recipe.aggregateLikes.toString(),
                    modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_time),
                    contentDescription = null,
                    tint = Yellow,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${recipe.readyInMinutes} min",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(label = stringResource(id = R.string.vegetarian), active = recipe.vegetarian)
                StatusChip(label = stringResource(id = R.string.vegan), active = recipe.vegan)
                StatusChip(label = stringResource(id = R.string.gluten_free), active = recipe.glutenFree)
                StatusChip(label = stringResource(id = R.string.dairy_free), active = recipe.dairyFree)
                StatusChip(label = stringResource(id = R.string.healthy), active = recipe.veryHealthy)
                StatusChip(label = stringResource(id = R.string.cheap), active = recipe.cheap)
            }

            Text(
                text = parseHtml(recipe.summary),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp),
                lineHeight = 24.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusChip(label: String, active: Boolean) {
    Surface(
        color = if (active) Green.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        border = if (active) androidx.compose.foundation.BorderStroke(1.dp, Green) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = if (active) Green else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp),
                color = if (active) Green else Color.Gray
            )
        }
    }
}
