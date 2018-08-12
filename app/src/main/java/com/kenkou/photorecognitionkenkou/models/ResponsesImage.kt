package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty

class ResponsesImage(
        @JsonProperty("responses") var responses: ArrayList<LogoAnnotations>?
)