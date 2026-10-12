package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.TransactionType
import com.example.ui.AppNavTab
import com.example.ui.TradingViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: TradingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val brokerTheme by viewModel.brokerTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(brokerTheme = brokerTheme) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: TradingViewModel) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val brokerTheme by viewModel.brokerTheme.collectAsStateWithLifecycle()
    val isLiveTicking by viewModel.isLiveTicking.collectAsStateWithLifecycle()

    val stocks by viewModel.stocks.collectAsStateWithLifecycle()
    val indices by viewModel.indices.collectAsStateWithLifecycle()
    val positions by viewModel.positions.collectAsStateWithLifecycle()
    val holdings by viewModel.holdings.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val optionChain by viewModel.optionChain.collectAsStateWithLifecycle()
    val strategies by viewModel.strategies.collectAsStateWithLifecycle()
    val news by viewModel.news.collectAsStateWithLifecycle()
    val dematProfile by viewModel.dematProfile.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val watchlistTab by viewModel.watchlistTab.collectAsStateWithLifecycle()

    val selectedStock by viewModel.selectedStock.collectAsStateWithLifecycle()
    val tradingStock by viewModel.tradingStock.collectAsStateWithLifecycle()
    val tradeTransactionType by viewModel.tradeTransactionType.collectAsStateWithLifecycle()

    val showPnlCustomizer by viewModel.showPnlCustomizer.collectAsStateWithLifecycle()
    val showCustomPositionDialog by viewModel.showCustomPositionDialog.collectAsStateWithLifecycle()
    val showAddFundsDialog by viewModel.showAddFundsDialog.collectAsStateWithLifecycle()
    val showPnlShareCard by viewModel.showPnlShareCard.collectAsStateWithLifecycle()

    val totalPositionsPnl by viewModel.totalPositionsPnl.collectAsStateWithLifecycle()
    val totalHoldingsInvested by viewModel.totalHoldingsInvested.collectAsStateWithLifecycle()
    val totalHoldingsCurrent by viewModel.totalHoldingsCurrent.collectAsStateWithLifecycle()
    val totalHoldingsPnl by viewModel.totalHoldingsPnl.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("main_bottom_nav_bar")
            ) {
                AppNavTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            when (tab) {
                                AppNavTab.WATCHLIST -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.ShowChart else Icons.Outlined.ShowChart,
                                    contentDescription = tab.title
                                )
                                AppNavTab.ORDERS -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                    contentDescription = tab.title
                                )
                                AppNavTab.PORTFOLIO -> BadgedBox(
                                    badge = {
                                        val isGreen = totalPositionsPnl >= 0
                                        Badge(
                                            containerColor = if (isGreen) MarketGreen else MarketRed
                                        ) {
                                            Text(
                                                text = if (isGreen) "▲" else "▼",
                                                color = Color.White,
                                                fontSize = 8.sp
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.Work else Icons.Outlined.WorkOutline,
                                        contentDescription = tab.title
                                    )
                                }
                                AppNavTab.OPTIONS -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.ElectricBolt else Icons.Outlined.ElectricBolt,
                                    contentDescription = tab.title
                                )
                                AppNavTab.NEWS -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.Newspaper else Icons.Outlined.Newspaper,
                                    contentDescription = tab.title
                                )
                                AppNavTab.DEMAT -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = tab.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = when (tab) {
                                    AppNavTab.WATCHLIST -> "Watchlist"
                                    AppNavTab.ORDERS -> "Orders"
                                    AppNavTab.PORTFOLIO -> "P&L"
                                    AppNavTab.OPTIONS -> "F&O"
                                    AppNavTab.NEWS -> "News"
                                    AppNavTab.DEMAT -> "Demat"
                                },
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = DarkSurfaceElevated,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppNavTab.WATCHLIST -> WatchlistScreen(
                    indices = indices,
                    stocks = stocks,
                    brokerTheme = brokerTheme,
                    searchQuery = searchQuery,
                    watchlistTab = watchlistTab,
                    onSearchChange = viewModel::setSearchQuery,
                    onSelectWatchlistTab = viewModel::setWatchlistTab,
                    onSelectBrokerTheme = viewModel::setBrokerTheme,
                    onStockClick = viewModel::openStockDetail
                )
                AppNavTab.ORDERS -> OrdersScreen(
                    orders = orders
                )
                AppNavTab.PORTFOLIO -> PortfolioScreen(
                    positions = positions,
                    holdings = holdings,
                    totalPositionsPnl = totalPositionsPnl,
                    totalHoldingsInvested = totalHoldingsInvested,
                    totalHoldingsCurrent = totalHoldingsCurrent,
                    totalHoldingsPnl = totalHoldingsPnl,
                    brokerTheme = brokerTheme,
                    isLiveTicking = isLiveTicking,
                    onToggleLiveTicking = viewModel::toggleLiveTicking,
                    onOpenPnlCustomizer = { viewModel.setShowPnlCustomizer(true) },
                    onOpenShareCard = { viewModel.setShowPnlShareCard(true) },
                    onSquareOffPosition = { pos ->
                        // Quick square-off
                        stocks.firstOrNull { it.symbol == pos.symbol }?.let { st ->
                            viewModel.openTradeDialog(st, TransactionType.SELL)
                        } ?: run {
                            scope.launch {
                                snackbarHostState.showSnackbar("Position squared off successfully")
                            }
                        }
                    }
                )
                AppNavTab.OPTIONS -> OptionsHubScreen(
                    optionChain = optionChain,
                    strategies = strategies,
                    onSelectStrategy = viewModel::openStrategyDetail,
                    onExecuteStrategy = { strat ->
                        viewModel.executeOptionStrategy(strat)
                        scope.launch {
                            snackbarHostState.showSnackbar("Executed ${strat.name} with ${strat.legs.size} legs!")
                        }
                    },
                    onSelectStrikeForTrade = { contract, isCall ->
                        val symbol = "NIFTY ${contract.strike.toInt()} ${if (isCall) "CE" else "PE"}"
                        val price = if (isCall) contract.ceLtp else contract.peLtp
                        val dummyStock = com.example.model.Stock(
                            symbol = symbol,
                            name = "NIFTY 24 OCT ${contract.strike.toInt()} ${if (isCall) "CALL" else "PUT"}",
                            ltp = price,
                            change = 12.0,
                            changePercent = 4.5,
                            open = price * 0.95,
                            high = price * 1.1,
                            low = price * 0.9,
                            close = price,
                            volume = 250000L,
                            vwap = price,
                            week52High = price * 2,
                            week52Low = price * 0.5,
                            sector = "Index Options"
                        )
                        viewModel.openTradeDialog(dummyStock, TransactionType.BUY)
                    }
                )
                AppNavTab.NEWS -> NewsScreen(
                    news = news
                )
                AppNavTab.DEMAT -> DematScreen(
                    profile = dematProfile,
                    brokerTheme = brokerTheme,
                    onSelectBrokerTheme = viewModel::setBrokerTheme,
                    onOpenAddFunds = { viewModel.setShowAddFundsDialog(true) }
                )
            }
        }
    }

    // Modal Bottom Sheets & Dialogs

    // 1. Stock Detail Sheet (with Interactive Candlestick Chart & Depth)
    selectedStock?.let { stock ->
        StockDetailSheet(
            stock = stock,
            onDismiss = viewModel::closeStockDetail,
            onTradeClick = { txnType ->
                viewModel.closeStockDetail()
                viewModel.openTradeDialog(stock, txnType)
            }
        )
    }

    // 2. Trade Execution Modal Sheet
    tradingStock?.let { stock ->
        TradeOrderSheet(
            stock = stock,
            initialType = tradeTransactionType,
            availableMargin = dematProfile.availableMargin,
            onDismiss = viewModel::closeTradeDialog,
            onExecuteOrder = { txnType, orderType, productType, qty, price, triggerPrice ->
                val success = viewModel.executeTrade(
                    symbol = stock.symbol,
                    name = stock.name,
                    transactionType = txnType,
                    orderType = orderType,
                    productType = productType,
                    quantity = qty,
                    price = price,
                    triggerPrice = triggerPrice
                )
                viewModel.closeTradeDialog()
                scope.launch {
                    if (success) {
                        snackbarHostState.showSnackbar("Order Executed: ${txnType.name} $qty ${stock.symbol} @ ₹$price")
                    } else {
                        snackbarHostState.showSnackbar("Insufficient margin to place order!")
                    }
                }
            }
        )
    }

    // 3. P&L Customizer / Studio Sheet ("jo hame dikhana he wahi dikhe")
    if (showPnlCustomizer) {
        PnlCustomizerSheet(
            currentTotalPnl = totalPositionsPnl,
            positions = positions,
            isLiveTicking = isLiveTicking,
            onToggleLiveTicking = viewModel::toggleLiveTicking,
            onApplyPreset = { preset ->
                viewModel.applyCustomPnlPreset(preset)
                scope.launch {
                    snackbarHostState.showSnackbar("Applied Preset to Portfolio!")
                }
            },
            onApplyTargetPnl = { target ->
                viewModel.setCustomPortfolioPnl(target)
                scope.launch {
                    snackbarHostState.showSnackbar("Updated Dashboard P&L to ₹${String.format("%,.0f", target)}")
                }
            },
            onUpdatePosition = { id, ltp, pnl, qty ->
                viewModel.updateSinglePosition(id, ltp, pnl, qty)
                scope.launch {
                    snackbarHostState.showSnackbar("Updated position P&L!")
                }
            },
            onOpenAddCustomDialog = {
                viewModel.setShowCustomPositionDialog(true)
            },
            onDismiss = { viewModel.setShowPnlCustomizer(false) }
        )
    }

    // 4. Add Custom Position Dialog
    if (showCustomPositionDialog) {
        AddCustomPositionDialog(
            onDismiss = { viewModel.setShowCustomPositionDialog(false) },
            onAdd = { sym, name, prod, qty, buy, ltp, pnl ->
                viewModel.addCustomPosition(sym, name, prod, qty, buy, ltp, pnl)
                scope.launch {
                    snackbarHostState.showSnackbar("Added custom position $sym with ₹${String.format("%,.0f", pnl)} P&L!")
                }
            }
        )
    }

    // 5. P&L Share Card Showcase Dialog
    if (showPnlShareCard) {
        PnlShareCardDialog(
            totalPnl = totalPositionsPnl,
            brokerTheme = brokerTheme,
            clientId = dematProfile.clientId,
            onDismiss = { viewModel.setShowPnlShareCard(false) }
        )
    }

    // 6. Add Funds Dialog
    if (showAddFundsDialog) {
        AddFundsDialog(
            currentMargin = dematProfile.availableMargin,
            onDismiss = { viewModel.setShowAddFundsDialog(false) },
            onAddFunds = { amount ->
                viewModel.addFunds(amount)
                scope.launch {
                    snackbarHostState.showSnackbar("Deposited ₹${String.format("%,.0f", amount)} successfully into Demat Margin!")
                }
            }
        )
    }
}
