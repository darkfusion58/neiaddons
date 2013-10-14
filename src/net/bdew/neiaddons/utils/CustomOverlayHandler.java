/**
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/neiaddons
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/neiaddons/master/MMPL-1.0.txt
 */

package net.bdew.neiaddons.utils;

import java.util.List;

import net.bdew.neiaddons.PacketHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import codechicken.nei.LayoutManager;
import codechicken.nei.OffsetPositioner;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.IRecipeHandler;

public class CustomOverlayHandler implements IOverlayHandler {
    private boolean invert;
    private String command;
    private int xOffs, yOffs;
    
    public CustomOverlayHandler(String command, int xOffs, int yOffs, boolean invert) {
        super();
        this.command = command;
        this.xOffs = xOffs;
        this.yOffs = yOffs;
        this.invert = invert;
    }

    @Override
    public void overlayRecipe(GuiContainer cont, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        List<PositionedStack> ingr = recipe.getIngredientStacks(recipeIndex);

        if (invert) {
            shift = !shift;
        }

        if (!shift) {
            IStackPositioner positioner = new OffsetPositioner(xOffs, yOffs);
            IRecipeOverlayRenderer renderer = new DefaultOverlayRenderer(ingr, positioner);
            LayoutManager.overlayRenderer = renderer;
        } else {
            NBTTagList stacksnbt = new NBTTagList();

            for (int i = 0; i < ingr.size(); i++) {

                if (ingr.get(i) != null) {
                    // This is back-asswards but i don't see a better way :(
                    int x = (ingr.get(i).relx - 25) / 18;
                    int y = (ingr.get(i).rely - 6) / 18;

                    ItemStack stack = ingr.get(i).items[0];
                    NBTTagCompound stacknbt = stack.writeToNBT(new NBTTagCompound());
                    stacknbt.setInteger("slot", y * 3 + x);
                    stacksnbt.appendTag(stacknbt);
                }

                ItemStack stack = recipe.getResultStack(recipeIndex).items[0];
                NBTTagCompound stacknbt = stack.writeToNBT(new NBTTagCompound());
                stacknbt.setInteger("slot", 9);
                stacksnbt.appendTag(stacknbt);
            }

            NBTTagCompound data = new NBTTagCompound();
            data.setTag("stacks", stacksnbt);

            PacketHelper.send(command, data);
        }
    }
}