package com.example.facilityshop.model

class Category {
    var id: Int = 0
    var name: String = ""
    var image: String = ""

    constructor(id: Int, name: String, image: String) {
        this.id = id
        this.name = name
        this.image = image
    }
}