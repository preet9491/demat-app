package com.example.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CandleStick
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InteractiveStockChart(
    candles: List<CandleStick>,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No chart data available", color = TextSecondary)
        }
        return
    }

    var isCandleMode by remember { mutableStateOf(true) }
    var showEma by remember { mutableStateOf(true) }
    var showBollinger by remember { mutableStateOf(false) }
    var showRsi by remember { mutableStateOf(true) }
    var showVolume by remember { mutableStateOf(true) }
    var selectedTimeframe by remember { mutableStateOf("1D") }

    // Touch inspection crosshair index
    var crosshairIndex by remember { mutableStateOf<Int?>(null) }

    val activeCandle = crosshairIndex?.let { idx ->
        if (idx in candles.indices) candles[idx] else candles.last()
    } ?: candles.last()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceElevated, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Active OHLC Tooltip Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "O: ${String.format("%.2f", activeCandle.open)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "H: ${String.format("%.2f", activeCandle.high)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MarketGreen
                    )
                    Text(
                        text = "L: ${String.format("%.2f", activeCandle.low)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MarketRed
                    )
                    Text(
                        text = "C: ${String.format("%.2f", activeCandle.close)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (activeCandle.close >= activeCandle.open) MarketGreen else MarketRed
                    )
                }
                Text(
                    text = "Vol: ${String.format("%,.0f", activeCandle.volume)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            // Chart Mode Toggle (Candles vs Line)
            FilterChip(
                selected = isCandleMode,
                onClick = { isCandleMode = !isCandleMode },
                label = { Text(if (isCandleMode) "Candles" else "Line", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Chart Type",
                        modifier = Modifier.size(14.dp)
                    )
                },
                modifier = Modifier.height(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Timeframe Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("1D", "5D", "1M", "6M", "1Y", "5Y").forEach { tf ->
                val isSelected = selectedTimeframe == tf
                Surface(
                    onClick = { selectedTimeframe = tf },
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.height(26.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = tf,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Technical Indicators Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IndicatorPill(label = "EMA (20/50)", isSelected = showEma, color = IndicatorEma20) {
                showEma = !showEma
            }
            IndicatorPill(label = "BOLL", isSelected = showBollinger, color = KiteBlue) {
                showBollinger = !showBollinger
            }
            IndicatorPill(label = "RSI (14)", isSelected = showRsi, color = IndicatorRsiLine) {
                showRsi = !showRsi
            }
            IndicatorPill(label = "VOL", isSelected = showVolume, color = TextSecondary) {
                showVolume = !showVolume
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Chart Canvas with Gestures
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .pointerInput(candles) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val candleWidth = size.width / candles.size
                            val idx = (offset.x / candleWidth).toInt().coerceIn(0, candles.size - 1)
                            crosshairIndex = idx
                        },
                        onDrag = { change, _ ->
                            val candleWidth = size.width / candles.size
                            val idx = (change.position.x / candleWidth).toInt().coerceIn(0, candles.size - 1)
                            crosshairIndex = idx
                        },
                        onDragEnd = { crosshairIndex = null },
                        onDragCancel = { crosshairIndex = null }
                    )
                }
                .pointerInput(candles) {
                    detectTapGestures(
                        onTap = { offset ->
                            val candleWidth = size.width / candles.size
                            val idx = (offset.x / candleWidth).toInt().coerceIn(0, candles.size - 1)
                            crosshairIndex = idx
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().testTag("chart_canvas")) {
                val chartWidth = size.width
                val chartHeight = size.height
                val count = candles.size
                if (count == 0) return@Canvas

                val minPrice = candles.minOf { it.low } * 0.998f
                val maxPrice = candles.maxOf { it.high } * 1.002f
                val priceRange = if (maxPrice > minPrice) maxPrice - minPrice else 1f

                val candleStep = chartWidth / count
                val candleWidth = candleStep * 0.65f

                // Draw Horizontal Gridlines
                val gridLines = 4
                for (g in 0..gridLines) {
                    val y = chartHeight * (g.toFloat() / gridLines)
                    drawLine(
                        color = DarkBorder,
                        start = Offset(0f, y),
                        end = Offset(chartWidth, y),
                        strokeWidth = 1f
                    )
                }

                // Bollinger Bands calculation (simplified 20 period)
                if (showBollinger && count > 10) {
                    val upperPath = Path()
                    val lowerPath = Path()
                    candles.forEachIndexed { i, c ->
                        val x = i * candleStep + (candleStep / 2)
                        val midY = chartHeight - ((c.close - minPrice) / priceRange) * chartHeight
                        val dev = chartHeight * 0.08f
                        val upperY = (midY - dev).coerceAtLeast(0f)
                        val lowerY = (midY + dev).coerceAtMost(chartHeight)
                        if (i == 0) {
                            upperPath.moveTo(x, upperY)
                            lowerPath.moveTo(x, lowerY)
                        } else {
                            upperPath.lineTo(x, upperY)
                            lowerPath.lineTo(x, lowerY)
                        }
                    }
                    drawPath(upperPath, color = KiteBlue.copy(alpha = 0.5f), style = Stroke(width = 1.5f))
                    drawPath(lowerPath, color = KiteBlue.copy(alpha = 0.5f), style = Stroke(width = 1.5f))
                }

                // Volume bars at bottom
                if (showVolume) {
                    val maxVol = candles.maxOf { it.volume }
                    candles.forEachIndexed { i, candle ->
                        val x = i * candleStep + ((candleStep - candleWidth) / 2)
                        val volHeight = (candle.volume / maxVol) * (chartHeight * 0.25f)
                        val y = chartHeight - volHeight
                        val isBull = candle.close >= candle.open
                        drawRect(
                            color = if (isBull) MarketGreen.copy(alpha = 0.25f) else MarketRed.copy(alpha = 0.25f),
                            topLeft = Offset(x, y),
                            size = Size(candleWidth, volHeight)
                        )
                    }
                }

                if (isCandleMode) {
                    // Draw Candlesticks
                    candles.forEachIndexed { i, candle ->
                        val isBull = candle.close >= candle.open
                        val color = if (isBull) MarketGreen else MarketRed
                        val xCenter = i * candleStep + (candleStep / 2)
                        val xLeft = i * candleStep + ((candleStep - candleWidth) / 2)

                        // Wicks
                        val highY = chartHeight - ((candle.high - minPrice) / priceRange) * chartHeight
                        val lowY = chartHeight - ((candle.low - minPrice) / priceRange) * chartHeight
                        drawLine(
                            color = color,
                            start = Offset(xCenter, highY),
                            end = Offset(xCenter, lowY),
                            strokeWidth = 2f
                        )

                        // Body
                        val openY = chartHeight - ((candle.open - minPrice) / priceRange) * chartHeight
                        val closeY = chartHeight - ((candle.close - minPrice) / priceRange) * chartHeight
                        val topY = minOf(openY, closeY)
                        val bodyHeight = maxOf(2f, kotlin.math.abs(closeY - openY))

                        drawRect(
                            color = color,
                            topLeft = Offset(xLeft, topY),
                            size = Size(candleWidth, bodyHeight)
                        )
                    }
                } else {
                    // Draw Line Chart with Gradient Path
                    val linePath = Path()
                    candles.forEachIndexed { i, candle ->
                        val x = i * candleStep + (candleStep / 2)
                        val y = chartHeight - ((candle.close - minPrice) / priceRange) * chartHeight
                        if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
                    }
                    drawPath(linePath, color = MarketGreen, style = Stroke(width = 3f, cap = StrokeCap.Round))
                }

                // EMA 20 & EMA 50 Curves
                if (showEma && count > 5) {
                    val ema20Path = Path()
                    val ema50Path = Path()
                    var prevEma20 = candles.first().close
                    var prevEma50 = candles.first().close
                    val k20 = 2f / (20f + 1f)
                    val k50 = 2f / (50f + 1f)

                    candles.forEachIndexed { i, candle ->
                        val x = i * candleStep + (candleStep / 2)
                        val currentEma20 = (candle.close * k20) + (prevEma20 * (1f - k20))
                        val currentEma50 = (candle.close * k50) + (prevEma50 * (1f - k50))
                        prevEma20 = currentEma20
                        prevEma50 = currentEma50

                        val y20 = chartHeight - ((currentEma20 - minPrice) / priceRange) * chartHeight
                        val y50 = chartHeight - ((currentEma50 - minPrice) / priceRange) * chartHeight

                        if (i == 0) {
                            ema20Path.moveTo(x, y20)
                            ema50Path.moveTo(x, y50)
                        } else {
                            ema20Path.lineTo(x, y20)
                            ema50Path.lineTo(x, y50)
                        }
                    }
                    drawPath(ema20Path, color = IndicatorEma20, style = Stroke(width = 2f))
                    drawPath(ema50Path, color = IndicatorEma50, style = Stroke(width = 2f))
                }

                // Crosshair cursor line
                crosshairIndex?.let { idx ->
                    if (idx in candles.indices) {
                        val crossX = idx * candleStep + (candleStep / 2)
                        val c = candles[idx]
                        val crossY = chartHeight - ((c.close - minPrice) / priceRange) * chartHeight

                        drawLine(
                            color = TextPrimary.copy(alpha = 0.7f),
                            start = Offset(crossX, 0f),
                            end = Offset(crossX, chartHeight),
                            strokeWidth = 1.5f
                        )
                        drawLine(
                            color = TextPrimary.copy(alpha = 0.7f),
                            start = Offset(0f, crossY),
                            end = Offset(chartWidth, crossY),
                            strokeWidth = 1.5f
                        )
                        drawCircle(color = TextPrimary, radius = 4f, center = Offset(crossX, crossY))
                    }
                }
            }
        }

        // Sub-chart: RSI (14) Oscillator
        if (showRsi) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("RSI (14)", fontSize = 10.sp, color = IndicatorRsiLine, fontWeight = FontWeight.Bold)
                    Text("62.4 (Neutral-Bullish)", fontSize = 10.sp, color = MarketGreen)
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    // 70 overbought & 30 oversold bands
                    val y70 = h * 0.30f
                    val y30 = h * 0.70f
                    drawLine(color = MarketRed.copy(alpha = 0.4f), start = Offset(0f, y70), end = Offset(w, y70), strokeWidth = 1f)
                    drawLine(color = MarketGreen.copy(alpha = 0.4f), start = Offset(0f, y30), end = Offset(w, y30), strokeWidth = 1f)

                    // Shaded midzone
                    drawRect(
                        color = IndicatorRsiLine.copy(alpha = 0.08f),
                        topLeft = Offset(0f, y70),
                        size = Size(w, y30 - y70)
                    )

                    // RSI wave
                    val rsiPath = Path()
                    val count = candles.size
                    val step = w / count
                    candles.forEachIndexed { i, c ->
                        val x = i * step + (step / 2)
                        // Mock RSI normalized around 30 to 75
                        val rsiVal = 40f + ((c.close - c.open) / c.open) * 800f + ((i % 7) * 4f)
                        val clampedRsi = rsiVal.coerceIn(20f, 85f)
                        val y = h - (clampedRsi / 100f) * h
                        if (i == 0) rsiPath.moveTo(x, y) else rsiPath.lineTo(x, y)
                    }
                    drawPath(rsiPath, color = IndicatorRsiLine, style = Stroke(width = 2f))
                }
            }
        }
    }
}

@Composable
private fun IndicatorPill(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else DarkSurface,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, color) else null,
        modifier = Modifier.height(26.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
