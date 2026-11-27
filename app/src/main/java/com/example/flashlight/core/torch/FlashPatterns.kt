package com.example.flashlight.core.torch

import kotlin.random.Random

/** Các kiểu nháy dựng sẵn. Chỉ là dữ liệu (danh sách bước) – TorchController lo việc chạy. */
object FlashPatterns {

    // Nhịp Morse chuẩn: dấu chấm = 1 đơn vị, gạch = 3, nghỉ giữa ký hiệu = 1, giữa chữ = 3, giữa từ = 7
    private const val UNIT = 200L

    /** ... --- ...  rồi nghỉ, lặp lại. */
    fun sos(): List<FlashStep> {
        fun letter(symbol: Long) = List(3) {
            listOf(FlashStep(true, symbol), FlashStep(false, UNIT))
        }.flatten()

        val s = letter(UNIT)
        val o = letter(UNIT * 3)
        // Bước nghỉ cuối của mỗi chữ kéo dài thành nghỉ giữa chữ (3) và giữa từ (7)
        return s.dropLast(1) + FlashStep(false, UNIT * 3) +
                o.dropLast(1) + FlashStep(false, UNIT * 3) +
                s.dropLast(1) + FlashStep(false, UNIT * 7)
    }

    /** Nháy đều: sáng onMs, tắt offMs (Flash Alert dùng). */
    fun strobe(onMs: Long, offMs: Long): List<FlashStep> =
        listOf(FlashStep(true, onMs), FlashStep(false, offMs))

    /**
     * DJ: 8 nhịp ngắn với độ dài ngẫu nhiên → cảm giác nhấp nháy theo nhạc.
     * TODO (hỏi mentor): bản "theo nhạc thật" cần quyền micro (RECORD_AUDIO) để bắt nhịp.
     */
    fun dj(): List<FlashStep> = List(8) {
        listOf(
            FlashStep(true, Random.nextLong(40, 120)),
            FlashStep(false, Random.nextLong(60, 260))
        )
    }.flatten()
}
