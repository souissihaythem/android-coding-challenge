package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


class ImageContent(
        @JsonProperty("content") var content: String
)