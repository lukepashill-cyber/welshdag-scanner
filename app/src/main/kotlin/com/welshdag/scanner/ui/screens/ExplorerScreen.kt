package com.welshdag.scanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.welshdag.scanner.network.BlockSummary
import com.welshdag.scanner.network.SearchResultItem
import com.welshdag.scanner.network.TransactionSummary
import com.welshdag.scanner.ui.viewmodel.ExplorerViewModel
import com.welshdag.scanner.util.formatWeiDecimal
import com.welshdag.scanner.util.groupDigits
import com.welshdag.scanner.util.relativeTime
import com.welshdag.scanner.util.shortenHash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerScreen(
    navController: NavController,
    viewModel: ExplorerViewModel = hiltViewModel()
) {
    val overview by viewModel.overview.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WelshDAG Explorer") },
                actions = {
                    IconButton(onClick = viewModel::refresh, enabled = !isLoading) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    if (it.isBlank()) viewModel.clearSearch()
                },
                label = { Text("Address, transaction or block") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            query = ""
                            viewModel.clearSearch()
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { viewModel.search(query) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            errorMessage?.let { message ->
                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(
                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                )
            }

            val results = searchResults
            when {
                isSearching -> CenteredSpinner("Searching…")

                results != null -> SearchResults(results) { hash ->
                    navController.navigate("address/$hash")
                }

                isLoading && overview == null -> CenteredSpinner("Loading chain data…")

                else -> OverviewContent(
                    overview = overview,
                    onAddressClick = { navController.navigate("address/$it") }
                )
            }
        }
    }
}

@Composable
private fun CenteredSpinner(label: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(label)
    }
}

@Composable
private fun SearchResults(
    results: List<SearchResultItem>,
    onAddressClick: (String) -> Unit
) {
    if (results.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No matches.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(results) { item ->
            val hash = item.addressHash ?: item.transactionHash ?: item.blockHash
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                    .clickable(enabled = item.addressHash != null) {
                        item.addressHash?.let(onAddressClick)
                    }
                    .padding(14.dp)
            ) {
                Text(
                    text = (item.type ?: "result").replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name ?: shortenHash(hash, head = 14, tail = 10),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun OverviewContent(
    overview: com.welshdag.scanner.data.ExplorerOverview?,
    onAddressClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        overview?.stats?.let { stats ->
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatTile("Blocks", groupDigits(stats.totalBlocks), Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatTile("Transactions", groupDigits(stats.totalTransactions), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatTile("Addresses", groupDigits(stats.totalAddresses), Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatTile(
                        label = "Avg block",
                        value = stats.averageBlockTimeMs
                            ?.let { "${(it / 1000).toInt()}s" } ?: "—",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (overview != null && overview.blocks.isNotEmpty()) {
            item { SectionHeader("Latest blocks") }
            items(overview.blocks) { block ->
                BlockRow(block, onAddressClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        if (overview != null && overview.transactions.isNotEmpty()) {
            item { SectionHeader("Latest transactions") }
            items(overview.transactions) { tx ->
                TransactionRow(tx, onAddressClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BlockRow(block: BlockSummary, onAddressClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#${groupDigits(block.height)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = relativeTime(block.timestamp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${block.transactionCount ?: 0} txns · ${block.size ?: 0} bytes",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
        block.miner?.hash?.let { miner ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "miner ${shortenHash(miner)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable { onAddressClick(miner) }
            )
        }
    }
}

@Composable
private fun TransactionRow(tx: TransactionSummary, onAddressClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = shortenHash(tx.hash),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = relativeTime(tx.timestamp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${formatWeiDecimal(tx.value)} BDAG",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(6.dp))

        tx.fromAddress?.hash?.let { from ->
            Text(
                text = "from ${shortenHash(from)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable { onAddressClick(from) }
            )
        }
        tx.toAddress?.hash?.let { to ->
            Text(
                text = "to ${shortenHash(to)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable { onAddressClick(to) }
            )
        }
    }
}
