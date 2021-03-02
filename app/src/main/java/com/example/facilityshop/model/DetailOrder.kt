package com.example.facilityshop.model

class DetailOrder {
    var idProduct = 0
    var pname = ""
    var quantity = ""
    var price = ""

    constructor(idProduct: Int, pname: String, quantity: String, price: String) {
        this.idProduct = idProduct
        this.pname = pname
        this.quantity = quantity
        this.price = price
    }
}