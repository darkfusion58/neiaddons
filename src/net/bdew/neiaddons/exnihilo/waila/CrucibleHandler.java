/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/neiaddons
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/neiaddons/master/MMPL-1.0.txt
 */

package net.bdew.neiaddons.exnihilo.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.bdew.neiaddons.exnihilo.AddonExnihilo;
import net.bdew.neiaddons.exnihilo.WailaHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.text.DecimalFormat;
import java.util.List;

public class CrucibleHandler implements IWailaDataProvider {
    private final DecimalFormat dec;

    public CrucibleHandler() {
        dec = new DecimalFormat("#,##0");
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        float solidVolume = tag.getFloat("solidVolume");
        float fluidVolume = tag.getFloat("fluidVolume");
        int contentID = tag.getInteger("contentID");
        int contentMeta = tag.getInteger("contentMeta");
        Fluid fluid = FluidRegistry.getFluid(tag.getShort("fluid"));

        if (fluid != null && fluidVolume > 0)
            currenttip.add(String.format("Fluid: %s %s mB", fluid.getLocalizedName(), dec.format(fluidVolume)));

        if (contentID > 0 && solidVolume > 0) {
            String itemname = Item.itemsList[contentID].getItemStackDisplayName(new ItemStack(contentID, 1, contentMeta));
            currenttip.add(String.format("Solid: %s %s", itemname, dec.format(solidVolume)));
        }

        try {
            currenttip.add(String.format("Speed: %s mB/t", WailaHandler.mCrucibleGetMeltSpeed.invoke(accessor.getTileEntity())));
        } catch (Throwable t) {
            AddonExnihilo.instance.logWarning("Failed to call getMeltSpeed: %s", t.toString());
            t.printStackTrace();
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }
}
