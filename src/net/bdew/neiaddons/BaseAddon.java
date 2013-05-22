/**
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/neiaddons
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/neiaddons/master/MMPL-1.0.txt
 */

package net.bdew.neiaddons;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.bdew.neiaddons.api.NEIAddon;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.Side;

public abstract class BaseAddon implements NEIAddon {

    protected Boolean active = false;
    protected static Logger log;

    @Override
    public final Boolean isActive() {
        return active;
    }

    public final static void logInfo(String message, Object... params) {
        log.log(Level.INFO, String.format(message, params));
    }

    public final static void logWarning(String message, Object... params) {
        log.log(Level.WARNING, String.format(message, params));
    }

    /**
     * @return Array of version dependencies, format used by FML {@link VersionParser}
     */
    public String[] getDependencies() {
        return new String[0];
    }

    /**
     * @return true to continue loading
     */
    public boolean checkSide(Side side) {
        return true;
    }

    public abstract void preInit(FMLPreInitializationEvent ev);

    public final void doPreInit(FMLPreInitializationEvent ev) {
        log = ev.getModLog();
        if (!checkSide(ev.getSide())) {
            logInfo("Wrong side: %s, %s Addon not loading", ev.getSide().toString(), getName());
            return;
        }

        for (String dep : getDependencies()) {
            if (!verifyModVersion(dep)) {
                logWarning("Requirements unmet, %s Addon not loading", getName());
                return;
            }
        }

        NEIAddons.register(this);
    }

    protected Boolean verifyModVersion(String spec) {
        ArtifactVersion req = VersionParser.parseVersionReference(spec);
        String modid = req.getLabel();

        Map<String, ModContainer> modlist = Loader.instance().getIndexedModList();

        if (!modlist.containsKey(modid)) {
            logInfo("Required mod %s is not installed, dependent features will be unavailable", req.getLabel());
            return false;
        }

        ArtifactVersion found = modlist.get(modid).getProcessedVersion();

        if (found == null) {
            logInfo("Unable to determine version of required mod %s, dependent features will be unavailable", req.getLabel());
            return false;
        }

        if (!req.containsVersion(found)) {
            logInfo("Version mismatch: %s is required while %s was detected, dependent features will be unavailable", req.toString(), found.getVersionString());
            return false;
        }

        logInfo("Version check success: %s required / %s detected", req.toString(), found.getVersionString());

        return true;
    }

}