package com.zero.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.zero.android.common.R

val InterFontFamily =
	FontFamily(
		Font(R.font.inter_regular),
		Font(R.font.inter_bold, FontWeight.Bold),
		Font(R.font.inter_extra_bold, FontWeight.ExtraBold),
		Font(R.font.inter_extra_light, FontWeight.ExtraLight),
		Font(R.font.inter_light, FontWeight.Light),
		Font(R.font.inter_medium, FontWeight.Medium),
		Font(R.font.inter_semi_bold, FontWeight.SemiBold),
		Font(R.font.inter_thin, FontWeight.Thin)
	)

private val CustomFontStyle = TextStyle(fontFamily = InterFontFamily)

fun Typography.customTextStyle(
	fontSize: TextUnit,
	fontWeight: FontWeight = FontWeight.Normal,
	fontStyle: FontStyle = FontStyle.Normal
) = CustomFontStyle.copy(fontSize = fontSize, fontWeight = fontWeight, fontStyle = fontStyle)

fun Typography.customTextStyle(defaultTextStyle: TextStyle) =
	defaultTextStyle.copy(fontFamily = InterFontFamily)

// Set of Material typography styles to start with
val Typography =
	Typography(
		displaySmall = CustomFontStyle.copy(fontWeight = FontWeight.Medium, fontSize = 12.sp),
		displayMedium = CustomFontStyle.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp),
		displayLarge = CustomFontStyle.copy(fontWeight = FontWeight.Medium, fontSize = 16.sp),
		bodySmall = CustomFontStyle.copy(fontWeight = FontWeight.Normal, fontSize = 12.sp),
		bodyMedium = CustomFontStyle.copy(fontWeight = FontWeight.Normal, fontSize = 14.sp),
		bodyLarge = CustomFontStyle.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp),
		labelSmall = CustomFontStyle.copy(fontWeight = FontWeight.Medium, fontSize = 10.sp),
		labelMedium = CustomFontStyle.copy(fontWeight = FontWeight.Medium, fontSize = 11.sp),
		labelLarge = CustomFontStyle.copy(fontWeight = FontWeight.Medium, fontSize = 12.sp)
	)
