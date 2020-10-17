package com.akhil.advices.dao

import com.akhil.advices.api.AdviceResponse

data class MainResponse(
    var adviceResponse: AdviceResponse?,
    var networkResponse: String
)