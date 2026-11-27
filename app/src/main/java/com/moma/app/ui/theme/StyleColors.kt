package com.moma.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.moma.core.model.MartialStyle

/**
 * Presentation colour per style. Kept out of the domain enum so the domain
 * layer stays JVM-only and testable without Android or Compose on the classpath.
 */
val MartialStyle.color: Color
    get() = when (this) {
        MartialStyle.BJJ_GI -> StyleBjjGi
        MartialStyle.BJJ_NO_GI -> StyleBjjNoGi
        MartialStyle.JUDO -> StyleJudo
        MartialStyle.WRESTLING -> StyleWrestling
        MartialStyle.KYOKUSHIN -> StyleKyokushin
        MartialStyle.MUAY_THAI -> StyleMuayThai
    }
