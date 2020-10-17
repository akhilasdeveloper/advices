package com.akhil.advices.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdviceResponse(@SerializedName("slip") @Expose var slip: Slip) {
    override fun toString(): String {
        return "AdviceResponse(slip=$slip)"
    }
}

class Slip(
    @SerializedName("id") @Expose var id: Int,
    @SerializedName("advice") @Expose var advice: String
){
    override fun toString(): String {
        return "Slip(id=$id, advice='$advice')"
    }
}