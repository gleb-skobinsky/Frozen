package com.example.frozen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.frozen.ui.theme.FrozenTheme

class MainActivity : ComponentActivity() {
    private val frozenId = uuid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrozenTheme {
                Text(
                    text = SAMPLE_TEXT,
                    modifier = Modifier
                        .frozen(frozenId)
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
