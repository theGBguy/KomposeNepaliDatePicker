package io.github.thegbguy.ui.date

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.thegbguy.date.NepaliDate
import io.github.thegbguy.date.NepaliDateUtils
import io.github.thegbguy.date.NepaliDateUtils.fillMissingWeekDayValue
import io.github.thegbguy.date.NepaliDateUtils.getInstance
import io.github.thegbguy.date.NepaliDateUtils.getNepaliMonthString
import io.github.thegbguy.date.YearMonth
import io.github.thegbguy.date.displayWithLocale
import io.github.thegbguy.date.getSelectableYearMonth
import io.github.thegbguy.library.generated.resources.Res
import io.github.thegbguy.library.generated.resources.date_picker_cancel
import io.github.thegbguy.library.generated.resources.date_picker_ok
import io.github.thegbguy.ui.base.ComposePagerSnapHelper
import io.github.thegbguy.ui.base.MonthGridView
import io.github.thegbguy.ui.date.*
import io.github.thegbguy.utils.NepaliDateConstants.maxNepaliDate
import io.github.thegbguy.utils.NepaliDateConstants.minNepaliDate
import io.github.thegbguy.utils.NpDateColor.backgroundColor
import io.github.thegbguy.utils.NpDateColor.textColor
import org.jetbrains.compose.resources.stringResource


@Composable
fun NepaliDatePicker(
    startDate: NepaliDate = getInstance(),
    showYearPickerFirst: Boolean = true,
    minDate: NepaliDate? = null,
    maxDate: NepaliDate? = null,
    highlightDays: List<NepaliDate>? = null,
    disableDays: List<NepaliDate>? = null,
    onDismiss: () -> Unit,
    onDateSelected: (NepaliDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(startDate inBounds (minDate to maxDate)) }
    var showYearPicker by remember { mutableStateOf(showYearPickerFirst) }

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
        onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = backgroundColor()
        ) {

            Column {

                DatePickerHeader(selectedDate) { showYearPicker = !showYearPicker }

                if (showYearPicker) {
                    DatePickerYearBody(
                        startDate = selectedDate,
                        minDate = minDate ?: minNepaliDate,
                        maxDate = maxDate ?: maxNepaliDate,
                    ) {
                        selectedDate =
                            fillMissingWeekDayValue(selectedDate.copy(year = it) inBounds (minDate to maxDate))
                        showYearPicker = !showYearPicker
                    }
                } else {
                    DatePickerMonthBody(
                        startDate = selectedDate,
                        minDate = minDate ?: minNepaliDate,
                        maxDate = maxDate ?: maxNepaliDate,
                        highlightDays = highlightDays,
                        disableDays = disableDays
                    ) { selectedDate = it }
                }
                DatePickerDialogButtons(
                    onDismiss = { onDismiss() },
                    onSelected = {
                        onDateSelected(selectedDate)
                        onDismiss()
                    })
            }
        }
    }
}

@Composable
private fun DatePickerDialogButtons(onSelected: () -> Unit, onDismiss: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        DialogButton(text = stringResource(Res.string.date_picker_cancel)) { onDismiss() }
        DialogButton(text = stringResource(Res.string.date_picker_ok)) { onSelected() }
    }
}

@Composable
private fun DatePickerHeader(
    selectedDate: NepaliDate,
    onYearClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
    ) {
        Text(
            fontSize = 16.sp,
            color = Color.White,
            text = displayWithLocale(selectedDate.year),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                )
                { onYearClick() }
                .padding(top = 16.dp, start = 16.dp, bottom = 4.dp)
                .fillMaxWidth()
        )
        Text(
            text = "${NepaliDateUtils.getDayOfWeek(selectedDate.dayOfWeek)}, ${
                getNepaliMonthString(selectedDate.month)
            } ${displayWithLocale(selectedDate.day)}",
            color = Color.White,
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)

        )
    }
}

@Composable
private fun DatePickerMonthBody(
    startDate: NepaliDate,
    minDate: NepaliDate,
    maxDate: NepaliDate,
    highlightDays: List<NepaliDate>?,
    disableDays: List<NepaliDate>?,
    selectedDate: (NepaliDate) -> Unit
) {

    val dateRange by remember { mutableStateOf(getSelectableYearMonth(minDate, maxDate)) }
    var currentViewDate by remember { mutableStateOf(startDate) }
    var pagerPosition by remember {
        mutableStateOf(dateRange.indexOf(YearMonth(startDate.year, startDate.month)))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 350.dp)
    ) {

        MonthWithIndicatorView(
            minDate = minDate,
            maxDate = maxDate,
            date = currentViewDate,
            onLeftClick = { pagerPosition-- },
            onRightClick = { pagerPosition++ }
        )

        ComposePagerSnapHelper(
            width = 310.dp,
            snapPosition = pagerPosition
        ) { listState ->
            val visibleYearMonth = dateRange[listState.firstVisibleItemIndex]
            currentViewDate = currentViewDate.copy(
                year = visibleYearMonth.year,
                month = visibleYearMonth.month
            )

            LazyRow(
                state = listState,
            ) {
                items(dateRange) { item ->
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        DayOfWeekView()
                        MonthDaysView(
                            yearMonth = item,
                            minDate = minDate,
                            maxDate = maxDate,
                            disableDays = disableDays,
                            highlightDays = highlightDays,
                        ) { selectedDate(it) }
                    }
                }
            }
        }
    }
}


@Composable
private fun DatePickerYearBody(
    startDate: NepaliDate,
    minDate: NepaliDate,
    maxDate: NepaliDate,
    onYearClick: (Int) -> Unit
) {
    val selectableYears = getSelectableYearMonth(minDate, maxDate)
        .map { it.year }
        .distinct()

    val index = selectableYears.indexOf(startDate.year)
    val listState = rememberLazyListState(if (index > 3) index - 3 else 0)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState
    ) {
        items(selectableYears) { item ->
            YearViewItem(
                day = displayWithLocale(item),
                isSelected = item == startDate.year,
            ) { onYearClick(item) }
        }
    }
}


@Composable
private fun DayOfWeekView() {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (i in 1..7) {
            DayOfWeekViewItem(NepaliDateUtils.getShortDayOfWeek(week = i))
        }
    }
}

@Composable
private fun MonthWithIndicatorView(
    date: NepaliDate,
    minDate: NepaliDate,
    maxDate: NepaliDate,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Row {
        if (!minDate.isSameMonthYear(date)) {
            MonthViewIndicator(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "previous month"
            ) { onLeftClick() }
        }
        Spacer(modifier = Modifier.weight(1f))

        Text(
            color = textColor(),
            text = getNepaliMonthString(date.month)
                .plus(" ")
                .plus(displayWithLocale(date.year)),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        if (!maxDate.isSameMonthYear(date)) {
            MonthViewIndicator(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "next month"
            ) { onRightClick() }
        }
    }
}

@Composable
private fun MonthDaysView(
    yearMonth: YearMonth,
    minDate: NepaliDate,
    maxDate: NepaliDate,
    highlightDays: List<NepaliDate>?,
    disableDays: List<NepaliDate>?,
    onDaySelected: (NepaliDate) -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }

    val startDayOfWeek: Int by remember {
        mutableStateOf(
            NepaliDateUtils.startWeekDayMonthMap.get(yearMonth.year)?.getOrNull(yearMonth.month)
                ?: (-1)
        )
    }
    val daysCount: Int by remember {
        mutableStateOf(
            NepaliDateUtils.daysInMonthMap.get(yearMonth.year)?.getOrNull(yearMonth.month) ?: -1
        )
    }

    MonthGridView(items = (1..daysCount).toList(), firstRowTab = startDayOfWeek) { day, dayOfWeek ->

        val date = NepaliDate(yearMonth.year, yearMonth.month, day, dayOfWeek)
        val isEnabled = (date in minDate..maxDate) && !(disableDays?.contains(date) ?: false)

        MonthDayViewItem(
            day = displayWithLocale(day),
            isSelected = selectedItem == day,
            isHighlight = highlightDays?.contains(date) == true,
            isEnabled = isEnabled,
        ) {
            selectedItem = day
            onDaySelected(date)
        }
    }
}


