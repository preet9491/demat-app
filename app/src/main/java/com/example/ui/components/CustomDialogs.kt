package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.BrokerTheme
import com.example.model.ProductType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddCustomPositionDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, ProductType, Int, Double, Double, Double) -> Unit
) {
    var symbol by remember { mutableStateOf("NIFTY 25000 CE") }
    var name by remember { mutableStateOf("NIFTY 24 OCT 25000 CALL") }
    var quantityText by remember { mutableStateOf("1500") }
    var buyPriceText by remember { mutableStateOf("94.50") }
    var ltpText by remember { mutableStateOf("182.00") }
    var customPnlText by remember { mutableStateOf("131250.00") }
    var productType by remember { mutableStateOf(ProductType.MIS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Custom Trade / Position",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Symbol / Contract") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_position_symbol_input")
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contract Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = buyPriceText,
                        onValueChange = { buyPriceText = it },
                        label = { Text("Buy Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ltpText,
                        onValueChange = { ltpText = it },
                        label = { Text("LTP (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = customPnlText,
                        onValueChange = { customPnlText = it },
                        label = { Text("Custom P&L (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val q = quantityText.toIntOrNull() ?: 100
                    val buy = buyPriceText.toDoubleOrNull() ?: 100.0
                    val ltp = ltpText.toDoubleOrNull() ?: 150.0
                    val pnl = customPnlText.toDoubleOrNull() ?: ((ltp - buy) * q)
                    onAdd(symbol, name, productType, q, buy, ltp, pnl)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = UpstoxIndigo),
                modifier = Modifier.testTag("confirm_add_custom_position_button")
            ) {
                Text("Add Position", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PnlShareCardDialog(
    totalPnl: Double,
    brokerTheme: BrokerTheme,
    clientId: String,
    onDismiss: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    val isProfit = totalPnl >= 0
    val returnPct = if (isProfit) 38.6 else -4.2

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isProfit) MarketGreen else MarketRed),
            modifier = Modifier.fillMaxWidth().testTag("pnl_share_card_container")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                if (isProfit) MarketGreen.copy(alpha = 0.15f) else MarketRed.copy(alpha = 0.15f),
                                DarkSurfaceElevated
                            )
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Broker Badge & Verified Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = when (brokerTheme) {
                            BrokerTheme.ZERODHA_KITE -> KiteBlue
                            BrokerTheme.GROWW_CLEAN -> GrowwTeal
                            BrokerTheme.UPSTOX_PRO -> UpstoxIndigo
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = brokerTheme.displayName.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = MarketGreen, modifier = Modifier.size(16.dp))
                        Text("VERIFIED P&L", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MarketGreen)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "TOTAL NET P&L",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${if (isProfit) "+₹" else "-₹"}${String.format("%,.2f", kotlin.math.abs(totalPnl))}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isProfit) MarketGreen else MarketRed
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = (if (isProfit) MarketGreen else MarketRed).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = if (isProfit) MarketGreen else MarketRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${if (isProfit) "+" else ""}$returnPct% Returns",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isProfit) MarketGreen else MarketRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(color = DarkBorder)

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Client ID", fontSize = 10.sp, color = TextMuted)
                        Text(clientId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Timestamp", fontSize = 10.sp, color = TextMuted)
                        Text(dateStr, fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Close Showcase", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddFundsDialog(
    currentMargin: Double,
    onDismiss: () -> Unit,
    onAddFunds: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("50000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = KiteBlue)
                Text("Add Trading Margin", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Current Available Margin: ₹${String.format("%,.2f", currentMargin)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Deposit Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("funds_amount_input")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("10000", "50000", "100000", "500000").forEach { preset ->
                        Surface(
                            onClick = { amountText = preset },
                            shape = RoundedCornerShape(6.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "+₹${preset.toInt() / 1000}k",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    amountText.toDoubleOrNull()?.let { onAddFunds(it) }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = KiteBlue),
                modifier = Modifier.testTag("confirm_add_funds_button")
            ) {
                Text("Deposit Instantly", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
