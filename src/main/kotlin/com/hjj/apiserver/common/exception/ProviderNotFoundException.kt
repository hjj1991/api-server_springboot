package com.hjj.apiserver.common.exception

import java.io.Serial

class ProviderNotFoundException: Exception {

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)

    companion object {
        private const val serialVersionUID: Long = -5895709377922476126L
    }
}