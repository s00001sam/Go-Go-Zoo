package com.sam.gogozoo.util

import com.sam.gogozoo.R
import com.sam.gogozoo.util.Util.getString

enum class CurrentFragmentType(val value: String) {
    HOME(getString(R.string.home_title)),
    LIST(getString(R.string.list_title)),
    SCHEDULE(getString(R.string.schedule_title)),
    DETAILAREA(""),
    DETAILANIMAL("")
}