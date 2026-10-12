package com.example.data

import com.example.model.*
import kotlin.random.Random

object MarketDataProvider {

    fun generateCandles(basePrice: Double, count: Int = 40): List<CandleStick> {
        val candles = mutableListOf<CandleStick>()
        var currentClose = basePrice.toFloat() * 0.96f
        val now = System.currentTimeMillis()
        val intervalMs = 15 * 60 * 1000L // 15 mins

        for (i in 0 until count) {
            val open = currentClose
            val change = (Random.nextFloat() - 0.48f) * (open * 0.012f)
            val close = open + change
            val high = maxOf(open, close) + Random.nextFloat() * (open * 0.008f)
            val low = minOf(open, close) - Random.nextFloat() * (open * 0.008f)
            val volume = 15000f + Random.nextFloat() * 180000f

            candles.add(
                CandleStick(
                    timestamp = now - ((count - i) * intervalMs),
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )
            currentClose = close
        }
        return candles
    }

    private fun generateDepth(ltp: Double): DepthData {
        val bids = mutableListOf<DepthItem>()
        val asks = mutableListOf<DepthItem>()
        var totalBuy = 0L
        var totalSell = 0L

        for (i in 1..5) {
            val bidPrice = ltp - (i * 0.15)
            val bidQty = 120 * i + (i * 75)
            val bidOrders = 3 + i * 2
            bids.add(DepthItem(bidOrders, bidQty, String.format("%.2f", bidPrice).toDouble()))
            totalBuy += bidQty

            val askPrice = ltp + (i * 0.15)
            val askQty = 140 * i + (i * 60)
            val askOrders = 2 + i * 2
            asks.add(DepthItem(askOrders, askQty, String.format("%.2f", askPrice).toDouble()))
            totalSell += askQty
        }

        return DepthData(bids, asks, totalBuy, totalSell)
    }

    fun getInitialIndices(): List<MarketIndex> {
        return listOf(
            MarketIndex("NIFTY 50", "NSE Benchmark", 24852.15, 142.30, 0.58),
            MarketIndex("SENSEX", "BSE Benchmark", 81382.40, 480.10, 0.59),
            MarketIndex("BANK NIFTY", "Nifty Banking", 53240.85, 312.45, 0.59),
            MarketIndex("FIN NIFTY", "Financial Services", 23910.20, 95.10, 0.40),
            MarketIndex("INDIA VIX", "Volatility Index", 12.84, -0.42, -3.17)
        )
    }

    fun getInitialStocks(): List<Stock> {
        val stockTemplates = listOf(
            Triple("RELIANCE", "Reliance Industries Ltd", Pair(2980.50, "Energy & Retail")),
            Triple("TCS", "Tata Consultancy Services", Pair(4190.25, "Information Tech")),
            Triple("HDFCBANK", "HDFC Bank Limited", Pair(1645.80, "Banking & Finance")),
            Triple("INFY", "Infosys Limited", Pair(1895.40, "Information Tech")),
            Triple("TATAMOTORS", "Tata Motors Ltd", Pair(972.60, "Automobile & EV")),
            Triple("ICICIBANK", "ICICI Bank Ltd", Pair(1234.15, "Banking & Finance")),
            Triple("ZOMATO", "Zomato Limited", Pair(268.40, "Internet & Consumer")),
            Triple("ITC", "ITC Limited", Pair(492.30, "FMCG")),
            Triple("SBIN", "State Bank of India", Pair(824.75, "Public Bank")),
            Triple("ADANIENT", "Adani Enterprises Ltd", Pair(3140.00, "Diversified")),
            Triple("BAJFINANCE", "Bajaj Finance Ltd", Pair(7310.50, "NBFC"),),
            Triple("BHARTIARTL", "Bharti Airtel Ltd", Pair(1598.20, "Telecom"))
        )

        return stockTemplates.map { (symbol, name, data) ->
            val ltp = data.first
            val sector = data.second
            val change = (Random.nextDouble() - 0.35) * (ltp * 0.02)
            val changePct = (change / (ltp - change)) * 100
            val open = ltp - change * 0.6
            val high = maxOf(open, ltp) + (ltp * 0.006)
            val low = minOf(open, ltp) - (ltp * 0.006)
            val close = ltp - change
            val volume = 1200000L + Random.nextLong(4500000L)

            Stock(
                symbol = symbol,
                name = name,
                exchange = "NSE",
                ltp = String.format("%.2f", ltp).toDouble(),
                change = String.format("%.2f", change).toDouble(),
                changePercent = String.format("%.2f", changePct).toDouble(),
                open = String.format("%.2f", open).toDouble(),
                high = String.format("%.2f", high).toDouble(),
                low = String.format("%.2f", low).toDouble(),
                close = String.format("%.2f", close).toDouble(),
                volume = volume,
                vwap = String.format("%.2f", (high + low + ltp) / 3).toDouble(),
                week52High = String.format("%.2f", ltp * 1.22).toDouble(),
                week52Low = String.format("%.2f", ltp * 0.78).toDouble(),
                sector = sector,
                candles1D = generateCandles(ltp, 45),
                depth = generateDepth(ltp)
            )
        }
    }

    fun getInitialPositions(): List<Position> {
        return listOf(
            Position(
                id = "POS_1",
                symbol = "NIFTY 24900 CE",
                name = "NIFTY 24 OCT 24900 CALL",
                product = ProductType.MIS,
                quantity = 150, // 6 lots
                buyPrice = 112.40,
                ltp = 158.80,
                dayPnl = 6960.0,
                totalPnl = 6960.0
            ),
            Position(
                id = "POS_2",
                symbol = "BANKNIFTY 53400 CE",
                name = "BANKNIFTY 24 OCT 53400 CALL",
                product = ProductType.MIS,
                quantity = 60, // 4 lots
                buyPrice = 245.00,
                ltp = 330.50,
                dayPnl = 5130.0,
                totalPnl = 5130.0
            ),
            Position(
                id = "POS_3",
                symbol = "TATAMOTORS",
                name = "Tata Motors Ltd",
                product = ProductType.MIS,
                quantity = 200,
                buyPrice = 960.50,
                ltp = 972.60,
                dayPnl = 2420.0,
                totalPnl = 2420.0
            ),
            Position(
                id = "POS_4",
                symbol = "RELIANCE",
                name = "Reliance Industries Ltd",
                product = ProductType.MIS,
                quantity = 50,
                buyPrice = 2965.00,
                ltp = 2980.50,
                dayPnl = 775.0,
                totalPnl = 775.0
            )
        )
    }

    fun getInitialHoldings(): List<Holding> {
        return listOf(
            Holding("HDFCBANK", "HDFC Bank Ltd", 150, 1510.00, 1645.80),
            Holding("TCS", "Tata Consultancy Services", 40, 3850.00, 4190.25),
            Holding("ITC", "ITC Limited", 500, 430.00, 492.30),
            Holding("ZOMATO", "Zomato Limited", 800, 165.00, 268.40),
            Holding("INFY", "Infosys Limited", 100, 1580.00, 1895.40)
        )
    }

    fun getInitialOrders(): List<Order> {
        return listOf(
            Order(
                id = "ORD_7891",
                symbol = "NIFTY 24900 CE",
                stockName = "NIFTY 24 OCT 24900 CALL",
                transactionType = TransactionType.BUY,
                orderType = OrderType.LIMIT,
                productType = ProductType.MIS,
                quantity = 150,
                price = 112.40,
                status = OrderStatus.EXECUTED,
                timestamp = "09:32 AM",
                executedPrice = 112.40
            ),
            Order(
                id = "ORD_7892",
                symbol = "BANKNIFTY 53400 CE",
                stockName = "BANKNIFTY 24 OCT 53400 CALL",
                transactionType = TransactionType.BUY,
                orderType = OrderType.MARKET,
                productType = ProductType.MIS,
                quantity = 60,
                price = 245.00,
                status = OrderStatus.EXECUTED,
                timestamp = "10:15 AM",
                executedPrice = 245.00
            ),
            Order(
                id = "ORD_7893",
                symbol = "RELIANCE",
                stockName = "Reliance Industries",
                transactionType = TransactionType.BUY,
                orderType = OrderType.LIMIT,
                productType = ProductType.MIS,
                quantity = 50,
                price = 2965.00,
                status = OrderStatus.EXECUTED,
                timestamp = "11:04 AM",
                executedPrice = 2965.00
            ),
            Order(
                id = "ORD_7894",
                symbol = "TATAMOTORS",
                stockName = "Tata Motors SL",
                transactionType = TransactionType.SELL,
                orderType = OrderType.SL,
                productType = ProductType.MIS,
                quantity = 200,
                price = 952.00,
                triggerPrice = 955.00,
                status = OrderStatus.OPEN,
                timestamp = "11:20 AM"
            )
        )
    }

    fun getNiftyOptionChain(atmStrike: Double = 24850.0): List<OptionContract> {
        val strikes = listOf(
            24650.0, 24700.0, 24750.0, 24800.0, 24850.0,
            24900.0, 24950.0, 25000.0, 25050.0, 25100.0
        )

        return strikes.map { strike ->
            val distance = (strike - atmStrike)
            // Call price formula approx
            val intrinsicCE = maxOf(0.0, atmStrike - strike)
            val timeValueCE = maxOf(18.0, 160.0 - (Math.abs(distance) * 0.45))
            val ceLtp = intrinsicCE + timeValueCE

            // Put price formula approx
            val intrinsicPE = maxOf(0.0, strike - atmStrike)
            val timeValuePE = maxOf(16.0, 155.0 - (Math.abs(distance) * 0.45))
            val peLtp = intrinsicPE + timeValuePE

            val ceDelta = (0.50 - (distance / 500.0)).coerceIn(0.05, 0.95)
            val peDelta = (-0.50 - (distance / 500.0)).coerceIn(-0.95, -0.05)

            OptionContract(
                strike = strike,
                ceLtp = String.format("%.2f", ceLtp).toDouble(),
                ceChange = String.format("%.2f", (Random.nextDouble() - 0.4) * 12).toDouble(),
                ceOi = (45000 + Random.nextInt(120000)),
                ceIv = 13.8 + (Random.nextDouble() * 1.5),
                ceDelta = String.format("%.2f", ceDelta).toDouble(),
                ceTheta = -12.4,
                peLtp = String.format("%.2f", peLtp).toDouble(),
                peChange = String.format("%.2f", (Random.nextDouble() - 0.45) * 10).toDouble(),
                peOi = (48000 + Random.nextInt(110000)),
                peIv = 14.1 + (Random.nextDouble() * 1.4),
                peDelta = String.format("%.2f", peDelta).toDouble(),
                peTheta = -11.8
            )
        }
    }

    fun getOptionStrategies(): List<OptionStrategy> {
        return listOf(
            OptionStrategy(
                id = "STRAT_1",
                type = StrategyType.BULL_CALL_SPREAD,
                name = "Bull Call Spread",
                underlying = "NIFTY 24850",
                underlyingPrice = 24852.0,
                outlook = "Moderately Bullish (Upside with Defined Risk)",
                legs = listOf(
                    OptionLeg(TransactionType.BUY, 24850.0, "CE", 132.0, 1, 25),
                    OptionLeg(TransactionType.SELL, 25000.0, "CE", 58.0, 1, 25)
                ),
                maxProfit = 1900.0, // (150 spread - 74 net premium paid) * 25
                maxLoss = 1850.0,  // 74 net premium * 25
                breakevens = listOf(24924.0),
                riskReward = "1 : 1.02",
                marginRequired = 28500.0,
                popPercent = 58
            ),
            OptionStrategy(
                id = "STRAT_2",
                type = StrategyType.BEAR_PUT_SPREAD,
                name = "Bear Put Spread",
                underlying = "NIFTY 24850",
                underlyingPrice = 24852.0,
                outlook = "Moderately Bearish (Downside with Defined Risk)",
                legs = listOf(
                    OptionLeg(TransactionType.BUY, 24850.0, "PE", 125.0, 1, 25),
                    OptionLeg(TransactionType.SELL, 24700.0, "PE", 52.0, 1, 25)
                ),
                maxProfit = 1925.0,
                maxLoss = 1825.0,
                breakevens = listOf(24777.0),
                riskReward = "1 : 1.05",
                marginRequired = 27800.0,
                popPercent = 54
            ),
            OptionStrategy(
                id = "STRAT_3",
                type = StrategyType.SHORT_STRADDLE,
                name = "Short Straddle (Expiry Theta Decay)",
                underlying = "NIFTY 24850",
                underlyingPrice = 24852.0,
                outlook = "Neutral / Range-Bound (Collect High Premium)",
                legs = listOf(
                    OptionLeg(TransactionType.SELL, 24850.0, "CE", 132.0, 1, 25),
                    OptionLeg(TransactionType.SELL, 24850.0, "PE", 125.0, 1, 25)
                ),
                maxProfit = 6425.0, // (132 + 125) * 25
                maxLoss = 99999.0, // Undefined
                breakevens = listOf(24593.0, 25107.0),
                riskReward = "1 : Dynamic",
                marginRequired = 142000.0,
                popPercent = 64
            ),
            OptionStrategy(
                id = "STRAT_4",
                type = StrategyType.IRON_CONDOR,
                name = "Iron Condor (4-Legged Range Play)",
                underlying = "NIFTY 24850",
                underlyingPrice = 24852.0,
                outlook = "Delta Neutral (Captures Low Volatility)",
                legs = listOf(
                    OptionLeg(TransactionType.BUY, 24650.0, "PE", 38.0, 1, 25),
                    OptionLeg(TransactionType.SELL, 24750.0, "PE", 74.0, 1, 25),
                    OptionLeg(TransactionType.SELL, 24950.0, "CE", 78.0, 1, 25),
                    OptionLeg(TransactionType.BUY, 25050.0, "CE", 42.0, 1, 25)
                ),
                maxProfit = 1800.0,
                maxLoss = 1700.0,
                breakevens = listOf(24678.0, 25022.0),
                riskReward = "1 : 1.06",
                marginRequired = 44500.0,
                popPercent = 68
            )
        )
    }

    fun getMarketNews(): List<MarketNews> {
        return listOf(
            MarketNews(
                id = "NEWS_1",
                title = "RBI MPC Meeting: Benchmark Repo Rate Kept Unchanged at 6.5%, Growth Outlook Bullish",
                summary = "Governor highlights resilient domestic consumption and robust manufacturing PMI. Banking & Auto indices lead market gains.",
                source = "Mint Markets",
                timeAgo = "12m ago",
                sentiment = MarketSentiment.BULLISH,
                category = NewsCategory.ECONOMY,
                relatedSymbols = listOf("HDFCBANK", "ICICIBANK", "TATAMOTORS")
            ),
            MarketNews(
                id = "NEWS_2",
                title = "Tata Motors EV Subsidiary Secures ₹2,500 Cr Order for Fleet Electrification",
                summary = "Commercial order for 8,500 electric commercial vehicles and battery architecture; stock surges 2.8% on heavy institutional volume.",
                source = "MoneyControl",
                timeAgo = "28m ago",
                sentiment = MarketSentiment.BULLISH,
                category = NewsCategory.MARKETS,
                relatedSymbols = listOf("TATAMOTORS")
            ),
            MarketNews(
                id = "NEWS_3",
                title = "Q2 Earnings Preview: IT Majors See Margin Expansion Amid AI & Cloud Spend",
                summary = "TCS and Infosys projected to report 7-9% YoY deal flow momentum as international enterprise generative AI adoption gathers pace.",
                source = "Economic Times",
                timeAgo = "1h ago",
                sentiment = MarketSentiment.BULLISH,
                category = NewsCategory.EARNINGS,
                relatedSymbols = listOf("TCS", "INFY")
            ),
            MarketNews(
                id = "NEWS_4",
                title = "Hyundai Motor India ₹27,870 Cr Mega IPO Opens with Strong Institutional Demand",
                summary = "India's largest-ever public issue subscribed 1.4x on Day 2 with substantial anchor allocations to leading domestic mutual funds.",
                source = "Bloomberg Quint",
                timeAgo = "2h ago",
                sentiment = MarketSentiment.NEUTRAL,
                category = NewsCategory.IPO,
                relatedSymbols = listOf("TATAMOTORS", "MARUTI")
            ),
            MarketNews(
                id = "NEWS_5",
                title = "FIIs Turn Net Buyers in Indian Equities with ₹1,840 Cr Inflow",
                summary = "Foreign portfolio investors reverse three consecutive weeks of selling; strong participation in Nifty heavyweight blue chips.",
                source = "CNBC TV18",
                timeAgo = "3h ago",
                sentiment = MarketSentiment.BULLISH,
                category = NewsCategory.MARKETS,
                relatedSymbols = listOf("RELIANCE", "HDFCBANK")
            )
        )
    }
}
