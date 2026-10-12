package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrokerTheme
import com.example.model.MarketIndex
import com.example.model.Stock
import com.example.ui.theme.*

@Composable
fun WatchlistScreen(
    indices: List<MarketIndex>,
    stocks: List<Stock>,
    brokerTheme: BrokerTheme,
    searchQuery: String,
    watchlistTab: Int,
    onSearchChange: (String) -> Unit,
    onSelectWatchlistTab: (Int) -> Unit,
    onSelectBrokerTheme: (BrokerTheme) -> Unit,
    onStockClick: (Stock) -> Unit
) {
    val filteredStocks = remember(stocks, searchQuery, watchlistTab) {
        val baseList = when (watchlistTab) {
            1 -> stocks // Top Nifty
            2 -> stocks.filter { it.sector.contains("Bank", ignoreCase = true) || it.sector.contains("Finance", ignoreCase = true) }
            3 -> stocks.filter { it.sector.contains("Tech", ignoreCase = true) || it.sector.contains("Internet", ignoreCase = true) }
            4 -> stocks.filter { it.sector.contains("Auto", ignoreCase = true) || it.sector.contains("Energy", ignoreCase = true) }
            else -> stocks
        }
        if (searchQuery.isBlank()) baseList
        else baseList.filter {
            it.symbol.contains(searchQuery, ignoreCase = true) ||
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.sector.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Ticker Tape for Indices (NIFTY 50, SENSEX, BANK NIFTY, INDIA VIX)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(indices, key = { it.symbol }) { idx ->
                IndexTickerItem(index = idx)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Broker Mode Switcher Banner (Kite vs Groww vs Upstox)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BrokerTheme.values().forEach { theme ->
                val isSelected = brokerTheme == theme
                Surface(
                    onClick = { onSelectBrokerTheme(theme) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) {
                        when (theme) {
                            BrokerTheme.ZERODHA_KITE -> KiteBlue
                            BrokerTheme.GROWW_CLEAN -> GrowwTeal
                            BrokerTheme.UPSTOX_PRO -> UpstoxIndigo
                        }
                    } else DarkSurface,
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = when (theme) {
                                BrokerTheme.ZERODHA_KITE -> "Kite Mode"
                                BrokerTheme.GROWW_CLEAN -> "Groww Mode"
                                BrokerTheme.UPSTOX_PRO -> "Upstox Mode"
                            },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search stocks, indices, derivatives...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp)
                .testTag("watchlist_search_bar"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = DarkBorder
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Watchlist Tabs (Zerodha Kite style: W1, W2, W3, Banking, Tech)
        ScrollableTabRow(
            selectedTabIndex = watchlistTab - 1,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            divider = {}
        ) {
            listOf("Watchlist 1", "Banking", "Tech & New Age", "Auto & Core").forEachIndexed { index, title ->
                val isSelected = watchlistTab == index + 1
                Tab(
                    selected = isSelected,
                    onClick = { onSelectWatchlistTab(index + 1) },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else TextSecondary
                        )
                    }
                )
            }
        }

        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(top = 4.dp))

        // Stocks List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredStocks, key = { it.symbol }) { stock ->
                StockListItem(
                    stock = stock,
                    brokerTheme = brokerTheme,
                    onClick = { onStockClick(stock) }
                )
            }
        }
    }
}

@Composable
private fun IndexTickerItem(index: MarketIndex) {
    val isPositive = index.change >= 0
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = index.symbol,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = String.format("%,.2f", index.value),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${if (isPositive) "+" else ""}${index.changePercent}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPositive) MarketGreen else MarketRed
                )
            }
        }
    }
}

@Composable
private fun StockListItem(
    stock: Stock,
    brokerTheme: BrokerTheme,
    onClick: () -> Unit
) {
    val isPositive = stock.change >= 0

    // Subtle flash color animation when LTP ticks
    val tickColor by animateColorAsState(
        targetValue = if (isPositive) DarkSurface else DarkSurface,
        animationSpec = tween(500),
        label = "tick_anim"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = tickColor),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("stock_item_${stock.symbol}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Symbol & Exchange
            Column(modifier = Modifier.weight(1.2f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stock.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = stock.exchange,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = stock.name,
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }

            // Right: LTP & Change %
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%,.2f", stock.ltp)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isPositive) MarketGreen else MarketRed
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isPositive) MarketGreen else MarketRed,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}₹${stock.change} (${stock.changePercent}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPositive) MarketGreen else MarketRed
                    )
                }
            }
        }
    }
}
