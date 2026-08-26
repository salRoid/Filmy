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
public val DropdownMenuIcon: ImageVector
    get() {
        if (_dropdown_menu_icon != null) {
            return _dropdown_menu_icon!!
        }
        _dropdown_menu_icon =
            ImageVector.Builder(
                name = "dropdown_menu",
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
                        moveTo(14.5f, 14.5f)
                        lineTo(18f, 11f)
                        horizontalLineTo(11f)
                        lineToRelative(3.5f, 3.5f)
                        close()
                        moveTo(12f, 12f)
                        close()
                        moveTo(8f, 19f)
                        quadTo(5.08f, 19f, 3.04f, 16.96f)
                        quadTo(1f, 14.93f, 1f, 12f)
                        quadTo(1f, 9.07f, 3.04f, 7.04f)
                        reflectiveQuadTo(8f, 5f)
                        horizontalLineToRelative(8f)
                        quadToRelative(2.93f, 0f, 4.96f, 2.04f)
                        reflectiveQuadTo(23f, 12f)
                        reflectiveQuadToRelative(-2.04f, 4.96f)
                        reflectiveQuadTo(16f, 19f)
                        horizontalLineTo(8f)
                        close()
                        moveTo(8f, 17f)
                        horizontalLineToRelative(8f)
                        quadToRelative(2.07f, 0f, 3.54f, -1.46f)
                        reflectiveQuadTo(21f, 12f)
                        quadTo(21f, 9.92f, 19.54f, 8.46f)
                        reflectiveQuadTo(16f, 7f)
                        horizontalLineTo(8f)
                        quadTo(5.93f, 7f, 4.46f, 8.46f)
                        reflectiveQuadTo(3f, 12f)
                        reflectiveQuadToRelative(1.46f, 3.54f)
                        reflectiveQuadTo(8f, 17f)
                        close()
                    }
                }
                .build()
        return _dropdown_menu_icon!!
    }

private var _dropdown_menu_icon: ImageVector? = null
