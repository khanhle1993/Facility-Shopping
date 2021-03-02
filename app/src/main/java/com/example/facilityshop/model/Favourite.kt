package com.example.facilityshop.model

class Favourite {
    var id = 0
    var idProduct = 0
    var idUser = 0
    var pname = ""
    var price = 0
    var image = ""

    constructor(id: Int, idProduct: Int, idUser: Int, pname: String, price: Int, image: String) {
        this.id = id
        this.idProduct = idProduct
        this.idUser = idUser
        this.pname = pname
        this.price = price
        this.image = image
    }
}