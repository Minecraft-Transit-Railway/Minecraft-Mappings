package org.mtr.mapping.render.tool;

import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;

public final class RenderSystemUtil {
    public static void recordRenderCall(RenderCall renderCall){
        RenderSystem.recordRenderCall(renderCall);
    }
}