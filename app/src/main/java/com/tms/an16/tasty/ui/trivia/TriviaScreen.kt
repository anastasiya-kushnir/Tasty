package com.tms.an16.tasty.ui.trivia

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tms.an16.tasty.R
import com.tms.an16.tasty.network.NetworkResult
import com.tms.an16.tasty.ui.components.ErrorState
import com.tms.an16.tasty.ui.components.LoadingState

@Composable
fun TriviaScreen(
    viewModel: TriviaViewModel,
    onTriviaLoaded: (String) -> Unit = {},
) {
    val triviaResponse by viewModel.triviaResponse.collectAsStateWithLifecycle()
    val offlineTrivia by viewModel.readTrivia.collectAsStateWithLifecycle(initialValue = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_trivia),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )

        when (val response = triviaResponse) {
            is NetworkResult.Idle,
            is NetworkResult.Loading,
                -> {
                LoadingState()
            }

            is NetworkResult.Success -> {
                val text = response.data?.text ?: ""
                onTriviaLoaded(text)
                SuccessState(text)
            }

            is NetworkResult.Error -> {
                if (offlineTrivia.isNotEmpty()) {
                    val text = offlineTrivia.first().trivia.text
                    onTriviaLoaded(text)
                    SuccessState(text)
                } else {
                    ErrorState()
                }
            }
        }
    }
}

@Composable
private fun SuccessState(triviaText: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_trivia_bulb),
            contentDescription = null,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = stringResource(id = R.string.interesting_fact),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline,
            ),
        ) {
            Text(
                text = triviaText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Start,
            )
        }
    }
}
