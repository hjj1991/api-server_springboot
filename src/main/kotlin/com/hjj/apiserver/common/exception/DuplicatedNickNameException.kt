package com.hjj.apiserver.common.exception

import com.hjj.apiserver.common.ErrConst

class DuplicatedNickNameException : BaseException(ErrConst.ERR_CODE0003, ErrConst.ERR_CODE0003.msg)
