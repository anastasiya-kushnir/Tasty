package com.tms.an16.tasty.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tms.an16.tasty.R

@Composable
fun ErrorState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sad),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .alpha(0.5f),
        )
        Text(
            text = stringResource(id = R.string.no_internet_connection),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(0.5f),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    }
}
