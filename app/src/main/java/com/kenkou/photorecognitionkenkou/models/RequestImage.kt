package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty


class RequestImage(
        @JsonProperty("requests") var requests: List<Image>
)