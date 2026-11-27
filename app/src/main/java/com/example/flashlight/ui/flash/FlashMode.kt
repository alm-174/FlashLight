package com.example.flashlight.ui.flash

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.flashlight.R

/**
 * 3 tab của màn Flash.
 *  iconRes   : icon nhỏ trên tab (đổi màu xám/xanh bằng code)
 *  imageOff  : hình trong vòng tròn khi TẮT  (ảnh màu xuất từ Figma)
 *  imageOn   : hình trong vòng tròn khi BẬT
 *  imageSize : kích thước hình – mỗi kiểu một cỡ theo Figma
 */
enum class FlashMode(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val imageOff: Int,
    @DrawableRes val imageOn: Int,
    @DimenRes val imageSize: Int
) {
    LIGHT(
        R.string.flash_tab_light, R.drawable.ic_flashlight,
        R.drawable.img_flash_light, R.drawable.img_flash_light, R.dimen.flash_image_light
    ),
    SOS(
        R.string.flash_tab_sos, R.drawable.ic_siren,
        R.drawable.img_sos_off, R.drawable.img_sos_on, R.dimen.flash_image_sos
    ),
    DJ(
        R.string.flash_tab_dj, R.drawable.ic_music_note,
        R.drawable.img_dj_off, R.drawable.img_dj_on, R.dimen.flash_image_dj
    )
}