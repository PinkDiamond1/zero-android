package com.zero.android.ui.theme

private val DarkExtendedColorPalette =
	ExtendedColor(
		chatBubblePrimary = PersianIndigo,
		chatBubblePrimaryVariant = RussianViolet,
		chatBubbleSecondary = CetaceanBlue,
		header = Platinum,
		headerVariant = RaisinBlack,
		buttonPrimary = RaisinBlack75,
		buttonSecondary = Gray,
		colorTextPrimary = White,
		colorTextSecondary = TaupeGray,
		colorTextSecondaryVariant = Gray,
		success = EmeraldGreen,
		glow = LavenderIndigo,
		glowVariant = Indigo,
		divider = Gray,
		surface = White,
		surfaceVariant = Gray,
		surfaceInverse = Black
	)

private val LightExtendedColorPalette =
	ExtendedColor(
		chatBubblePrimary = CadetBlue,
		chatBubblePrimaryVariant = Rhythm,
		chatBubbleSecondary = CetaceanBlue,
		header = ChineseBlack,
		headerVariant = RaisinBlack,
		buttonPrimary = RaisinBlack75,
		buttonSecondary = Gray,
		colorTextPrimary = RaisinBlack75,
		colorTextSecondary = PhilippineSilver,
		colorTextSecondaryVariant = Gray,
		success = EmeraldGreen,
		glow = LavenderIndigo,
		glowVariant = Indigo,
		divider = Gray,
		surface = Black,
		surfaceVariant = Gray,
		surfaceInverse = White
	)

// LIGHT THEME PALETTES
private val NeonCyanThemePaletteLight =
	LightExtendedColorPalette.copy(glow = Crayola, glowVariant = DeepGreen)

private val NeonPinkThemePaletteLight =
	LightExtendedColorPalette.copy(glow = Fuchsia, glowVariant = RoseViolet)

private val NeonRedThemePaletteLight =
	LightExtendedColorPalette.copy(glow = DeepCarminePink, glowVariant = BloodRed)

private val NeonBlueThemePaletteLight =
	LightExtendedColorPalette.copy(glow = VividSkyBlue, glowVariant = RichBlack)

// DARK THEME PALETTES
private val NeonCyanThemePaletteDark =
	DarkExtendedColorPalette.copy(glow = Crayola, glowVariant = DeepGreen)

private val NeonPinkThemePaletteDark =
	DarkExtendedColorPalette.copy(glow = Fuchsia, glowVariant = RoseViolet)

private val NeonRedThemePaletteDark =
	DarkExtendedColorPalette.copy(glow = DeepCarminePink, glowVariant = BloodRed)

private val NeonBlueThemePaletteDark =
	DarkExtendedColorPalette.copy(glow = VividSkyBlue, glowVariant = RichBlack)

val DEFAULT_LIGHT_PALETTE = LightExtendedColorPalette
val DEFAULT_DARK_PALETTE = DarkExtendedColorPalette

private val lightThemePalettes =
	listOf(
		DEFAULT_LIGHT_PALETTE,
		NeonCyanThemePaletteLight,
		NeonPinkThemePaletteLight,
		NeonRedThemePaletteLight,
		NeonBlueThemePaletteLight
	)

private val darkThemePalettes =
	listOf(
		DEFAULT_DARK_PALETTE,
		NeonCyanThemePaletteDark,
		NeonPinkThemePaletteDark,
		NeonRedThemePaletteDark,
		NeonBlueThemePaletteDark
	)

fun getThemePalette(darkMode: Boolean, randomThemePalette: Int): ExtendedColor {
	return if (darkMode) darkThemePalettes.getOrNull(randomThemePalette) ?: DEFAULT_DARK_PALETTE
	else lightThemePalettes.getOrNull(randomThemePalette) ?: DEFAULT_LIGHT_PALETTE
}
