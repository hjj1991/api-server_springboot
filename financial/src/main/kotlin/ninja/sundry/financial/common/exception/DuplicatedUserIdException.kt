package com.hjj.apiserver.common.exception

import com.hjj.apiserver.common.ErrConst

class DuplicatedUserIdException : BaseException(ErrConst.ERR_CODE0002, ErrConst.ERR_CODE0002.msg)
