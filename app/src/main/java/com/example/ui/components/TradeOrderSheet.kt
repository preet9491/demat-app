package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
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
import com.example.model.OrderType
import com.example.model.ProductType
import com.example.model.Stock
import com.example.model.TransactionType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeOrderSheet(
    stock: Stock,
    initialType: TransactionType = TransactionType.BUY,
    availableMargin: Double,
    onDismiss: () -> Unit,
    onExecuteOrder: (TransactionType, OrderType, ProductType, Int, Double, Double) -> Unit
) {
    var transactionType by remember { mutableStateOf(initialType) }
    var productType by remember { mutableStateOf(ProductType.MIS) }
    var orderType by remember { mutableStateOf(OrderType.MARKET) }
    var quantityText by remember { mutableStateOf("50") }
    var priceText by remember { mutableStateOf(String.format("%.2f", stock.ltp)) }
    var triggerPriceText by remember { mutableStateOf(String.format("%.2f", stock.ltp * 0.98)) }

    val qty = quantityText.toIntOrNull() ?: 1
    val limitPrice = priceText.toDoubleOrNull() ?: stock.ltp
    val triggerPrice = triggerPriceText.toDoubleOrNull() ?: 0.0

    val effectivePrice = if (orderType == OrderType.MARKET) stock.ltp else limitPrice
    val totalRequiredMargin = if (productType == ProductType.MIS) {
        (effectivePrice * qty) / 5.0 // 5x leverage
    } else {
        effectivePrice * qty
    }

    val hasSufficientFunds = transactionType == TransactionType.SELL || availableMargin >= totalRequiredMargin

    val themeColor = if (transactionType == TransactionType.BUY) KiteBlue else KiteOrange

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header with Stock Info & Buy/Sell Segmented Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stock.symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = stock.exchange,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "₹${stock.ltp} (${if (stock.change >= 0) "+" else ""}${stock.changePercent}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (stock.change >= 0) MarketGreen else MarketRed
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_order_dialog_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buy / Sell Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (transactionType == TransactionType.BUY) KiteBlue else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { transactionType = TransactionType.BUY }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BUY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (transactionType == TransactionType.BUY) Color.White else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (transactionType == TransactionType.SELL) KiteOrange else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { transactionType = TransactionType.SELL }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SELL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (transactionType == TransactionType.SELL) Color.White else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Type Tabs (MIS / CNC)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProductType.values().forEach { pType ->
                    val isSelected = productType == pType
                    Surface(
                        onClick = { productType = pType },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) themeColor.copy(alpha = 0.2f) else DarkSurface,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, themeColor) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = pType.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) themeColor else TextPrimary
                            )
                            Text(
                                text = if (pType == ProductType.MIS) "Intraday 5x" else "Delivery",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Type Selector (Market, Limit, SL, SL-M)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OrderType.values().forEach { oType ->
                    val isSelected = orderType == oType
                    Surface(
                        onClick = { orderType = oType },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) themeColor else DarkSurface,
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = oType.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity Input with quick stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("order_quantity_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        unfocusedBorderColor = DarkBorder
                    ),
                    leadingIcon = {
                        IconButton(onClick = {
                            val current = quantityText.toIntOrNull() ?: 1
                            if (current > 1) quantityText = (current - 1).toString()
                        }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextSecondary)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            val current = quantityText.toIntOrNull() ?: 0
                            quantityText = (current + 1).toString()
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextSecondary)
                        }
                    }
                )

                if (orderType != OrderType.MARKET) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("order_price_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                }
            }

            if (orderType == OrderType.SL || orderType == OrderType.SL_M) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = triggerPriceText,
                    onValueChange = { triggerPriceText = it },
                    label = { Text("Trigger Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("order_trigger_price_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        unfocusedBorderColor = DarkBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Margin & Funds Requirement Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Approx Margin", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = "₹${String.format("%,.2f", totalRequiredMargin)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Available Margin", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = "₹${String.format("%,.2f", availableMargin)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasSufficientFunds) MarketGreen else MarketRed
                    )
                }
            }

            if (!hasSufficientFunds) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Insufficient funds for this trade. Add funds or switch to Intraday (MIS 5x).",
                    fontSize = 11.sp,
                    color = MarketRed
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Place Order Button
            Button(
                onClick = {
                    onExecuteOrder(
                        transactionType,
                        orderType,
                        productType,
                        qty,
                        effectivePrice,
                        triggerPrice
                    )
                },
                enabled = hasSufficientFunds && qty > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_trade_order_button"),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${transactionType.name} $qty QTY @ ${if (orderType == OrderType.MARKET) "MARKET" else "₹$effectivePrice"}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
