package com.hjj.apiserver.common.exception

import com.hjj.apiserver.common.ErrConst

class NotFoundException(
    val errorConst: ErrConst,
) : RuntimeException(
    errorConst.msg,
)
