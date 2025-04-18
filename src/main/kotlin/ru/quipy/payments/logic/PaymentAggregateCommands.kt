package ru.quipy.payments.logic

import ru.quipy.payments.api.PaymentCreatedEvent
import ru.quipy.payments.api.PaymentProcessedEvent
import ru.quipy.payments.api.PaymentSubmittedEvent
import java.io.File
import java.time.Duration
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


fun PaymentAggregateState.create(id: UUID, orderId: UUID, amount: Int): PaymentCreatedEvent {
    return PaymentCreatedEvent(
        paymentId = id,
        orderId = orderId,
        amount = amount,
    )
}

fun PaymentAggregateState.logSubmission(success: Boolean, transactionId: UUID, startedAt: Long, spentInQueueDuration: Duration): PaymentSubmittedEvent {
    return PaymentSubmittedEvent(
        this.getId(), success, this.orderId, transactionId, startedAt, spentInQueueDuration
    )
}

val processingTimesMillis = mutableListOf<Long>()

val fileWithValues = File("src/main/resources/values.txt")

fun PaymentAggregateState.logProcessing(
    success: Boolean,
    processedAt: Long,
    transactionId: UUID? = null,
    reason: String? = null
): PaymentProcessedEvent {
    val submittedAt = this.submissions[transactionId ?: UUID.randomUUID()]?.timeStarted ?: 0
    val spentInQueueDuration = this.submissions[transactionId ?: UUID.randomUUID()]?.spentInQueue ?: Duration.ofMillis(0)

/*    val processingTime = processedAt - submittedAt
    processingTimesMillis.add(processingTime)
    fileWithValues.appendText("$processingTime ")

    if (processingTimesMillis.size == 980) {
        val average = processingTimesMillis.average()
        val deviation = sqrt(processingTimesMillis.sumOf { (it - average).pow(2.0) } / 980)

        println("Среднее время исполнения запроса: $average")
        println("Стандартное отклонение: $deviation")
    }*/


    return PaymentProcessedEvent(
        this.getId(), success, this.orderId, submittedAt, processedAt, this.amount!!, transactionId, reason, spentInQueueDuration
    )
}