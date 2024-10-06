package uk.akane.accord.logic.utils

object UiUtils {
    data class ScreenCorners(
        val topLeft: Float,
        val topRight: Float,
        val bottomLeft: Float,
        val bottomRight: Float
    ) {
        fun getAvgRadius() =
            (topLeft + topRight + bottomLeft + bottomRight) / 4f
    }
}