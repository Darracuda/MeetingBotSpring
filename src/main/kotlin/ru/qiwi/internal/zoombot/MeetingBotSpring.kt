package ru.qiwi.internal.zoombot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@SpringBootApplication
class ZoomApplication() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ZoomApplication>(*args)
        }
    }
}