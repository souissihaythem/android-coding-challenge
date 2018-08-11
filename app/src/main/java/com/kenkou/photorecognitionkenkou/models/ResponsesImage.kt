package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class ResponsesImage(
        @JsonProperty("responses") var responses: ArrayList<LogoAnnotations>?
)