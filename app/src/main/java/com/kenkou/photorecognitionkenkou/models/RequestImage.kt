package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


class RequestImage(
        @JsonProperty("requests") var requests: List<Image>
): Serializable