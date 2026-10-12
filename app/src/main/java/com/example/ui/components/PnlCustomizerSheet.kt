package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Position
import com.example.model.ProductType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PnlCustomizerSheet(
    currentTotalPnl: Double,
    positions: List<Position>,
    isLiveTicking: Boolean,
    onToggleLiveTicking: () -> Unit,
    onApplyPreset: (String) -> Unit,
    onApplyTargetPnl: (Double) -> Unit,
    onUpdatePosition: (String, Double, Double, Int) -> Unit,
    onOpenAddCustomDialog: () -> Unit,
    onDismiss: () -> Unit
) {
    var targetPnlInput by remember { mutableStateOf(String.format("%.0f", currentTotalPnl)) }
    var selectedPositionToEdit by remember { mutableStateOf<Position?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "P&L Customizer",
                            tint = GrowwTeal
                        )
                        Column {
                            Text(
                                text = "P&L Studio & Dashboard Controller",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Control what you display on your P&L dashboard",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_pnl_customizer_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            }

            // Target Total P&L Override Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Set Custom Target P&L (Total)",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = targetPnlInput,
                                onValueChange = { targetPnlInput = it },
                                label = { Text("Exact Amount (₹)") },
                                placeholder = { Text("e.g. 145000") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_pnl_target_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GrowwTeal,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )

                            Button(
                                onClick = {
                                    targetPnlInput.toDoubleOrNull()?.let { target ->
                                        onApplyTargetPnl(target)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GrowwTeal),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("apply_custom_pnl_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Apply")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Apply", fontWeight = FontWeight.Bold, color = DarkBackground)
                            }
                        }
                    }
                }
            }

            // Quick Preset Showcase Buttons
            item {
                Text(
                    text = "Quick Showcase Presets",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetChip(
                            label = "🚀 Super Green (+₹1.45L)",
                            color = MarketGreen,
                            modifier = Modifier.weight(1f)
                        ) {
                            onApplyPreset("SUPER_GREEN")
                            targetPnlInput = "145280"
                        }
                        PresetChip(
                            label = "🔥 Expiry Jackpot (+₹4.85L)",
                            color = MarketGreen,
                            modifier = Modifier.weight(1f)
                        ) {
                            onApplyPreset("EXPIRY_JACKPOT")
                            targetPnlInput = "485600"
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetChip(
                            label = "🎯 Steady Pro (+₹42.5K)",
                            color = KiteBlue,
                            modifier = Modifier.weight(1f)
                        ) {
                            onApplyPreset("CONSISTENT_PRO")
                            targetPnlInput = "42500"
                        }
                        PresetChip(
                            label = "🛡️ Small Loss (-₹6.4K)",
                            color = MarketRed,
                            modifier = Modifier.weight(1f)
                        ) {
                            onApplyPreset("RED_DEFENSIVE")
                            targetPnlInput = "-6400"
                        }
                    }

                    OutlinedButton(
                        onClick = { onApplyPreset("RESET_ORGANIC") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset to Organic Market Values", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // Live Tick Fluctuations Switch
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Live Ticking Simulation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                if (isLiveTicking) {
                                    Surface(
                                        color = MarketGreen.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "LIVE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MarketGreen,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (isLiveTicking) "Prices fluctuate smoothly every 1.5s" else "Values are frozen steady for screenshots",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = isLiveTicking,
                            onCheckedChange = { onToggleLiveTicking() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GrowwTeal
                            )
                        )
                    }
                }
            }

            // Add Custom Position Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Positions (${positions.size})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Button(
                        onClick = onOpenAddCustomDialog,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UpstoxIndigo),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Custom Trade", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Individual Positions Customizer List
            items(positions, key = { it.id }) { pos ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = pos.symbol,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Surface(
                                    color = DarkSurfaceElevated,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = pos.product.name,
                                        fontSize = 9.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Qty: ${pos.quantity} • Buy: ₹${pos.buyPrice} • LTP: ₹${pos.ltp}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (pos.totalPnl >= 0) "+₹" else "-₹"}${String.format("%,.2f", kotlin.math.abs(pos.totalPnl))}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (pos.totalPnl >= 0) MarketGreen else MarketRed
                            )
                            IconButton(
                                onClick = { selectedPositionToEdit = pos },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Position", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Position Edit Dialog
    selectedPositionToEdit?.let { pos ->
        var editLtp by remember { mutableStateOf(pos.ltp.toString()) }
        var editPnl by remember { mutableStateOf(pos.totalPnl.toString()) }
        var editQty by remember { mutableStateOf(pos.quantity.toString()) }

        AlertDialog(
            onDismissRequest = { selectedPositionToEdit = null },
            title = { Text("Edit ${pos.symbol} P&L", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editPnl,
                        onValueChange = { editPnl = it },
                        label = { Text("Custom P&L (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = editLtp,
                        onValueChange = { editLtp = it },
                        label = { Text("LTP (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = editQty,
                        onValueChange = { editQty = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pnl = editPnl.toDoubleOrNull() ?: pos.totalPnl
                        val ltp = editLtp.toDoubleOrNull() ?: pos.ltp
                        val q = editQty.toIntOrNull() ?: pos.quantity
                        onUpdatePosition(pos.id, ltp, pnl, q)
                        selectedPositionToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GrowwTeal)
                ) {
                    Text("Save", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPositionToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PresetChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.14f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
