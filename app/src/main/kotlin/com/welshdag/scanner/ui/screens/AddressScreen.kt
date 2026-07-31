package com.welshdag.scanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.welshdag.scanner.network.AccountBalance
import com.welshdag.scanner.network.TransactionSummary
import com.welshdag.scanner.ui.viewmodel.AddressViewModel
import com.welshdag.scanner.util.formatWeiDecimal
import com.welshdag.scanner.util.relativeTime
import com.welshdag.scanner.util.shortenHash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    address: String,
    navController: NavController,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val detail by viewModel.detail.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val peerBalances by viewModel.peerBalances.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(address) { viewModel.load(address) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Address") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.load(address) }, enabled = !isLoading) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            if (isLoading && detail == null && peerBalances.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading address…")
                }
                return@Box
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = address,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                errorMessage?.let { message ->
                    item {
                        Text(
                            text = message,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                detail?.let { d ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "EXPLORER BALANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${formatWeiDecimal(d.coinBalance)} BDAG",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            d.balanceUpdatedAtBlock?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "as of block $it",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (d.isContract == true) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Contract",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                if (peerBalances.isNotEmpty()) {
                    item {
                        SectionLabel("Peer node balances")
                    }
                    items(peerBalances) { peer ->
                        PeerBalanceRow(peer)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }

                if (transactions.isNotEmpty()) {
                    item { SectionLabel("Transactions") }
                    items(transactions) { tx ->
                        AddressTransactionRow(tx, address)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else if (!isLoading) {
                    item {
                        Text(
                            text = "No transactions for this address.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun PeerBalanceRow(peer: AccountBalance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (peer.error == null) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = "  ${peer.host}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            val error = peer.error
            if (error != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (peer.error == null) {
            Text(
                text = "${peer.balance ?: "0"} BDAG",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AddressTransactionRow(tx: TransactionSummary, viewedAddress: String) {
    val outgoing = tx.fromAddress?.hash.equals(viewedAddress, ignoreCase = true)
    val counterparty = if (outgoing) tx.toAddress?.hash else tx.fromAddress?.hash

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
                text = if (outgoing) "OUT" else "IN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (outgoing) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.tertiary
                }
            )
            Text(
                text = relativeTime(tx.timestamp),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${formatWeiDecimal(tx.value)} BDAG",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${if (outgoing) "to" else "from"} ${shortenHash(counterparty)}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = shortenHash(tx.hash),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}
