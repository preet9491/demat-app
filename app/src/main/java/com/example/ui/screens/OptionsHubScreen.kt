package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OptionContract
import com.example.model.OptionStrategy
import com.example.ui.components.options.OptionChainTable
import com.example.ui.components.options.StrategyPayoffDiagram
import com.example.ui.theme.*

@Composable
fun OptionsHubScreen(
    optionChain: List<OptionContract>,
    strategies: List<OptionStrategy>,
    onSelectStrategy: (OptionStrategy) -> Unit,
    onExecuteStrategy: (OptionStrategy) -> Unit,
    onSelectStrikeForTrade: (OptionContract, Boolean) -> Unit
) {
    var selectedHubTab by remember { mutableStateOf(1) } // 0: Option Chain, 1: Strategy Builder

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Header
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "F&O Derivatives Hub",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "NIFTY 50 • 24 OCT EXPIRY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KiteBlue
                    )
                }

                Surface(
                    color = UpstoxIndigo.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "UPSTOX PRO ENGINE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = UpstoxCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub Tab Selector
            TabRow(
                selectedTabIndex = selectedHubTab,
                containerColor = DarkSurfaceElevated,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedHubTab == 0,
                    onClick = { selectedHubTab = 0 },
                    text = {
                        Text(
                            text = "OPTION CHAIN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedHubTab == 0) MaterialTheme.colorScheme.primary else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedHubTab == 1,
                    onClick = { selectedHubTab = 1 },
                    text = {
                        Text(
                            text = "STRATEGY BUILDER",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedHubTab == 1) MaterialTheme.colorScheme.primary else TextSecondary
                        )
                    }
                )
            }
        }

        if (selectedHubTab == 0) {
            // Option Chain View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    OptionChainTable(
                        contracts = optionChain,
                        atmStrike = 24850.0,
                        onSelectStrike = onSelectStrikeForTrade
                    )
                }
            }
        } else {
            // Strategy Builder View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(strategies, key = { it.id }) { strategy ->
                    StrategyCard(
                        strategy = strategy,
                        onExecute = { onExecuteStrategy(strategy) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StrategyCard(
    strategy: OptionStrategy,
    onExecute: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth().testTag("strategy_card_${strategy.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Outlook
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strategy.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = strategy.outlook,
                        fontSize = 11.sp,
                        color = KiteBlue,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = MarketGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${strategy.popPercent}% POP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MarketGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Strategy Legs Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                strategy.legs.forEach { leg ->
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${leg.action.name} ${leg.strike.toInt()} ${leg.optionType} @ ₹${leg.premium}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (leg.action == com.example.model.TransactionType.BUY) MarketGreen else MarketRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Payoff Diagram
            StrategyPayoffDiagram(strategy = strategy)

            Spacer(modifier = Modifier.height(12.dp))

            // Margin & Risk-Reward info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Margin Required", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "₹${String.format("%,.0f", strategy.marginRequired)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Risk : Reward", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = strategy.riskReward,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1-Click Execute Strategy Button
            Button(
                onClick = onExecute,
                colors = ButtonDefaults.buttonColors(containerColor = UpstoxIndigo),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("execute_strategy_${strategy.id}")
            ) {
                Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Execute Strategy (${strategy.legs.size} Legs)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
