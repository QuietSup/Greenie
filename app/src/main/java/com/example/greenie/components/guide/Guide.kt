package com.example.greenie.components.guide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.greenie.PreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Guide(
    navController: NavHostController,
    preferencesDataStore: PreferencesDataStore,
    scope: CoroutineScope
) {
    val pages: List<@Composable () -> Unit> = listOf(
        {
            GuideItem(
                imgName = "ai_diagnose.png",
                strings = listOf(
                    "Diagnose plants with",
                    "AI"
                ),
                stressIndex = 1
            )
        },
        {
            GuideItem(
                imgName = "treatment_recs.png",
                strings = listOf(
                    "Treatment",
                    "recommendations"
                ),
                stressIndex = 0
            )
        },
        {
            GuideItem(
                imgName = "reminders.png",
                strings = listOf(
                    "Reminders",
                    "with recommended treatments"
                ),
                stressIndex = 0
            )
        },
        {
            GuideItem(
                imgName = "notes.png",
                strings = listOf(
                    "Keep track of your",
                    "progress",
                ),
                stressIndex = 1
            )
        },
    )

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = "Start", style = MaterialTheme.typography.titleMedium)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = ""
                    )
                },
                onClick = {
                    if (pagerState.currentPage == pages.lastIndex) {
                        navController.navigate("journal") {
                            navController.popBackStack("journal", true)
                            scope.launch {
                                preferencesDataStore.setFirstLaunch(false)
                            }
                        }
                    }
                    else {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        }
                    }
                },
                expanded = pagerState.currentPage == pages.lastIndex
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .size(16.dp)
                            .background(color)
                    )
                }
            }
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = it
        ) { index ->
            pages[index]()
        }
    }
}

@Composable
fun GuideItem(imgName: String, strings: List<String>, stressIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-20).dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .height(62.sp),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                lineHeight = 31.sp,
                text = buildAnnotatedString {
                    strings.forEachIndexed { index, s ->
                        if (index == stressIndex) {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            ) {
                                append("$s ")
                            }
                        } else {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                )
                            ) {
                                append("$s ")
                            }
                        }
                    }
                }
            )
        }

        AsyncImage(
            model = "file:///android_asset/guide/$imgName",
            contentDescription = "",
            modifier = Modifier
                .fillMaxHeight()
        )
    }
}