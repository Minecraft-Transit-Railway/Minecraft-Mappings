package org.mtr.mapping.render.tool;

import com.mojang.blaze3d.systems.IRenderCall;
import com.mojang.blaze3d.systems.RenderSystem;

public final class RenderSystemUtil {
    public static void recordRenderCall(IRenderCall renderCall){
        RenderSystem.recordRenderCall(renderCall);
    }
}