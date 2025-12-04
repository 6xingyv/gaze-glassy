package com.mocharealm.gaze.glassy.effects

import com.mocharealm.gaze.glassy.BackdropEffectScope
import com.mocharealm.gaze.core.platform.PlatformRenderEffect
import com.mocharealm.gaze.core.platform.PlatformVersion
import com.mocharealm.gaze.core.platform.asPlatformRenderEffect
import com.mocharealm.gaze.core.platform.createChainEffect

fun BackdropEffectScope.effect(effect: PlatformRenderEffect) {
    if (!PlatformVersion.supportsRenderEffect()) return
    val currentEffect = renderEffect
    renderEffect =
        if (currentEffect != null) {
            createChainEffect(effect, currentEffect)
        } else {
            effect
        }
}

fun BackdropEffectScope.effect(effect: androidx.compose.ui.graphics.RenderEffect) {
    if (!PlatformVersion.supportsRenderEffect()) return
    val platformEffect = effect.asPlatformRenderEffect() ?: return
    effect(platformEffect)
}
