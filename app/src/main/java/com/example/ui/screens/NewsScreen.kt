package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarketNews
import com.example.model.MarketSentiment
import com.example.model.NewsCategory
import com.example.ui.theme.*

@Composable
fun NewsScreen(
    news: List<MarketNews>
) {
    var selectedCategory by remember { mutableStateOf<NewsCategory?>(null) }

    val filteredNews = remember(news, selectedCategory) {
        if (selectedCategory == null) news
        else news.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Newspaper, contentDescription = null, tint = GrowwTeal)
                Text(
                    text = "Market News & Intelligence",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                text = "Curated Indian market headlines with AI sentiment",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All News", fontSize = 11.sp) }
                    )
                }
                items(NewsCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.name, fontSize = 11.sp) }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredNews, key = { it.id }) { item ->
                NewsItemCard(newsItem = item)
            }
        }
    }
}

@Composable
private fun NewsItemCard(newsItem: MarketNews) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth().testTag("news_item_${newsItem.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Source & Sentiment pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = newsItem.source,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KiteBlue
                    )
                    Text(
                        text = "• ${newsItem.timeAgo}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    color = when (newsItem.sentiment) {
                        MarketSentiment.BULLISH -> MarketGreen.copy(alpha = 0.15f)
                        MarketSentiment.BEARISH -> MarketRed.copy(alpha = 0.15f)
                        MarketSentiment.NEUTRAL -> TextSecondary.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (newsItem.sentiment) {
                                MarketSentiment.BULLISH -> Icons.Default.TrendingUp
                                MarketSentiment.BEARISH -> Icons.Default.TrendingDown
                                MarketSentiment.NEUTRAL -> Icons.Default.Newspaper
                            },
                            contentDescription = null,
                            tint = when (newsItem.sentiment) {
                                MarketSentiment.BULLISH -> MarketGreen
                                MarketSentiment.BEARISH -> MarketRed
                                MarketSentiment.NEUTRAL -> TextSecondary
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = newsItem.sentiment.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (newsItem.sentiment) {
                                MarketSentiment.BULLISH -> MarketGreen
                                MarketSentiment.BEARISH -> MarketRed
                                MarketSentiment.NEUTRAL -> TextSecondary
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = newsItem.summary,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Related Stocks Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                newsItem.relatedSymbols.forEach { sym ->
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = sym,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
