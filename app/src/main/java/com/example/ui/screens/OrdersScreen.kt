package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.TransactionType
import com.example.ui.theme.*

@Composable
fun OrdersScreen(
    orders: List<Order>
) {
    var selectedOrderTab by remember { mutableStateOf(0) } // 0: Executed, 1: Open, 2: GTT

    val filteredOrders = remember(orders, selectedOrderTab) {
        when (selectedOrderTab) {
            0 -> orders.filter { it.status == OrderStatus.EXECUTED }
            1 -> orders.filter { it.status == OrderStatus.OPEN || it.status == OrderStatus.TRIGGER_PENDING }
            else -> emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Order Book",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${orders.size} Total Orders",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Tabs: Executed vs Open vs GTT
        TabRow(
            selectedTabIndex = selectedOrderTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = selectedOrderTab == 0,
                onClick = { selectedOrderTab = 0 },
                text = {
                    Text(
                        "EXECUTED (${orders.count { it.status == OrderStatus.EXECUTED }})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedOrderTab == 0) MaterialTheme.colorScheme.primary else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedOrderTab == 1,
                onClick = { selectedOrderTab = 1 },
                text = {
                    Text(
                        "OPEN (${orders.count { it.status == OrderStatus.OPEN }})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedOrderTab == 1) MaterialTheme.colorScheme.primary else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedOrderTab == 2,
                onClick = { selectedOrderTab = 2 },
                text = {
                    Text(
                        "GTT (0)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedOrderTab == 2) MaterialTheme.colorScheme.primary else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No ${if (selectedOrderTab == 0) "executed" else "open"} orders found",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderItemCard(order = order)
                }
            }
        }
    }
}

@Composable
private fun OrderItemCard(order: Order) {
    val isBuy = order.transactionType == TransactionType.BUY
    val isExecuted = order.status == OrderStatus.EXECUTED

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth().testTag("order_item_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = if (isBuy) KiteBlue.copy(alpha = 0.2f) else KiteOrange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = order.transactionType.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBuy) KiteBlue else KiteOrange,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = order.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = if (isExecuted) MarketGreen.copy(alpha = 0.15f) else Color(0xFFFFB800).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isExecuted) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = if (isExecuted) MarketGreen else Color(0xFFFFB800),
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = order.status.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExecuted) MarketGreen else Color(0xFFFFB800)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = order.stockName,
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Product", fontSize = 10.sp, color = TextMuted)
                        Text(order.productType.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Column {
                        Text("Qty", fontSize = 10.sp, color = TextMuted)
                        Text("${order.quantity}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Column {
                        Text("Order Price", fontSize = 10.sp, color = TextMuted)
                        Text("₹${order.price} (${order.orderType.name})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                Text(
                    text = order.timestamp,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
