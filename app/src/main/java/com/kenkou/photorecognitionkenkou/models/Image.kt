package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


class Image(
        @JsonProperty("image") var image: ImageContent,
        @JsonProperty("features") var features: ArrayList<FeatureType>
): Serializable