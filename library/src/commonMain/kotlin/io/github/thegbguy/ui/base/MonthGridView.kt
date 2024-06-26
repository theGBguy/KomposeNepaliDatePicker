package io.github.thegbguy.ui.base

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import getScreenSizeInfo
import io.github.thegbguy.utils.WEIGHT_DAY_VIEW_MONTH

/**
 * Creates a [MonthGridView] for calendar view for a month with all of its dates
 * Grid View with 7 column.
 *
 * Not using experimental compose API for Grid View
 */
@Composable
internal fun MonthGridView(
    items: List<Int>,
    firstRowTab: Int,
    view: @Composable (Int, Int) -> Unit
) {

    val rem = (items.size + firstRowTab) % 7
    val div = ((items.size + firstRowTab) / 7)

    val columnCount = div + if (rem == 0) 0 else 1

    co.touchlab.kermit.Logger.d("rem: $rem, div: $div and columnCount: $columnCount")

    for (i in 0 until columnCount) {
        if (i == (columnCount - 1) && rem != 0) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until rem) {
                    view(items[((i * 7) + j) - firstRowTab], j + 1)
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 7) {
                    if (j < firstRowTab && i == 0) {
                        Spacer(modifier = Modifier.width(getScreenSizeInfo().wDP / WEIGHT_DAY_VIEW_MONTH))
                    } else {
                        view(items[((i * 7) + j) - firstRowTab], j + 1)
                    }
                }
            }
        }
    }
}