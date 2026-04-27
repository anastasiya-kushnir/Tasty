package com.tms.an16.tasty.ui.details

import android.content.Intent
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tms.an16.tasty.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit
) {
    val recipe = viewModel.recipe ?: return
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var instructionsWebView by remember { mutableStateOf<WebView?>(null) }

    val tabs = listOf(
        stringResource(id = R.string.overview),
        stringResource(id = R.string.ingredients),
        stringResource(id = R.string.instructions)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${context.getString(R.string.awesome_recipe_check_it_out)} \n ${recipe.title} \n ${recipe.sourceUrl}"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(shareIntent)
                    }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    }
                    IconButton(onClick = {
                        printPdf(context, instructionsWebView, recipe.title)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_pdf), contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> OverviewTab(
                    recipe = recipe,
                    isFavorite = isFavorite,
                    onFavoriteClick = { viewModel.toggleFavorite() }
                )
                1 -> IngredientsTab(ingredients = recipe.extendedIngredients)
                2 -> InstructionsTab(
                    sourceUrl = recipe.sourceUrl,
                    onWebViewCreated = { instructionsWebView = it }
                )
            }
        }
    }
}
