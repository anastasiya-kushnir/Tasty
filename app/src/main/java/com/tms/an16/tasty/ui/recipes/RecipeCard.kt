package com.tms.an16.tasty.ui.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.ui.theme.Red
import com.tms.an16.tasty.ui.theme.Yellow
import com.tms.an16.tasty.util.parseHtml

@Composable
fun RecipeCard(
    recipe: RecipeEntity,
    onRecipeClick: (RecipeEntity) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onRecipeClick(recipe) },
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
        ) {
            AsyncImage(
                model = recipe.image,
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .padding(start = 18.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = parseHtml(recipe.summary),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RecipeStatusItem(
                        iconRes = R.drawable.ic_favorite,
                        text = recipe.aggregateLikes.toString(),
                        tint = Red,
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    RecipeStatusItem(
                        iconRes = R.drawable.ic_time,
                        text = recipe.readyInMinutes.toString(),
                        tint = Yellow,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    if (recipe.vegan) {
                        RecipeStatusItem(
                            iconRes = R.drawable.ic_vegan,
                            text = stringResource(id = R.string.vegan),
                            tint = Color.Unspecified,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeStatusItem(
    iconRes: Int,
    text: String,
    tint: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (tint == Color.Unspecified) MaterialTheme.colorScheme.onSurfaceVariant else tint,
        )
    }
}
