package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty


class Image(
        @JsonProperty("image") var image: ImageContent,
        @JsonProperty("features") var features: ArrayList<FeatureType>
)