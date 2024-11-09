package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.viewmodel.FareCalculatorViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FareCalculatorScreen(cardState: CardState) {
    val viewModel = remember { FareCalculatorViewModel.getInstance() }

    // Update card state when it changes
    LaunchedEffect(cardState) {
        viewModel.updateCardState(cardState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)  // Reduced from 16.dp to 8.dp
    ) {
        Text(
            text = "Fare Calculator",
            style = MaterialTheme.typography.h5
        )

        // From Station Dropdown
        ExposedDropdownMenuBox(
            expanded = viewModel.fromExpanded,
            onExpandedChange = { viewModel.toggleFromExpanded() }
        ) {
            TextField(
                value = viewModel.fromStation?.name ?: "Select Origin Station",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.fromExpanded) },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = viewModel.fromExpanded,
                onDismissRequest = { viewModel.dismissDropdowns() }
            ) {
                viewModel.stations.forEach { station ->
                    DropdownMenuItem(
                        onClick = { viewModel.updateFromStation(station) }
                    ) {
                        Text(text = station.name)
                    }
                }
            }
        }

        // To Station Dropdown
        ExposedDropdownMenuBox(
            expanded = viewModel.toExpanded,
            onExpandedChange = { viewModel.toggleToExpanded() }
        ) {
            TextField(
                value = viewModel.toStation?.name ?: "Select Destination Station",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.toExpanded) },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = viewModel.toExpanded,
                onDismissRequest = { viewModel.dismissDropdowns() }
            ) {
                viewModel.stations.forEach { station ->
                    DropdownMenuItem(
                        onClick = { viewModel.updateToStation(station) }
                    ) {
                        Text(text = station.name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fare Display
        if (viewModel.fromStation != null && viewModel.toStation != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MRT Pass / Rapid Pass",
                            style = MaterialTheme.typography.h6
                        )
                        Text(
                            text = "৳ ${viewModel.discountedFare}",
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Regular ৳ ${viewModel.calculatedFare}",
                            style = MaterialTheme.typography.caption
                        )
                        Text(
                            text = "Discount ৳ ${viewModel.getSavings()}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(2.dp))

                    when (cardState) {
                        is CardState.Balance -> {
                            val balance = cardState.amount
                            if (balance >= viewModel.calculatedFare) {
                                Text(
                                    text = "Your balance (৳ $balance) is sufficient for this trip",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    text = "Your balance (৳ $balance) is not enough for this trip",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.error.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        else -> {
                            Text(
                                text = "Tap your card to check if you have sufficient balance",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}