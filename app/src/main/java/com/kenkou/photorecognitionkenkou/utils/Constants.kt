package com.kenkou.photorecognitionkenkou.utils

object Constants {
    object REQUESTS {
        const val REQUEST_GALLERY = 1
        const val REQUEST_CAMERA = 2
        const val REQUEST_PERMISSIONS = 3
        const val REQUEST_SIGNIN = 4
    }

    object SCOPES {
        const val CLOUD_PLATEFORM = "https://www.googleapis.com/auth/cloud-platform"
        const val CLOUD_VISION = "https://www.googleapis.com/auth/cloud-vision"
        const val OAUTH2_PROFILE_EMAIL = "oauth2:profile email"
    }

    object HEADER_KEY {
        const val CONTENT_TYPE = "Content-Type"
        const val AUTHORIZATION = "Authorization"
    }

    object HEADER_VALUE {
        const val APPLICATION_JSON = "application/json"
        const val BEARER = "Bearer"
    }

    object DIRECTORY {
        const val IMAGE = "/kenkou"
    }
}
