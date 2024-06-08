package org.mtr.mapping.render.tool;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;

import java.util.Objects;

public final class SlideRenderType extends RenderLayer {
    private static final ImmutableList<RenderPhase> GENERAL_STATES;

    static {
        GENERAL_STATES = ImmutableList.of(
                TRANSLUCENT_TRANSPARENCY,
                DISABLE_DIFFUSE_LIGHTING,
                SHADE_MODEL,
                ONE_TENTH_ALPHA,
                LEQUAL_DEPTH_TEST,
                ENABLE_CULLING,
                ENABLE_LIGHTMAP,
                DISABLE_OVERLAY_COLOR,
                FOG,
                NO_LAYERING,
                MAIN_TARGET,
                DEFAULT_TEXTURING,
                ALL_MASK,
                FULL_LINE_WIDTH
        );
    }

    private final int mHashCode;

    @Deprecated
    public SlideRenderType(String modId, int texture) {
        super(modId, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                GL11.GL_QUADS, 256, false, true,
                () -> {
                    GENERAL_STATES.forEach(RenderPhase::startDrawing);
                    RenderSystem.enableTexture();
                    RenderSystem.bindTexture(texture);
                },
                () -> GENERAL_STATES.forEach(RenderPhase::endDrawing));
        mHashCode = Objects.hash(super.hashCode(), GENERAL_STATES, texture);
    }

    @Deprecated
    public SlideRenderType(String modId, Identifier texture) {
        super(modId, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                GL11.GL_QUADS, 256, false, true,
                () -> {
                    GENERAL_STATES.forEach(RenderPhase::startDrawing);
                    RenderSystem.enableTexture();
                    MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
                },
                () -> GENERAL_STATES.forEach(RenderPhase::endDrawing));
        mHashCode = Objects.hash(super.hashCode(), GENERAL_STATES, texture);
    }

    @Override
    public int hashCode() {
        return mHashCode;
    }
}