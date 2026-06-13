package com.brkckr.parkv2.domain.model

import com.brkckr.parkv2.R

enum class ParkStatus(val resId: Int) {
    OPEN(R.string.open),
    FULL(R.string.full),
    CLOSED(R.string.closed)
}
