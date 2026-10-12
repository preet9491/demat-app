package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.model.BrokerTheme

private fun getBrokerColorScheme(brokerTheme: BrokerTheme = BrokerTheme.ZERODHA_KITE) = darkColorScheme(
    primary = when (brokerTheme) {
        BrokerTheme.ZERODHA_KITE -> KiteBlue
        BrokerTheme.GROWW_CLEAN -> GrowwTeal
        BrokerTheme.UPSTOX_PRO -> UpstoxIndigo
    },
    secondary = when (brokerTheme) {
        BrokerTheme.ZERODHA_KITE -> KiteOrange
        BrokerTheme.GROWW_CLEAN -> GrowwTeal
        BrokerTheme.UPSTOX_PRO -> UpstoxCyan
    },
    tertiary = MarketGreen,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceElevated,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = DarkBorder
)

@Composable
fun MyApplicationTheme(
    brokerTheme: BrokerTheme = BrokerTheme.ZERODHA_KITE,
    content: @Composable () -> Unit
) {
    val colorScheme = getBrokerColorScheme(brokerTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
