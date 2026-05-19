package com.frosch2010.lifestyle_scoring_app.ui.util

import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum

/**
 * Resolves the localized section title for this card family inside points breakdown lists.
 *
 * @receiver Card pillar whose label should be shown next to per-category scores.
 * @return String resource id for `getString`.
 */
fun CardTypeEnum.pointsCategoryLabelResId(): Int = when (this) {
    CardTypeEnum.ANIMAL -> R.string.points_category_animal
    CardTypeEnum.CAR -> R.string.points_category_car
    CardTypeEnum.HOUSE -> R.string.points_category_house
    CardTypeEnum.JOB -> R.string.points_category_job
    CardTypeEnum.SPORT -> R.string.points_category_sport
    CardTypeEnum.LOVE -> R.string.points_category_love
}

/**
 * Resolves the Material-style icon used when rendering this category row in breakdown UIs.
 *
 * @receiver Card pillar whose leading glyph should reflect the lifestyle theme.
 * @return Drawable resource id suitable for `AppCompatResources.getDrawable`.
 */
fun CardTypeEnum.pointsCategoryIconResId(): Int = when (this) {
    CardTypeEnum.ANIMAL -> R.drawable.ic_google_pets
    CardTypeEnum.CAR -> R.drawable.ic_google_directions_car
    CardTypeEnum.HOUSE -> R.drawable.ic_google_home
    CardTypeEnum.JOB -> R.drawable.ic_google_work
    CardTypeEnum.SPORT -> R.drawable.ic_google_fitness_center
    CardTypeEnum.LOVE -> R.drawable.ic_google_favorite
}

/**
 * Resolves the theme color token applied as icon tint for visual grouping in breakdown rows.
 *
 * @receiver Card pillar whose brand color should tint list adornments.
 * @return Color resource id resolved through `MaterialColors` or theme attributes downstream.
 */
fun CardTypeEnum.pointsCategoryTintResId(): Int = when (this) {
    CardTypeEnum.ANIMAL -> R.color.card_animal
    CardTypeEnum.CAR -> R.color.card_car
    CardTypeEnum.HOUSE -> R.color.card_house
    CardTypeEnum.JOB -> R.color.card_job
    CardTypeEnum.SPORT -> R.color.card_sport
    CardTypeEnum.LOVE -> R.color.card_love
}
