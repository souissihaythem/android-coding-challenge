package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty


class ImageContent(
        @JsonProperty("content") var content: String?
)