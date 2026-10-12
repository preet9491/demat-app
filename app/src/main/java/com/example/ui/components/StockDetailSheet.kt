package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Stock
import com.example.model.TransactionType
import com.example.ui.components.chart.InteractiveStockChart
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailSheet(
    stock: Stock,
    onDismiss: () -> Unit,
    onTradeClick: (TransactionType) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stock Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stock.symbol,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Surface(
                                    color = DarkSurfaceElevated,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = stock.exchange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${stock.name} • ${stock.sector}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_stock_detail_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }
                }

                // Price & Day Stats Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "₹${stock.ltp}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${if (stock.change >= 0) "+" else ""}₹${stock.change}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (stock.change >= 0) MarketGreen else MarketRed
                                )
                                Text(
                                    text = "(${if (stock.change >= 0) "+" else ""}${stock.changePercent}%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (stock.change >= 0) MarketGreen else MarketRed
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Vol: ${String.format("%,d", stock.volume)}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "VWAP: ₹${stock.vwap}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Interactive Chart with technical indicators
                item {
                    InteractiveStockChart(candles = stock.candles1D)
                }

                // High / Low Range Bars
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Today's Range
                            RangeBar(
                                label = "Today's Low / High",
                                low = stock.low,
                                high = stock.high,
                                current = stock.ltp
                            )
                            HorizontalDivider(color = DarkBorder)
                            // 52 Week Range
                            RangeBar(
                                label = "52-Week Low / High",
                                low = stock.week52Low,
                                high = stock.week52High,
                                current = stock.ltp
                            )
                        }
                    }
                }

                // Market Depth (Level 2 Ladder)
                stock.depth?.let { depth ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Market Depth (Level 2 Order Book)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Table Header
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text("Orders", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.7f))
                                    Text("Qty", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.9f))
                                    Text("Bid (Buy)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MarketGreen, modifier = Modifier.weight(1.1f))
                                    Text("Ask (Sell)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MarketRed, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                                    Text("Qty", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                                    Text("Orders", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                                }

                                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                                val maxRows = maxOf(depth.bids.size, depth.asks.size)
                                for (i in 0 until maxRows) {
                                    val bid = depth.bids.getOrNull(i)
                                    val ask = depth.asks.getOrNull(i)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(bid?.orders?.toString() ?: "-", fontSize = 11.sp, color = TextMuted, modifier = Modifier.weight(0.7f))
                                        Text(bid?.quantity?.toString() ?: "-", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.weight(0.9f))
                                        Text(bid?.price?.let { "₹$it" } ?: "-", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MarketGreen, modifier = Modifier.weight(1.1f))

                                        Text(ask?.price?.let { "₹$it" } ?: "-", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MarketRed, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                                        Text(ask?.quantity?.toString() ?: "-", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                                        Text(ask?.orders?.toString() ?: "-", fontSize = 11.sp, color = TextMuted, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Total Buy / Sell Ratio Progress Bar
                                val total = depth.totalBuyQty + depth.totalSellQty
                                val buyPct = if (total > 0) (depth.totalBuyQty.toFloat() / total) else 0.5f

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Buy: ${String.format("%,d", depth.totalBuyQty)} (${(buyPct * 100).toInt()}%)", fontSize = 10.sp, color = MarketGreen)
                                    Text("Total Sell: ${String.format("%,d", depth.totalSellQty)} (${((1f - buyPct) * 100).toInt()}%)", fontSize = 10.sp, color = MarketRed)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { buyPct },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = MarketGreen,
                                    trackColor = MarketRed
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fixed Bottom Action Bar: BUY & SELL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onTradeClick(TransactionType.BUY) },
                    colors = ButtonDefaults.buttonColors(containerColor = KiteBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("stock_detail_buy_button")
                ) {
                    Text("BUY", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }

                Button(
                    onClick = { onTradeClick(TransactionType.SELL) },
                    colors = ButtonDefaults.buttonColors(containerColor = KiteOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("stock_detail_sell_button")
                ) {
                    Text("SELL", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun RangeBar(
    label: String,
    low: Double,
    high: Double,
    current: Double
) {
    val progress = if (high > low) {
        ((current - low) / (high - low)).toFloat().coerceIn(0f, 1f)
    } else 0.5f

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("₹$low", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text("LTP: ₹$current", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MarketGreen)
            Text("₹$high", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = KiteBlue,
            trackColor = DarkBorder
        )
    }
}
