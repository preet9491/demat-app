package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TradingRepository
import com.example.model.OrderType
import com.example.model.ProductType
import com.example.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ApexTrade", appName)
    }

    @Test
    fun `test trading repository order placement and custom pnl`() {
        val repo = TradingRepository()
        assertEquals(4, repo.positions.value.size)

        // Place an order
        val success = repo.placeOrder(
            symbol = "INFY",
            name = "Infosys Ltd",
            transactionType = TransactionType.BUY,
            orderType = OrderType.MARKET,
            productType = ProductType.MIS,
            quantity = 50,
            price = 1890.0
        )
        assertTrue(success)
        assertEquals(5, repo.positions.value.size)

        // Test Custom P&L Override ("jo hame dikhana he wahi dikhe")
        repo.setCustomPortfolioPnl(145000.0)
        val totalPnl = repo.positions.value.sumOf { it.totalPnl }
        assertTrue(totalPnl > 140000.0)
    }
}
