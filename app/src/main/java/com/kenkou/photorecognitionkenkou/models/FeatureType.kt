package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty


class FeatureType(
        @JsonProperty("type") var type: String
)