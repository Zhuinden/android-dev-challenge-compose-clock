/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    var isStarted by rememberSaveable(null) { mutableStateOf(false) }

    var remainingTimeMillis by rememberSaveable(null) { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()

    var previousUpdateTime = remember { System.currentTimeMillis() }

    Surface(color = MaterialTheme.colors.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    @Composable
                    fun ClockText(text: String) {
                        Box(modifier = Modifier.padding(4.dp)) {
                            Text(text = text, fontSize = 16.sp, modifier = Modifier.padding(all = 4.dp))
                        }
                    }
                    
                    val indexHr0 = remainingTimeMillis / 1000 / 60 / 60 % 60 / 10
                    val indexHr1 = remainingTimeMillis / 1000 / 60 / 60 % 60 % 10
                    val indexMin0 = remainingTimeMillis / 1000 / 60 % 60 / 10
                    val indexMin1 = remainingTimeMillis / 1000 / 60 % 60 % 10
                    val indexSec0 = remainingTimeMillis / 1000 % 60 / 10
                    val indexSec1 = remainingTimeMillis / 1000 % 60 % 10
                    val indexMillisec0 = remainingTimeMillis / 100 % 10
                    val indexMillisec1 = remainingTimeMillis / 10 % 10
                    val indexMillisec2 = remainingTimeMillis % 10
                    
                    ClockText(text = "$indexHr0")
                    ClockText(text = "$indexHr1")
                    ClockText(text = ":")
                    ClockText(text = "$indexMin0")
                    ClockText(text = "$indexMin1")
                    ClockText(text = ":")
                    ClockText(text = "$indexSec0")
                    ClockText(text = "$indexSec1")
                    ClockText(text = ".")
                    ClockText(text = "$indexMillisec0")
                    ClockText(text = "$indexMillisec1")
                    ClockText(text = "$indexMillisec2")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            remainingTimeMillis += 5000L
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("+5 sec")
                    }

                    Button(
                        onClick = {
                            remainingTimeMillis += 30000L
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("+30 sec")
                    }

                    Button(
                        onClick = {
                            remainingTimeMillis += 60000L
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("+60 sec")
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Button(modifier = Modifier.padding(8.dp), onClick = {
                        previousUpdateTime = System.currentTimeMillis()
                        isStarted = true
                    }) {
                        Text(text = "Start")
                    }

                    Button(modifier = Modifier.padding(8.dp), onClick = {
                        isStarted = false
                    }) {
                        Text(text = "Pause")
                    }

                    Button(modifier = Modifier.padding(8.dp), onClick = {
                        isStarted = false
                        remainingTimeMillis = 0
                    }) {
                        Text(text = "Reset")
                    }
                }
            }
        }
    }

    DisposableEffect(key1 = Unit, effect = {
        val job = coroutineScope.launch(context = Dispatchers.Unconfined) {
            while (true) {
                yield()

                withContext(Dispatchers.Main) {
                    if (isStarted) {
                        val currentTime = System.currentTimeMillis()

                        remainingTimeMillis = (remainingTimeMillis - (currentTime - previousUpdateTime)).coerceAtLeast(0)
                        previousUpdateTime = currentTime
                    }
                }
            }
        }

        onDispose {
            job.cancel()
        }
    })
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
