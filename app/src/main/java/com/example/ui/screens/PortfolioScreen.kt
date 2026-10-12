package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrokerTheme
import com.example.model.Holding
import com.example.model.Position
import com.example.ui.theme.*

@Composable
fun PortfolioScreen(
    positions: List<Position>,
    holdings: List<Holding>,
    totalPositionsPnl: Double,
    totalHoldingsInvested: Double,
    totalHoldingsCurrent: Double,
    totalHoldingsPnl: Double,
    brokerTheme: BrokerTheme,
    isLiveTicking: Boolean,
    onToggleLiveTicking: () -> Unit,
    onOpenPnlCustomizer: () -> Unit,
    onOpenShareCard: () -> Unit,
    onSquareOffPosition: (Position) -> Unit
) {
    var selectedPortfolioTab by remember { mutableStateOf(0) } // 0: Positions, 1: Holdings

    val totalPortfolioPnl = if (selectedPortfolioTab == 0) totalPositionsPnl else totalHoldingsPnl
    val isProfit = totalPortfolioPnl >= 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Hero P&L Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("hero_pnl_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isProfit) MarketGreen.copy(alpha = 0.3f) else MarketRed.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                (if (isProfit) MarketGreen else MarketRed).copy(alpha = 0.12f),
                                DarkSurfaceElevated
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                // Header with Broker Pill & Live Tick Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (selectedPortfolioTab == 0) "TOTAL P&L (POSITIONS)" else "PORTFOLIO VALUE (HOLDINGS)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Live Ticks indicator button
                    Surface(
                        onClick = onToggleLiveTicking,
                        shape = RoundedCornerShape(12.dp),
                        color = if (isLiveTicking) MarketGreen.copy(alpha = 0.18f) else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isLiveTicking) MarketGreen else DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiveTicking) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = if (isLiveTicking) MarketGreen else TextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isLiveTicking) "LIVE TICKS" else "PAUSED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLiveTicking) MarketGreen else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Big Bold P&L Number
                Text(
                    text = "${if (isProfit) "+₹" else "-₹"}${String.format("%,.2f", kotlin.math.abs(totalPortfolioPnl))}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isProfit) MarketGreen else MarketRed
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Row
                if (selectedPortfolioTab == 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Realized P&L", fontSize = 11.sp, color = TextMuted)
                            Text("₹0.00", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Unrealized P&L", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = "${if (isProfit) "+₹" else "-₹"}${String.format("%,.2f", kotlin.math.abs(totalPositionsPnl))}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isProfit) MarketGreen else MarketRed
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Invested Value", fontSize = 11.sp, color = TextMuted)
                            Text("₹${String.format("%,.2f", totalHoldingsInvested)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Current Value", fontSize = 11.sp, color = TextMuted)
                            Text("₹${String.format("%,.2f", totalHoldingsCurrent)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MarketGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DarkBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // P&L Dashboard Control Bar ("jo hame dikhana he wahi dikhe")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenPnlCustomizer,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GrowwTeal),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(40.dp)
                            .testTag("open_pnl_customizer_button")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkBackground)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Customize P&L", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBackground)
                    }

                    OutlinedButton(
                        onClick = onOpenShareCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("share_pnl_card_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Card", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Segmented Tabs: POSITIONS vs HOLDINGS
        TabRow(
            selectedTabIndex = selectedPortfolioTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = selectedPortfolioTab == 0,
                onClick = { selectedPortfolioTab = 0 },
                text = {
                    Text(
                        text = "POSITIONS (${positions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedPortfolioTab == 0) MaterialTheme.colorScheme.primary else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedPortfolioTab == 1,
                onClick = { selectedPortfolioTab = 1 },
                text = {
                    Text(
                        text = "HOLDINGS (${holdings.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedPortfolioTab == 1) MaterialTheme.colorScheme.primary else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Content
        if (selectedPortfolioTab == 0) {
            // Positions List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(positions, key = { it.id }) { pos ->
                    PositionCard(
                        position = pos,
                        onSquareOff = { onSquareOffPosition(pos) },
                        onEdit = onOpenPnlCustomizer
                    )
                }
            }
        } else {
            // Holdings List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(holdings, key = { it.symbol }) { holding ->
                    HoldingCard(holding = holding)
                }
            }
        }
    }
}

@Composable
private fun PositionCard(
    position: Position,
    onSquareOff: () -> Unit,
    onEdit: () -> Unit
) {
    val isProfit = position.totalPnl >= 0

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth().testTag("position_card_${position.symbol}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Symbol & P&L
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = position.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = position.product.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "${if (isProfit) "+₹" else "-₹"}${String.format("%,.2f", kotlin.math.abs(position.totalPnl))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isProfit) MarketGreen else MarketRed
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = position.name,
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Details Row: Qty, Buy Avg, LTP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Qty", fontSize = 10.sp, color = TextMuted)
                        Text("${position.quantity}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column {
                        Text("Buy Avg", fontSize = 10.sp, color = TextMuted)
                        Text("₹${position.buyPrice}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column {
                        Text("LTP", fontSize = 10.sp, color = TextMuted)
                        Text("₹${position.ltp}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isProfit) MarketGreen else MarketRed)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit P&L", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    OutlinedButton(
                        onClick = onSquareOff,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Exit", fontSize = 10.sp, color = MarketRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HoldingCard(holding: Holding) {
    val isProfit = holding.totalPnl >= 0

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth().testTag("holding_card_${holding.symbol}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = holding.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${holding.quantity} shares • Avg ₹${holding.avgPrice}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${String.format("%,.2f", holding.currentValue)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${if (isProfit) "+" else ""}₹${String.format("%,.2f", holding.totalPnl)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isProfit) MarketGreen else MarketRed
                    )
                }
            }
        }
    }
}
