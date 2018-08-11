package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


class Logo(
        @JsonProperty("mid") var mid: String,
        @JsonProperty("description") var description: String?,
        @JsonProperty("score") var score: Double?,
        @JsonProperty("topicality") var topicality: Double?
): Serializable