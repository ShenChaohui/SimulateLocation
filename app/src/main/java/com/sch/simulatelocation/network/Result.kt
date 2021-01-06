package com.sch.simulatelocation.network

/**
 * Created by Sch.
 * Date: 2020/12/14
 * description:
 */
data class Result(
    var info: String,
    var infocode: String,
    var regeocode: Regeocode,
    var status: String
)

data class Regeocode(
    var addressComponent: AddressComponent,
    var formatted_address: String
)

data class AddressComponent(
    var adcode: String,
    var citycode: String,
    var country: String,
    var district: String,
    var province: String,
    var towncode: String,
    var township: String
)

