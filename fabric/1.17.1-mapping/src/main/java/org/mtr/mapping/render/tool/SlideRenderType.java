package org.mtr.mapping.render.tool;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.mtr.mapping.holder.Identifier;

public final class SlideRenderType extends RenderLayer.MultiPhase {

    @Deprecated
    public SlideRenderType(String modId, int texture) {
        super(modId, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS, 256, false, true,
                MultiPhaseParameters.builder()
                        .shader(TRANSPARENT_TEXT_SHADER)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .cull(ENABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .overlay(DISABLE_OVERLAY_COLOR)
                        .layering(NO_LAYERING)
                        .target(MAIN_TARGET)
                        .texturing(DEFAULT_TEXTURING)
                        .writeMaskState(ALL_MASK)
                        .lineWidth(FULL_LINE_WIDTH)
                        .build(true));
        var baseSetup = this.beginAction;
        this.beginAction = () -> {
            baseSetup.run();
            RenderSystem.setShaderTexture(0, texture);
        };
    }

    @Deprecated
    public SlideRenderType(String modId, Identifier texture) {
        super(modId + "_icon", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS, 256, false, true,
                MultiPhaseParameters.builder()
                        .shader(TRANSPARENT_TEXT_SHADER)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .cull(ENABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .overlay(DISABLE_OVERLAY_COLOR)
                        .layering(NO_LAYERING)
                        .target(MAIN_TARGET)
                        .texturing(DEFAULT_TEXTURING)
                        .writeMaskState(ALL_MASK)
                        .lineWidth(FULL_LINE_WIDTH)
                        .build(true));
        var baseSetup = this.beginAction;
        this.beginAction = () -> {
            baseSetup.run();
            RenderSystem.setShaderTexture(0, texture.data);
        };
    }
}