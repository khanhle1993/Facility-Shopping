package com.example.facilityshop.model

class Product {
    var id = 0
    var category = ""
    var ptype = ""
    var price = 0
    var pname = ""
    var description = ""
    var image1 = ""
    var image2 = ""
    var image3 = ""

    constructor(
        id: Int,
        category: String,
        ptype: String,
        pname: String,
        price: Int,
        description: String,
        image1: String,
        image2: String,
        image3: String
    ) {
        this.id = id
        this.category = category
        this.ptype = ptype
        this.pname = pname
        this.price = price
        this.description = description
        this.image1 = image1
        this.image2 = image2
        this.image3 = image3
    }
}