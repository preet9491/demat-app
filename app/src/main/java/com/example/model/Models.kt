package com.example.model

enum class BrokerTheme(val displayName: String, val tagline: String) {
    ZERODHA_KITE("Zerodha Kite", "Minimalist, High Precision Trading"),
    GROWW_CLEAN("Groww", "Simple, Fast & Clean Investing"),
    UPSTOX_PRO("Upstox Pro", "Pro Terminal & Advanced Derivatives")
}

data class CandleStick(
    val timestamp: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float
)

data class DepthItem(
    val orders: Int,
    val quantity: Int,
    val price: Double
)

data class DepthData(
    val bids: List<DepthItem>,
    val asks: List<DepthItem>,
    val totalBuyQty: Long,
    val totalSellQty: Long
)

data class Stock(
    val symbol: String,
    val name: String,
    val exchange: String = "NSE",
    val ltp: Double,
    val change: Double,
    val changePercent: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val vwap: Double,
    val week52High: Double,
    val week52Low: Double,
    val sector: String,
    val candles1D: List<CandleStick> = emptyList(),
    val depth: DepthData? = null
)

enum class OrderType {
    MARKET, LIMIT, SL, SL_M
}

enum class TransactionType {
    BUY, SELL
}

enum class ProductType(val label: String, val description: String) {
    MIS("Intraday (MIS)", "5x Margin, Auto Square-off at 3:20 PM"),
    CNC("Longterm (CNC)", "Full Cash, Delivery to Demat"),
    NRML("Overnight (NRML)", "Carry forward F&O contracts")
}

enum class OrderStatus {
    EXECUTED, OPEN, CANCELLED, TRIGGER_PENDING
}

data class Order(
    val id: String,
    val symbol: String,
    val stockName: String,
    val transactionType: TransactionType,
    val orderType: OrderType,
    val productType: ProductType,
    val quantity: Int,
    val price: Double,
    val triggerPrice: Double = 0.0,
    val status: OrderStatus,
    val timestamp: String,
    val executedPrice: Double = price
)

data class Position(
    val id: String,
    val symbol: String,
    val name: String,
    val product: ProductType,
    val quantity: Int,
    val buyPrice: Double,
    var ltp: Double,
    var dayPnl: Double,
    var totalPnl: Double,
    val isCustomPnlOverride: Boolean = false
) {
    val pnlPercent: Double
        get() = if (buyPrice > 0) ((ltp - buyPrice) / buyPrice) * 100.0 else 0.0
}

data class Holding(
    val symbol: String,
    val name: String,
    val quantity: Int,
    val avgPrice: Double,
    val ltp: Double,
    val investedValue: Double = quantity * avgPrice,
    val currentValue: Double = quantity * ltp,
    val totalPnl: Double = (quantity * ltp) - (quantity * avgPrice),
    val dayPnl: Double = ((ltp * 0.008) * quantity)
)

data class OptionContract(
    val strike: Double,
    val ceLtp: Double,
    val ceChange: Double,
    val ceOi: Int,
    val ceIv: Double,
    val ceDelta: Double,
    val ceTheta: Double,
    val peLtp: Double,
    val peChange: Double,
    val peOi: Int,
    val peIv: Double,
    val peDelta: Double,
    val peTheta: Double
)

enum class StrategyType {
    BULL_CALL_SPREAD,
    BEAR_PUT_SPREAD,
    LONG_STRADDLE,
    SHORT_STRADDLE,
    IRON_CONDOR
}

data class OptionLeg(
    val action: TransactionType,
    val strike: Double,
    val optionType: String, // "CE" or "PE"
    val premium: Double,
    val lots: Int = 1,
    val lotSize: Int = 25
)

data class OptionStrategy(
    val id: String,
    val type: StrategyType,
    val name: String,
    val underlying: String,
    val underlyingPrice: Double,
    val outlook: String,
    val legs: List<OptionLeg>,
    val maxProfit: Double,
    val maxLoss: Double,
    val breakevens: List<Double>,
    val riskReward: String,
    val marginRequired: Double,
    val popPercent: Int // Probability of Profit
)

enum class MarketSentiment {
    BULLISH, BEARISH, NEUTRAL
}

enum class NewsCategory {
    MARKETS, EARNINGS, IPO, ECONOMY
}

data class MarketNews(
    val id: String,
    val title: String,
    val summary: String,
    val source: String,
    val timeAgo: String,
    val sentiment: MarketSentiment,
    val category: NewsCategory,
    val relatedSymbols: List<String>
)

data class MarketIndex(
    val symbol: String,
    val name: String,
    val value: Double,
    val change: Double,
    val changePercent: Double
)

data class DematProfile(
    val clientName: String = "PREET S. GILL",
    val clientId: String = "APX99281",
    val dpId: String = "1208160002938172",
    val depository: String = "CDSL",
    val panMasked: String = "ABCDE****F",
    val linkedBank: String = "HDFC Bank (**** 4910)",
    val availableMargin: Double = 250000.0,
    val usedMargin: Double = 42850.0,
    val collateral: Double = 115000.0,
    val payinToday: Double = 0.0
)
