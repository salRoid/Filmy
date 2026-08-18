package tech.salroid.filmy.ui.common.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val MoreUp: ImageVector
    get() {
        if (_more_up != null) {
            return _more_up!!
        }
        _more_up =
            ImageVector.Builder(
                name = "more_up",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(17f, 16f)
                        verticalLineTo(7f)
                        horizontalLineTo(8f)
                        verticalLineTo(5f)
                        horizontalLineTo(19f)
                        verticalLineTo(16f)
                        horizontalLineTo(17f)
                        close()
                        moveToRelative(-5f, 5f)
                        verticalLineTo(12f)
                        horizontalLineTo(3f)
                        verticalLineTo(10f)
                        horizontalLineTo(14f)
                        verticalLineTo(21f)
                        horizontalLineTo(12f)
                        close()
                    }
                }
                .build()
        return _more_up!!
    }

private var _more_up: ImageVector? = null
