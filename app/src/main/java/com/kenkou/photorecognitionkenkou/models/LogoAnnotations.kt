package com.kenkou.photorecognitionkenkou.models

import com.fasterxml.jackson.annotation.JsonProperty


class LogoAnnotations(
        @JsonProperty("labelAnnotations") var labelAnnotations: List<Logo>
)