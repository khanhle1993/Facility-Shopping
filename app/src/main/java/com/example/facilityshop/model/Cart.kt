package com.example.facilityshop.model

class Cart {
    var id = 0
    var idProduct = 0
    var pname = ""
    var price = 0
    var quantity = 0
    var image = ""

    constructor(id: Int, idProduct: Int, pname: String, price: Int, quantity: Int, image: String) {
        this.id = id
        this.idProduct = idProduct
        this.pname = pname
        this.price = price
        this.quantity = quantity
        this.image = image
    }
}