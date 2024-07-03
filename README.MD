# KomposeNepaliDatePicker

This is KMP port of a date picker library written using Jetpack Compose for Android. A big thanks
to [Kiran Gyawali](https://github.com/keyrunHORNET) for
this awesome [library](https://github.com/keyrunHORNET/composeNepaliDatePicker)

# How to use

Add common library as dependency in common build.gradle:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.thegbguy:nepali-date-picker:1.0.0")
}
```

# Using Date Picker

```kotlin
var showDialog by remember { mutableStateOf(false) }

if (showDialog) {
    NepaliDatePicker(
        onDateSelected = { date ->
            // selected date
        },
        onDismiss = {
            showDialog = false
        }
    )
}
```

`NepaliDate` is a data class used to hold the value of year, month, day, and weekday in date converters and in callbacks
of the date picker.

- `onDismiss` is mandatory for the callback on the dismiss of the picker dialog.
- `onDateSelected` is mandatory for the callback on the date selected. Returns `selectedNepaliDate`.

## Picker Options

`NepaliDatePicker` can take additional arguments to customize the picker to your needs:

- `startDate` of type `NepaliDate` for the date you want to start with in the picker. Its default value is the current
  instance.
- `showYearPickerFirst` of type `Boolean` to show the year picker first when the picker is displayed. It is true by
  default; set it to false if you want the month view as the default view, especially if you have a limited date for
  selection in the picker.
- `minDate` of type `NepaliDate` to set the lower limit of the date in the picker.
- `maxDate` of type `NepaliDate` to set the upper limit of the date in the picker.
- `highlightDays` of type `List<NepaliDate>`. This will highlight the given list of dates in the picker.
- `disableDays` of type `List<NepaliDate>`. This will disable the selection of the given dates in the picker.

# Using Date Converter

Value when passed beyond the conversion range throws an `IllegalArgumentException`. Make sure you catch them.

Converting English Date to Nepali date (i.e., A.D to B.S):

```kotlin
NepaliDateUtils.adToBs(engYY, engMM, engDD)
```

You can also pass the `Calendar` instance as an argument.

Converting Nepali Date to English date (i.e., B.S to A.D):

```kotlin
NepaliDateUtils.bsToAd(nepYY, nepMM, nepDD)
```

You can also pass `NepaliDate` as an argument.

# Additional Options

Accessible from `NepaliDateUtils`:

- `NepaliDateUtils.isEngDateInRange(int yy, int mm, int dd)` returns true if the English date is within the range of
  conversion.
- `NepaliDateUtils.isNepDateInRange(int yy, int mm, int dd)` returns true if the Nepali date is within the range of
  conversion.
- `NepaliDateUtils.getInstance()` returns the current instance of the Nepali date.
- `NepaliDateUtils.fillMissingWeekDayValue(nepaliDate: NepaliDate)` will add the missing `dayOfWeek` for a
  given `NepaliDate`.

## Author

[theGBguy](https://github.com/theGBguy)

If you like this project, please star this project to encourage the author and fork it in case you need additional
features.

## Contact

If you want to contact me you can reach me at <chiran604@gmail.com>.

## License

This project uses the following
license: [MIT License](<https://github.com/theGBguy/KomposeNepaliDatePicker/blob/main/LICENSE>).