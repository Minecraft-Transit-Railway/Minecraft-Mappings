package org.mtr.mapping.render.tool;

import com.mojang.blaze3d.platform.GlStateManager;

public final class GlStateManagerUtil {
    public static void _bindTexture(int texture) {
        GlStateManager.bindTexture(texture);
    }

    public static void _deleteTexture(int texture) {
        GlStateManager.deleteTexture(texture);
    }
}