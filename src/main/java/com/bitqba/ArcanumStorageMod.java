package com.bitqba;

import com.bitqba.block.ModBlocks;
import com.bitqba.item.ModItemGroups;
import com.bitqba.item.ModItems;
import com.bitqba.ledger.LedgerCommand;
import com.bitqba.ledger.LedgerContentLoader;
import com.bitqba.ledger.LedgerService;
import com.bitqba.network.ModNetworking;
import com.bitqba.recipe.ModRecipes;
import com.bitqba.screen.ModScreenHandlers;
import com.bitqba.storage.ArchiveCache;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcanumStorageMod implements ModInitializer {
    public static final String MOD_ID = "arcanum_storage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModRecipes.register();
        ModItemGroups.register();
        ModScreenHandlers.register();
        ModNetworking.register();
        ArchiveCache.register();
        LedgerService.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                LedgerCommand.register(dispatcher));
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(
                Identifier.of(MOD_ID, "ledger_content"), new LedgerContentLoader());
        LOGGER.info("Arcanum Storage initialized");
    }
}
