package com.bitqba;

import com.bitqba.block.ModBlocks;
import com.bitqba.item.ModItemGroups;
import com.bitqba.item.ModItems;
import com.bitqba.network.ModNetworking;
import com.bitqba.screen.ModScreenHandlers;
import com.bitqba.storage.ArchiveCache;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcanumStorageMod implements ModInitializer {
    public static final String MOD_ID = "arcanum_storage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModItemGroups.register();
        ModScreenHandlers.register();
        ModNetworking.register();
        ArchiveCache.register();
        LOGGER.info("Arcanum Storage initialized");
    }
}
