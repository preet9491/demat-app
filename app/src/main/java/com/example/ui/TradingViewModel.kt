package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TradingRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    WATCHLIST("Watchlist"),
    ORDERS("Orders"),
    PORTFOLIO("Portfolio (P&L)"),
    OPTIONS("F&O Strategies"),
    NEWS("News"),
    DEMAT("Demat")
}

class TradingViewModel(
    private val repository: TradingRepository = TradingRepository()
) : ViewModel() {

    val stocks = repository.stocks
    val indices = repository.indices
    val positions = repository.positions
    val holdings = repository.holdings
    val orders = repository.orders
    val optionChain = repository.optionChain
    val strategies = repository.strategies
    val news = repository.news
    val dematProfile = repository.dematProfile
    val brokerTheme = repository.brokerTheme
    val isLiveTicking = repository.isLiveTicking

    private val _selectedTab = MutableStateFlow(AppNavTab.WATCHLIST)
    val selectedTab: StateFlow<AppNavTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _watchlistTab = MutableStateFlow(1) // Watchlist 1 to 5 (Zerodha style)
    val watchlistTab: StateFlow<Int> = _watchlistTab.asStateFlow()

    private val _selectedStock = MutableStateFlow<Stock?>(null)
    val selectedStock: StateFlow<Stock?> = _selectedStock.asStateFlow()

    private val _tradingStock = MutableStateFlow<Stock?>(null)
    val tradingStock: StateFlow<Stock?> = _tradingStock.asStateFlow()

    private val _tradeTransactionType = MutableStateFlow(TransactionType.BUY)
    val tradeTransactionType: StateFlow<TransactionType> = _tradeTransactionType.asStateFlow()

    private val _selectedStrategy = MutableStateFlow<OptionStrategy?>(null)
    val selectedStrategy: StateFlow<OptionStrategy?> = _selectedStrategy.asStateFlow()

    private val _showPnlCustomizer = MutableStateFlow(false)
    val showPnlCustomizer: StateFlow<Boolean> = _showPnlCustomizer.asStateFlow()

    private val _showCustomPositionDialog = MutableStateFlow(false)
    val showCustomPositionDialog: StateFlow<Boolean> = _showCustomPositionDialog.asStateFlow()

    private val _showAddFundsDialog = MutableStateFlow(false)
    val showAddFundsDialog: StateFlow<Boolean> = _showAddFundsDialog.asStateFlow()

    private val _showPnlShareCard = MutableStateFlow(false)
    val showPnlShareCard: StateFlow<Boolean> = _showPnlShareCard.asStateFlow()

    // Calculated Portfolio Metrics
    val totalPositionsPnl: StateFlow<Double> = positions.map { list ->
        list.sumOf { it.totalPnl }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalHoldingsInvested: StateFlow<Double> = holdings.map { list ->
        list.sumOf { it.investedValue }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalHoldingsCurrent: StateFlow<Double> = holdings.map { list ->
        list.sumOf { it.currentValue }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalHoldingsPnl: StateFlow<Double> = holdings.map { list ->
        list.sumOf { it.totalPnl }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun selectTab(tab: AppNavTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setWatchlistTab(tab: Int) {
        _watchlistTab.value = tab
    }

    fun setBrokerTheme(theme: BrokerTheme) {
        repository.setBrokerTheme(theme)
    }

    fun toggleLiveTicking() {
        repository.toggleLiveTicking(!isLiveTicking.value)
    }

    fun openStockDetail(stock: Stock) {
        _selectedStock.value = stock
    }

    fun closeStockDetail() {
        _selectedStock.value = null
    }

    fun openTradeDialog(stock: Stock, transactionType: TransactionType = TransactionType.BUY) {
        _tradingStock.value = stock
        _tradeTransactionType.value = transactionType
    }

    fun closeTradeDialog() {
        _tradingStock.value = null
    }

    fun openStrategyDetail(strategy: OptionStrategy) {
        _selectedStrategy.value = strategy
    }

    fun closeStrategyDetail() {
        _selectedStrategy.value = null
    }

    fun setShowPnlCustomizer(show: Boolean) {
        _showPnlCustomizer.value = show
    }

    fun setShowCustomPositionDialog(show: Boolean) {
        _showCustomPositionDialog.value = show
    }

    fun setShowAddFundsDialog(show: Boolean) {
        _showAddFundsDialog.value = show
    }

    fun setShowPnlShareCard(show: Boolean) {
        _showPnlShareCard.value = show
    }

    fun executeTrade(
        symbol: String,
        name: String,
        transactionType: TransactionType,
        orderType: OrderType,
        productType: ProductType,
        quantity: Int,
        price: Double,
        triggerPrice: Double = 0.0
    ): Boolean {
        return repository.placeOrder(
            symbol = symbol,
            name = name,
            transactionType = transactionType,
            orderType = orderType,
            productType = productType,
            quantity = quantity,
            price = price,
            triggerPrice = triggerPrice
        )
    }

    fun executeOptionStrategy(strategy: OptionStrategy) {
        repository.executeOptionStrategy(strategy)
    }

    fun applyCustomPnlPreset(preset: String) {
        repository.applyCustomPnlPreset(preset)
    }

    fun setCustomPortfolioPnl(targetPnl: Double) {
        repository.setCustomPortfolioPnl(targetPnl)
    }

    fun updateSinglePosition(
        positionId: String,
        newLtp: Double,
        newPnl: Double,
        newQty: Int
    ) {
        repository.updateSinglePosition(positionId, newLtp, newPnl, newQty)
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
        repository.addCustomPosition(symbol, name, product, quantity, buyPrice, ltp, customPnl)
    }

    fun addFunds(amount: Double) {
        repository.addFunds(amount)
    }

    fun withdrawFunds(amount: Double) {
        repository.withdrawFunds(amount)
    }
}
