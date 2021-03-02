package com.example.facilityshop.model

class History {
    var id = 0
    var buyer = ""
    var dateOrder = ""
    var timeOrder = ""
    var state = ""
    var totalAmount = ""

    constructor(
        id: Int,
        buyer: String,
        dateOrder: String,
        timeOrder: String,
        state: String,
        totalAmount: String
    ) {
        this.id = id
        this.buyer = buyer
        this.dateOrder = dateOrder
        this.timeOrder = timeOrder
        this.state = state
        this.totalAmount = totalAmount
    }
}