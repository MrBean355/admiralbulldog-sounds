/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mrbean355.admiralbulldog.gsi.GameStateIntegrationServer

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        val connected by GameStateIntegrationServer.isConnected().collectAsState(false)
        if (connected) {
            ConnectedScreen()
        } else {
            WaitingScreen()
        }
    }
}

@Composable
private fun ConnectedScreen() {
    Image(painterResource("images/poggies.png"), stringResource("cd_icon"), modifier = Modifier.size(64.dp))
    Text(stringResource("header_connected_to_dota"), fontSize = 22.sp)
    Text(stringResource("description_connected_to_dota"), fontSize = 18.sp)
}

@Composable
private fun WaitingScreen() {
    Image(painterResource("images/pause_champ.png"), stringResource("cd_icon"), modifier = Modifier.size(64.dp))
    Text(stringResource("header_waiting_for_dota"), fontSize = 22.sp)
    LinearProgressIndicator(Modifier.fillMaxWidth())
    Text(stringResource("description_waiting_for_dota"), fontSize = 18.sp)
}