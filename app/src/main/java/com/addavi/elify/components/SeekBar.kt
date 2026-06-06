import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Neon Glow Seekbar - یه seekbar مدرن با افکت نئون
 *
 * @param currentPosition موقعیت فعلی پخش (به میلی‌ثانیه)
 * @param duration مدت کل آهنگ (به میلی‌ثانیه)
 * @param onSeek callback که وقتی کاربر seek می‌کنه صدا زده میشه
 * @param modifier برای تنظیمات layout
 * @param neonColor رنگ اصلی نئون (پیشفرض: بنفش روشن)
 * @param glowIntensity شدت درخشش (0f تا 1f)
 */
@Composable
fun NeonGlowSeekBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    neonColor: Color = Color(0xFF00F5FF), // رنگ cyan نئونی
    glowIntensity: Float = 0.8f
) {
    // State برای مدیریت drag
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }

    // محاسبه progress (درصد پیشرفت)
    val progress = if (duration > 0) {
        currentPosition.toFloat() / duration.toFloat()
    } else 0f

    // انیمیشن smooth برای progress
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragPosition else progress,
        animationSpec = if (isDragging) {
            // وقتی drag می‌کنیم، سریع‌تر حرکت کنه
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else {
            // وقتی موزیک پخش میشه، نرم‌تر حرکت کنه
            tween(durationMillis = 200, easing = LinearEasing)
        },
        label = "seekbar_progress"
    )


    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Seekbar اصلی
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .pointerInput(Unit) {
                    // مدیریت drag (کشیدن thumb)
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            // وقتی drag تموم شد، موقعیت جدید رو به callback بفرست
                            val seekPosition = (dragPosition * duration).toLong()
                            onSeek(seekPosition)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val x = change.position.x
                            // محاسبه progress جدید بر اساس موقعیت انگشت
                            dragPosition = (x / size.width).coerceIn(0f, 1f)
                        }
                    )
                }
                .pointerInput(Unit) {
                    // مدیریت tap (زدن روی seekbar)
                    detectTapGestures { offset ->
                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        val seekPosition = (newProgress * duration).toLong()
                        onSeek(seekPosition)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerY = canvasHeight / 2
                val progressWidth = canvasWidth * animatedProgress

                val trackHeight = 5.dp.toPx()
                val thumbRadius = if (isDragging) 16.dp.toPx() else 14.dp.toPx()

                // 1️⃣ Background track (خط پس‌زمینه تاریک)
                drawLine(
                    color = Color(0xFF1A1A2E),
                    start = Offset(0f, centerY),
                    end = Offset(canvasWidth, centerY),
                    strokeWidth = trackHeight,
                    cap = StrokeCap.Round
                )

                // 2️⃣ Outer glow برای track (هاله بیرونی)
                if (progressWidth > 0) {
                    // لایه اول glow (بزرگ‌تر و کم‌رنگ‌تر)
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                neonColor.copy(alpha = 0.1f * glowIntensity),
                                neonColor.copy(alpha = 0.3f * glowIntensity),
                                neonColor.copy(alpha = 0.1f * glowIntensity)
                            ),
                            startX = 0f,
                            endX = progressWidth
                        ),
                        start = Offset(0f, centerY),
                        end = Offset(progressWidth, centerY),
                        strokeWidth = trackHeight * 4,
                        cap = StrokeCap.Round,
                        blendMode = BlendMode.Plus // برای افکت نئون
                    )

                    // لایه دوم glow (متوسط)
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                neonColor.copy(alpha = 0.3f * glowIntensity),
                                neonColor.copy(alpha = 0.5f * glowIntensity),
                                neonColor.copy(alpha = 0.3f * glowIntensity)
                            ),
                            startX = 0f,
                            endX = progressWidth
                        ),
                        start = Offset(0f, centerY),
                        end = Offset(progressWidth, centerY),
                        strokeWidth = trackHeight * 2,
                        cap = StrokeCap.Round,
                        blendMode = BlendMode.Plus
                    )
                }

                // 3️⃣ Active track (خط اصلی نئونی)
                if (progressWidth > 0) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                neonColor.copy(alpha = 0.7f),
                                neonColor,
                                neonColor.copy(alpha = 0.9f)
                            ),
                            startX = 0f,
                            endX = progressWidth
                        ),
                        start = Offset(0f, centerY),
                        end = Offset(progressWidth, centerY),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )
                }

                // 4️⃣ Thumb (دایره کشیدنی) با افکت نئون
                val thumbX = progressWidth.coerceIn(0f, canvasWidth)



                // دایره میانی thumb (نئونی)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            neonColor,
                            neonColor.copy(alpha = 0.8f)
                        ),
                        center = Offset(thumbX, centerY),
                        radius = thumbRadius * 0.7f
                    ),
                    radius = thumbRadius * 0.7f,
                    center = Offset(thumbX, centerY)
                )

                // نقطه مرکزی thumb (سفید درخشان)
                drawCircle(
                    color = Color.White,
                    radius = thumbRadius * 0.3f,
                    center = Offset(thumbX, centerY)
                )

                // 5️⃣ افکت اضافی: خطوط عمودی نئونی در موقعیت‌های خاص
                // (برای جذابیت بیشتر)
                if (isDragging) {
                    for (i in 0..10) {
                        val x = (canvasWidth / 10) * i
                        if (x < progressWidth) {
                            drawLine(
                                color = neonColor.copy(alpha = 0.2f),
                                start = Offset(x, centerY - trackHeight * 2),
                                end = Offset(x, centerY + trackHeight * 2),
                                strokeWidth = 1.dp.toPx(),
                                blendMode = BlendMode.Plus
                            )
                        }
                    }
                }
            }
        }
    }
}

