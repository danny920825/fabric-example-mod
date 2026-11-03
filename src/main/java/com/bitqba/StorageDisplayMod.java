package com.bitqba;

import com.bitqba.block.ModBlocks;
import com.bitqba.network.ModNetworking;
import com.bitqba.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageDisplayMod implements ModInitializer {
    public static final String MOD_ID = "storage_display";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModScreenHandlers.register();
        ModNetworking.register();
        LOGGER.info("Storage Display initialized");
    }
}
