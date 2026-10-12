package com.example.data

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class TradingRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private val _stocks = MutableStateFlow<List<Stock>>(MarketDataProvider.getInitialStocks())
    val stocks: StateFlow<List<Stock>> = _stocks.asStateFlow()

    private val _indices = MutableStateFlow<List<MarketIndex>>(MarketDataProvider.getInitialIndices())
    val indices: StateFlow<List<MarketIndex>> = _indices.asStateFlow()

    private val _positions = MutableStateFlow<List<Position>>(MarketDataProvider.getInitialPositions())
    val positions: StateFlow<List<Position>> = _positions.asStateFlow()

    private val _holdings = MutableStateFlow<List<Holding>>(MarketDataProvider.getInitialHoldings())
    val holdings: StateFlow<List<Holding>> = _holdings.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(MarketDataProvider.getInitialOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _optionChain = MutableStateFlow<List<OptionContract>>(MarketDataProvider.getNiftyOptionChain())
    val optionChain: StateFlow<List<OptionContract>> = _optionChain.asStateFlow()

    private val _strategies = MutableStateFlow<List<OptionStrategy>>(MarketDataProvider.getOptionStrategies())
    val strategies: StateFlow<List<OptionStrategy>> = _strategies.asStateFlow()

    private val _news = MutableStateFlow<List<MarketNews>>(MarketDataProvider.getMarketNews())
    val news: StateFlow<List<MarketNews>> = _news.asStateFlow()

    private val _dematProfile = MutableStateFlow(DematProfile())
    val dematProfile: StateFlow<DematProfile> = _dematProfile.asStateFlow()

    private val _brokerTheme = MutableStateFlow(BrokerTheme.ZERODHA_KITE)
    val brokerTheme: StateFlow<BrokerTheme> = _brokerTheme.asStateFlow()

    private val _isLiveTicking = MutableStateFlow(true)
    val isLiveTicking: StateFlow<Boolean> = _isLiveTicking.asStateFlow()

    private var tickingJob: Job? = null

    init {
        startMarketTickSimulation()
    }

    fun setBrokerTheme(theme: BrokerTheme) {
        _brokerTheme.value = theme
    }

    fun toggleLiveTicking(enabled: Boolean) {
        _isLiveTicking.value = enabled
        if (enabled) {
            startMarketTickSimulation()
        } else {
            tickingJob?.cancel()
            tickingJob = null
        }
    }

    private fun startMarketTickSimulation() {
        tickingJob?.cancel()
        tickingJob = scope.launch {
            while (isActive) {
                delay(1800)
                if (!_isLiveTicking.value) continue

                // Jiggle stock prices slightly
                _stocks.value = _stocks.value.map { stock ->
                    val deltaRatio = (Random.nextDouble() - 0.49) * 0.003
                    val newLtp = maxOf(1.0, stock.ltp * (1.0 + deltaRatio))
                    val roundedLtp = String.format("%.2f", newLtp).toDouble()
                    val newChange = roundedLtp - stock.close
                    val newChangePct = (newChange / stock.close) * 100.0
                    stock.copy(
                        ltp = roundedLtp,
                        change = String.format("%.2f", newChange).toDouble(),
                        changePercent = String.format("%.2f", newChangePct).toDouble(),
                        high = maxOf(stock.high, roundedLtp),
                        low = minOf(stock.low, roundedLtp)
                    )
                }

                // Jiggle indices
                _indices.value = _indices.value.map { idx ->
                    val delta = (Random.nextDouble() - 0.48) * (idx.value * 0.0004)
                    val newVal = idx.value + delta
                    idx.copy(
                        value = String.format("%.2f", newVal).toDouble(),
                        change = String.format("%.2f", idx.change + delta).toDouble(),
                        changePercent = String.format("%.2f", ((idx.change + delta) / idx.value) * 100).toDouble()
                    )
                }

                // Update non-custom overridden positions with live ticks
                _positions.value = _positions.value.map { pos ->
                    if (!pos.isCustomPnlOverride) {
                        val tickDelta = (Random.nextDouble() - 0.47) * 0.6
                        val newLtp = maxOf(1.0, pos.ltp + tickDelta)
                        val pnl = (newLtp - pos.buyPrice) * pos.quantity
                        pos.copy(
                            ltp = String.format("%.2f", newLtp).toDouble(),
                            dayPnl = String.format("%.2f", pnl).toDouble(),
                            totalPnl = String.format("%.2f", pnl).toDouble()
                        )
                    } else {
                        // Subtle tick for custom overridden positions to look alive while maintaining target P&L
                        val microTick = (Random.nextDouble() - 0.5) * 4.0
                        pos.copy(
                            dayPnl = String.format("%.2f", pos.dayPnl + microTick).toDouble(),
                            totalPnl = String.format("%.2f", pos.totalPnl + microTick).toDouble()
                        )
                    }
                }
            }
        }
    }

    // --- Trading Logic ---

    fun placeOrder(
        symbol: String,
        name: String,
        transactionType: TransactionType,
        orderType: OrderType,
        productType: ProductType,
        quantity: Int,
        price: Double,
        triggerPrice: Double = 0.0
    ): Boolean {
        val totalCost = if (productType == ProductType.MIS) (price * quantity) / 5.0 else (price * quantity)
        val profile = _dematProfile.value

        if (transactionType == TransactionType.BUY && profile.availableMargin < totalCost) {
            // Insufficient margin
            return false
        }

        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val orderId = "ORD_${System.currentTimeMillis() % 100000}"

        val newOrder = Order(
            id = orderId,
            symbol = symbol,
            stockName = name,
            transactionType = transactionType,
            orderType = orderType,
            productType = productType,
            quantity = quantity,
            price = price,
            triggerPrice = triggerPrice,
            status = OrderStatus.EXECUTED,
            timestamp = currentTime,
            executedPrice = price
        )

        _orders.value = listOf(newOrder) + _orders.value

        if (transactionType == TransactionType.BUY) {
            _dematProfile.value = profile.copy(
                availableMargin = maxOf(0.0, profile.availableMargin - totalCost),
                usedMargin = profile.usedMargin + totalCost
            )

            // Update or add position
            val currentPositions = _positions.value.toMutableList()
            val existingIndex = currentPositions.indexOfFirst { it.symbol == symbol && it.product == productType }

            if (existingIndex >= 0) {
                val existing = currentPositions[existingIndex]
                val newQty = existing.quantity + quantity
                val newAvgBuy = ((existing.buyPrice * existing.quantity) + (price * quantity)) / newQty
                val currentLtp = price
                val pnl = (currentLtp - newAvgBuy) * newQty
                currentPositions[existingIndex] = existing.copy(
                    quantity = newQty,
                    buyPrice = String.format("%.2f", newAvgBuy).toDouble(),
                    ltp = currentLtp,
                    dayPnl = pnl,
                    totalPnl = pnl
                )
            } else {
                currentPositions.add(
                    Position(
                        id = "POS_${System.currentTimeMillis() % 100000}",
                        symbol = symbol,
                        name = name,
                        product = productType,
                        quantity = quantity,
                        buyPrice = price,
                        ltp = price,
                        dayPnl = 0.0,
                        totalPnl = 0.0
                    )
                )
            }
            _positions.value = currentPositions
        } else {
            // Sell order - Square off or sell holding
            val currentPositions = _positions.value.toMutableList()
            val existingIndex = currentPositions.indexOfFirst { it.symbol == symbol }
            if (existingIndex >= 0) {
                val pos = currentPositions[existingIndex]
                if (pos.quantity <= quantity) {
                    currentPositions.removeAt(existingIndex)
                } else {
                    val remaining = pos.quantity - quantity
                    currentPositions[existingIndex] = pos.copy(quantity = remaining)
                }
                _positions.value = currentPositions
            }
        }

        return true
    }

    fun executeOptionStrategy(strategy: OptionStrategy) {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        strategy.legs.forEach { leg ->
            val symbol = "${strategy.underlying} ${leg.strike.toInt()} ${leg.optionType}"
            val qty = leg.lots * leg.lotSize
            val order = Order(
                id = "STRAT_${System.currentTimeMillis() % 100000}_${leg.strike.toInt()}",
                symbol = symbol,
                stockName = "${strategy.name} Leg",
                transactionType = leg.action,
                orderType = OrderType.MARKET,
                productType = ProductType.MIS,
                quantity = qty,
                price = leg.premium,
                status = OrderStatus.EXECUTED,
                timestamp = currentTime,
                executedPrice = leg.premium
            )
            _orders.value = listOf(order) + _orders.value

            val currentPositions = _positions.value.toMutableList()
            val initialPnl = if (leg.action == TransactionType.BUY) 120.0 else -80.0
            currentPositions.add(
                Position(
                    id = "POS_STRAT_${System.currentTimeMillis() % 100000}_${leg.strike.toInt()}",
                    symbol = symbol,
                    name = "${strategy.name} [${leg.action.name}]",
                    product = ProductType.MIS,
                    quantity = qty,
                    buyPrice = leg.premium,
                    ltp = leg.premium * 1.05,
                    dayPnl = initialPnl,
                    totalPnl = initialPnl
                )
            )
            _positions.value = currentPositions
        }
    }

    // --- Custom P&L Dashboard Control ("jo hame dikhana he wahi dikhe") ---

    fun applyCustomPnlPreset(presetName: String) {
        when (presetName) {
            "SUPER_GREEN" -> {
                // +₹1,45,280.00
                setCustomPortfolioPnl(145280.0)
            }
            "EXPIRY_JACKPOT" -> {
                // +₹4,85,600.00
                setCustomPortfolioPnl(485600.0)
            }
            "CONSISTENT_PRO" -> {
                // +₹42,500.00
                setCustomPortfolioPnl(42500.0)
            }
            "RECOVERY_DAY" -> {
                // +₹12,850.00
                setCustomPortfolioPnl(12850.0)
            }
            "RED_DEFENSIVE" -> {
                // -₹6,400.00
                setCustomPortfolioPnl(-6400.0)
            }
            "RESET_ORGANIC" -> {
                _positions.value = MarketDataProvider.getInitialPositions()
            }
        }
    }

    fun setCustomPortfolioPnl(targetTotalPnl: Double) {
        val currentList = _positions.value
        if (currentList.isEmpty()) {
            // Add a hero position if empty
            _positions.value = listOf(
                Position(
                    id = "POS_CUSTOM_HERO",
                    symbol = "NIFTY 24900 CE",
                    name = "NIFTY 24 OCT 24900 CALL",
                    product = ProductType.MIS,
                    quantity = 600,
                    buyPrice = 112.50,
                    ltp = 112.50 + (targetTotalPnl / 600.0),
                    dayPnl = targetTotalPnl,
                    totalPnl = targetTotalPnl,
                    isCustomPnlOverride = true
                )
            )
            return
        }

        // Distribute target P&L across existing positions realistically
        val count = currentList.size
        val weights = listOf(0.48, 0.32, 0.14, 0.06).take(count)
        val normalizedWeights = if (weights.size < count) {
            List(count) { 1.0 / count }
        } else {
            val sum = weights.sum()
            weights.map { it / sum }
        }

        _positions.value = currentList.mapIndexed { index, pos ->
            val share = targetTotalPnl * normalizedWeights[index]
            val ltpDelta = if (pos.quantity > 0) share / pos.quantity else 0.0
            val newLtp = maxOf(0.5, pos.buyPrice + ltpDelta)

            pos.copy(
                ltp = String.format("%.2f", newLtp).toDouble(),
                dayPnl = String.format("%.2f", share).toDouble(),
                totalPnl = String.format("%.2f", share).toDouble(),
                isCustomPnlOverride = true
            )
        }
    }

    fun updateSinglePosition(
        positionId: String,
        newLtp: Double,
        newPnl: Double,
        newQty: Int
    ) {
        _positions.value = _positions.value.map { pos ->
            if (pos.id == positionId) {
                pos.copy(
                    ltp = newLtp,
                    dayPnl = newPnl,
                    totalPnl = newPnl,
                    quantity = newQty,
                    isCustomPnlOverride = true
                )
            } else pos
        }
    }

    fun addCustomPosition(
        symbol: String,
        name: String,
        product: ProductType,
        quantity: Int,
        buyPrice: Double,
        ltp: Double,
        customPnl: Double
    ) {
        val newPos = Position(
            id = "POS_CUSTOM_${System.currentTimeMillis() % 100000}",
            symbol = symbol,
            name = name,
            product = product,
            quantity = quantity,
            buyPrice = buyPrice,
            ltp = ltp,
            dayPnl = customPnl,
            totalPnl = customPnl,
            isCustomPnlOverride = true
        )
        _positions.value = listOf(newPos) + _positions.value
    }

    fun addFunds(amount: Double) {
        val current = _dematProfile.value
        _dematProfile.value = current.copy(
            availableMargin = current.availableMargin + amount,
            payinToday = current.payinToday + amount
        )
    }

    fun withdrawFunds(amount: Double) {
        val current = _dematProfile.value
        if (current.availableMargin >= amount) {
            _dematProfile.value = current.copy(
                availableMargin = current.availableMargin - amount
            )
        }
    }
}
