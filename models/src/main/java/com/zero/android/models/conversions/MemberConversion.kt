package com.zero.android.models.conversions

import com.zero.android.models.Member
import com.zero.android.models.MemberMeta

fun Member.toMeta() = MemberMeta(id = id, name = name, image = image)
