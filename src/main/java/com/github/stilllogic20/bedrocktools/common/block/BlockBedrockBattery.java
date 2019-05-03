package com.github.stilllogic20.bedrocktools.common.block;

import com.github.stilllogic20.bedrocktools.common.energy.BedrockStorageBase;

public class BlockBedrockBattery extends BedrockStorageBase {

    protected BlockBedrockBattery(long capacity) {
        super(Integer.MAX_VALUE * 4L);
    }

}
