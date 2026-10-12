package com.example.ui.components.options

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OptionContract
import com.example.model.OptionStrategy
import com.example.model.TransactionType
import com.example.ui.theme.*

@Composable
fun OptionChainTable(
    contracts: List<OptionContract>,
    atmStrike: Double = 24850.0,
    onSelectStrike: (OptionContract, isCall: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(vertical = 10.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CALLS (CE)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MarketGreen,
                modifier = Modifier.weight(1.5f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = "STRIKE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = "PUTS (PE)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MarketRed,
                modifier = Modifier.weight(1.5f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        HorizontalDivider(color = DarkBorder)

        // Subheader (OI | LTP)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackground.copy(alpha = 0.5f))
                .padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            Text("OI", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text("LTP", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.8f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            Text("Price", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text("LTP", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.8f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
            Text("OI", fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }

        HorizontalDivider(color = DarkBorder)

        contracts.forEach { contract ->
            val isAtm = contract.strike == atmStrike
            val isItmCall = contract.strike < atmStrike
            val isItmPut = contract.strike > atmStrike

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isAtm) KiteBlue.copy(alpha = 0.12f)
                        else Color.Transparent
                    )
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Side (CE)
                Row(
                    modifier = Modifier
                        .weight(1.5f)
                        .background(
                            if (isItmCall) MarketGreen.copy(alpha = 0.06f) else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectStrike(contract, true) }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${contract.ceOi / 1000}k",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${contract.ceLtp}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MarketGreen
                        )
                        Text(
                            text = "Δ ${contract.ceDelta}",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Strike Price Center
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = if (isAtm) KiteBlue.copy(alpha = 0.35f) else DarkSurface,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "${contract.strike.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAtm) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Put Side (PE)
                Row(
                    modifier = Modifier
                        .weight(1.5f)
                        .background(
                            if (isItmPut) MarketRed.copy(alpha = 0.06f) else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectStrike(contract, false) }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "₹${contract.peLtp}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MarketRed
                        )
                        Text(
                            text = "Δ ${contract.peDelta}",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${contract.peOi / 1000}k",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
            HorizontalDivider(color = DarkBorder.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun StrategyPayoffDiagram(
    strategy: OptionStrategy,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Payoff Diagram (At Expiry)",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Max Profit: +₹${String.format("%,.0f", strategy.maxProfit)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MarketGreen
                )
                Text(
                    text = "Max Loss: -₹${String.format("%,.0f", strategy.maxLoss)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MarketRed
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Payoff Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("payoff_canvas")
        ) {
            val w = size.width
            val h = size.height

            // Center Zero-profit line
            val zeroY = h * 0.50f
            drawLine(
                color = TextMuted.copy(alpha = 0.6f),
                start = Offset(0f, zeroY),
                end = Offset(w, zeroY),
                strokeWidth = 1.5f
            )

            // Current price vertical dashed indicator
            val currentPriceX = w * 0.50f
            drawLine(
                color = KiteBlue,
                start = Offset(currentPriceX, 0f),
                end = Offset(currentPriceX, h),
                strokeWidth = 2f
            )

            // Draw representative Strategy Payoff Curve
            val payoffPath = Path()
            when (strategy.type) {
                com.example.model.StrategyType.BULL_CALL_SPREAD -> {
                    // Capped loss on left, rising, capped profit on right
                    val profitY = zeroY - (h * 0.35f)
                    val lossY = zeroY + (h * 0.35f)
                    val strike1X = w * 0.35f
                    val strike2X = w * 0.70f

                    payoffPath.moveTo(0f, lossY)
                    payoffPath.lineTo(strike1X, lossY)
                    payoffPath.lineTo(strike2X, profitY)
                    payoffPath.lineTo(w, profitY)
                }
                com.example.model.StrategyType.BEAR_PUT_SPREAD -> {
                    val profitY = zeroY - (h * 0.35f)
                    val lossY = zeroY + (h * 0.35f)
                    val strike1X = w * 0.30f
                    val strike2X = w * 0.65f

                    payoffPath.moveTo(0f, profitY)
                    payoffPath.lineTo(strike1X, profitY)
                    payoffPath.lineTo(strike2X, lossY)
                    payoffPath.lineTo(w, lossY)
                }
                com.example.model.StrategyType.SHORT_STRADDLE -> {
                    // Mountain peak at center ATM strike, falling off both sides
                    val peakProfitY = zeroY - (h * 0.45f)
                    val deepLossY = h * 0.95f
                    val atmX = w * 0.50f

                    payoffPath.moveTo(w * 0.15f, deepLossY)
                    payoffPath.lineTo(atmX, peakProfitY)
                    payoffPath.lineTo(w * 0.85f, deepLossY)
                }
                com.example.model.StrategyType.IRON_CONDOR -> {
                    // Flat maximum profit zone in middle, capped wings
                    val profitY = zeroY - (h * 0.30f)
                    val lossY = zeroY + (h * 0.30f)
                    val x1 = w * 0.20f
                    val x2 = w * 0.38f
                    val x3 = w * 0.62f
                    val x4 = w * 0.80f

                    payoffPath.moveTo(0f, lossY)
                    payoffPath.lineTo(x1, lossY)
                    payoffPath.lineTo(x2, profitY)
                    payoffPath.lineTo(x3, profitY)
                    payoffPath.lineTo(x4, lossY)
                    payoffPath.lineTo(w, lossY)
                }
                else -> {
                    payoffPath.moveTo(0f, zeroY + 20f)
                    payoffPath.lineTo(w, zeroY - 20f)
                }
            }

            drawPath(
                path = payoffPath,
                color = MarketGreen,
                style = Stroke(width = 3.5f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Breakeven & POP row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Breakeven: ${strategy.breakevens.joinToString(", ") { "₹${it.toInt()}" }}",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = "POP (Probability): ${strategy.popPercent}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = KiteBlue
            )
        }
    }
}
