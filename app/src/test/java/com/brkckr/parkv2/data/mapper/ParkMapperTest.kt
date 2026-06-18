package com.brkckr.parkv2.data.mapper

import com.brkckr.parkv2.data.local.entity.ParkEntity
import com.brkckr.parkv2.domain.model.ParkStatus
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ParkMapperTest {

    @Test
    fun `ParkEntity toDomain correctly maps open status when isOpen is 1 and has capacity`() {
        // Given
        val entity = ParkEntity(
            parkID = 1,
            parkName = "Test Park",
            lat = 41.0,
            lng = 28.0,
            capacity = 100,
            emptyCapacity = 50,
            isOpen = 1,
            parkType = "Open",
            district = "Test District",
            workHours = "24/7",
            freeTime = "15m",
            fee = "10 TL",
            monthlyFee = "500 TL",
            isFavorite = true
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertThat(domain.parkID).isEqualTo(1)
        assertThat(domain.isOpen).isTrue()
        assertThat(domain.status).isEqualTo(ParkStatus.OPEN)
        assertThat(domain.isFavorite).isTrue()
    }

    @Test
    fun `ParkEntity toDomain correctly maps full status when isOpen is 1 but capacity is 0`() {
        // Given
        val entity = ParkEntity(
            parkID = 1,
            parkName = "Test Park",
            lat = 41.0,
            lng = 28.0,
            capacity = 100,
            emptyCapacity = 0,
            isOpen = 1,
            parkType = "Open",
            district = "Test District",
            workHours = "24/7",
            freeTime = "15m",
            fee = "10 TL",
            monthlyFee = "500 TL"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertThat(domain.status).isEqualTo(ParkStatus.FULL)
    }

    @Test
    fun `ParkEntity toDomain correctly maps closed status when isOpen is 0`() {
        // Given
        val entity = ParkEntity(
            parkID = 1,
            parkName = "Test Park",
            lat = 41.0,
            lng = 28.0,
            capacity = 100,
            emptyCapacity = 50,
            isOpen = 0,
            parkType = "Open",
            district = "Test District",
            workHours = "24/7",
            freeTime = "15m",
            fee = "10 TL",
            monthlyFee = "500 TL"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertThat(domain.isOpen).isFalse()
        assertThat(domain.status).isEqualTo(ParkStatus.CLOSED)
    }
}
