package com.example.facilityshop.model

interface CartInterface {
    fun saveProduct(idProduct: String, idUser: String)
    fun deleteProduct(idProduct: String, idUser: String)
}