package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


class LogoAnnotations(
        @JsonProperty("labelAnnotations") var labelAnnotations: List<Logo>
)