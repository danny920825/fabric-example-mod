package com.bitqba.ledger;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;

/** Keeps the ledger content in sync with datapacks and {@code /reload}. */
public final class LedgerContentLoader implements SynchronousResourceReloader {
    @Override
    public void reload(ResourceManager manager) {
        LedgerContent.set(LedgerContent.loadForReload(manager));
    }
}
