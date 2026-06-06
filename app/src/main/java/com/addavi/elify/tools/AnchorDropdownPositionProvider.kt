package com.addavi.elify.tools

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.window.PopupPositionProvider

@Stable
class AnchorDropdownPositionProvider(
    private val anchorBounds: IntRect,
    private val marginPx: Int = 8,
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: androidx.compose.ui.unit.IntSize,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        popupContentSize: androidx.compose.ui.unit.IntSize
    ): IntOffset {
        // ما anchorBounds ورودی Popup رو نادیده می‌گیریم و از anchorBounds اصلی خودمون استفاده می‌کنیم
        val a = this.anchorBounds

        // پیشفرض: زیرِ anchor و راست‌چین (مثل overflow)
        var x = a.right - popupContentSize.width
        var y = a.bottom

        // clamp داخل صفحه با margin
        x = x.coerceIn(marginPx, windowSize.width - popupContentSize.width - marginPx)
        y = y.coerceIn(marginPx, windowSize.height - popupContentSize.height - marginPx)

        return IntOffset(x, y)
    }
}
