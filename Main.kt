fun main() {
    // Задача 1. Только что
    println("Введите, сколько секунд прошло с последнего визита:")
    val proshloSekund = readLine()!!.toInt()

    println(agoToText(proshloSekund))

    // Задача 2. Разная комиссия
    println("Введите тип карты (VK Pay / Mastercard / Maestro / Visa / Mir):")
    val tipKarty = readLine()!!.trim() // trim убирает лишние пробелы

    println("Введите сумму предыдущих переводов в этом месяце (руб.):")
    val predyduschiePerevody = readLine()!!.toLong()

    println("Введите сумму перевода (руб.):")
    val summaPerevoda = readLine()!!.toLong()

    val komissiya = calculateCommission(
        cardType = tipKarty,
        previousTransfersOfMonth = predyduschiePerevody,
        transferAmount = summaPerevoda
    )

    if (komissiya == -1L) {
        println("Перевод невозможен: превышен лимит")
    } else {
        println("Комиссия: ${komissiya / 100} руб. ${komissiya % 100} коп.")
    }
}

// Код задачи 1
fun agoToText(seconds: Int): String {
    return when (seconds) {
        in 0..60 -> "был(а) только что"
        in 61..60 * 60 -> "был(а) в сети ${minutesToText(seconds / 60)} назад"
        in (60 * 60 + 1)..24 * 60 * 60 -> "был(а) в сети ${hoursToText(seconds / 3600)} назад"
        in (24 * 60 * 60 + 1)..48 * 60 * 60 -> "был(а) вчера"
        in (48 * 60 * 60 + 1)..72 * 60 * 60 -> "был(а) позавчера"
        else -> "был(а) давно"
    }
}

fun minutesToText(minuty: Int): String {
    return when {
        minuty % 100 in 11..14 -> "$minuty минут"
        minuty % 10 == 1 -> "$minuty минуту"
        minuty % 10 in 2..4 -> "$minuty минуты"
        else -> "$minuty минут"
    }
}

fun hoursToText(chasy: Int): String {
    return when {
        chasy % 100 in 11..14 -> "$chasy часов"
        chasy % 10 == 1 -> "$chasy час"
        chasy % 10 in 2..4 -> "$chasy часа"
        else -> "$chasy часов"
    }
}

// Код задачи 2
fun calculateCommission(
    cardType: String = "VK Pay",
    previousTransfersOfMonth: Long = 0,
    transferAmount: Long
): Long {
    val summaVKopejkah = transferAmount * 100 // переводим в копейки
    val predyduschieVKopejkah = previousTransfersOfMonth * 100

    val dnevnoyLimit = 150_000L * 100
    val mesyachnyyLimit = 600_000L * 100
    val vkPayDnevnoyLimit = 15_000L * 100
    val vkPayMesyachnyyLimit = 40_000L * 100
    val limitMastercard = 75_000L * 100

    // Snachala proveryaem limity
    if (cardType == "VK Pay") {
        if (summaVKopejkah > vkPayDnevnoyLimit) return -1
        if (predyduschieVKopejkah + summaVKopejkah > vkPayMesyachnyyLimit) return -1
    } else {
        if (summaVKopejkah > dnevnoyLimit) return -1
        if (predyduschieVKopejkah + summaVKopejkah > mesyachnyyLimit) return -1
    }

    // Teper' schitaem komissiyu
    return when (cardType) {
        "Mastercard", "Maestro" ->
            if (predyduschieVKopejkah + summaVKopejkah <= limitMastercard) {
                0L
            } else {
                (summaVKopejkah * 0.006).toLong() + 20L * 100
            }
        "Visa", "Mir" -> {
            val nalog = (summaVKopejkah * 0.0075).toLong()
            val minimalnyyNalog = 35L * 100
            if (nalog < minimalnyyNalog) minimalnyyNalog else nalog
        }
        else -> 0L // VK Pay — vsegda besplatno
    }
}