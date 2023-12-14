package ro.davidarsene.safetycenter

import android.content.Context

enum class PositionInCardList(val backgroundDrawableResId: Int) {
    INSIDE_GROUP(R.drawable.safety_group_entry_background),
    LIST_START_END(R.drawable.card_top_large_bottom_large_background),
    LIST_START(R.drawable.card_top_large_bottom_flat_background),
    LIST_START_CARD_END(R.drawable.card_top_large_bottom_small_background),
    CARD_START(R.drawable.card_top_small_bottom_flat_background),
    CARD_START_END(R.drawable.card_top_small_bottom_small_background),
    CARD_START_LIST_END(R.drawable.card_top_small_bottom_large_background),
    CARD_ELEMENT(R.drawable.card_top_flat_bottom_flat_background),
    CARD_END(R.drawable.card_top_flat_bottom_small_background),
    LIST_END(R.drawable.card_top_flat_bottom_large_background);

    fun getTopMargin(context: Context) = when (this) {
        CARD_START, CARD_START_END, CARD_START_LIST_END -> context.resources.getDimensionPixelSize(R.dimen.sc_card_margin)
        LIST_START, LIST_START_END, LIST_START_CARD_END -> context.resources.getDimensionPixelSize(R.dimen.sc_list_margin_top)
        else -> 0
    }
}
