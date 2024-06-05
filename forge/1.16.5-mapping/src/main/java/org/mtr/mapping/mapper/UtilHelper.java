package org.mtr.mapping.mapper;

import net.minecraft.util.Util;

import java.util.concurrent.Executor;

public final class UtilHelper {
    public static Executor getMainWorkerExecutor(){
        return Util.backgroundExecutor();
    }
}