package com.wdcftgg.astralaltar.crafting;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class AddedActiveCraftingTask{

    private Map<Integer, Object> clientEffectContainer = new HashMap<>();

    private final AddedAbstractAltarRecipe recipeToCraft;
    private final AbstractAltarRecipe recipeToCraft1;
    private final UUID playerCraftingUUID;
    private int ticksCrafting = 0;
    private int totalCraftingTime;

    private CraftingState state;
    private NBTTagCompound craftingData = new NBTTagCompound();

    private AddedActiveCraftingTask(AddedAbstractAltarRecipe recipeToCraft, UUID playerCraftingUUID) {
        this(recipeToCraft, 1, playerCraftingUUID);
    }

    public AddedActiveCraftingTask(AddedAbstractAltarRecipe recipeToCraft, int durationDivisor, UUID playerCraftingUUID) {
        Objects.requireNonNull(recipeToCraft);

        this.recipeToCraft = recipeToCraft;
        this.recipeToCraft1 = null;
        this.playerCraftingUUID = playerCraftingUUID;
        this.state = CraftingState.ACTIVE;
        this.totalCraftingTime = recipeToCraft.craftingTickTime() / durationDivisor;
    }

    public AddedActiveCraftingTask(AbstractAltarRecipe recipeToCraft, int durationDivisor, UUID playerCraftingUUID) {
        Objects.requireNonNull(recipeToCraft);

        this.recipeToCraft = null;
        this.recipeToCraft1 = recipeToCraft;
        this.playerCraftingUUID = playerCraftingUUID;
        this.state = CraftingState.ACTIVE;
        this.totalCraftingTime = recipeToCraft.craftingTickTime() / durationDivisor;
    }

    private void attemptRecoverEffects(@Nullable AddedActiveCraftingTask previous) {
        if (previous != null && previous.recipeToCraft.getUniqueRecipeId() == this.recipeToCraft.getUniqueRecipeId()) {
            this.clientEffectContainer.putAll(previous.clientEffectContainer);
        }
    }

    public CraftingState getState() {
        return state;
    }

    public NBTTagCompound getCraftingData() {
        return craftingData;
    }

    public void setState(CraftingState state) {
        this.state = state;
    }

    public boolean shouldPersist(TileGodAltar ta) {
        return recipeToCraft instanceof GodRecipe || ta.getGodAltarLevel().ordinal() >= TileGodAltar.AltarLevel.TRAIT_CRAFT.ordinal();
    }

    public UUID getPlayerCraftingUUID() {
        return playerCraftingUUID;
    }

    @Nullable
    public EntityPlayer tryGetCraftingPlayerServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerCraftingUUID);
    }

    @SideOnly(Side.CLIENT)
    public <T> T getEffectContained(int index, Function<Integer, T> provider) {
        return (T) clientEffectContainer.computeIfAbsent(index, provider);
    }

    //True if the recipe progressed, false if it's stuck
    public boolean tick(TileGodAltar altar) {
        if(recipeToCraft instanceof IAddedCraftingProgress) {
            if (!((IAddedCraftingProgress) recipeToCraft).tryProcess(altar, this, craftingData, ticksCrafting, totalCraftingTime)) {
                return false;
            }
        }
        ticksCrafting++;
        return true;
    }

    public int getTicksCrafting() {
        return ticksCrafting;
    }

    public int getTotalCraftingTime() {
        return totalCraftingTime;
    }

    public AddedAbstractAltarRecipe getRecipeToCraft() {
        return recipeToCraft;
    }

    public boolean isFinished() {
        return ticksCrafting >= totalCraftingTime;
    }

    @Nullable
    public static AddedActiveCraftingTask deserialize(NBTTagCompound compound, @Nullable AddedActiveCraftingTask previous) {
        int recipeId = compound.getInteger("recipeId");
        AddedAbstractAltarRecipe recipe = (AddedAbstractAltarRecipe) AddedAltarRecipeRegistry.getRecipe(recipeId);

        if(recipe == null) {
            AstralSorcery.log.info("Recipe with unknown/invalid ID found: " + recipeId);
            return null;
        } else {
            UUID uuidCraft = compound.getUniqueId("crafterUUID");
            int tick = compound.getInteger("recipeTick");
            int total = compound.getInteger("totalCraftingTime");
            CraftingState state = CraftingState.values()[compound.getInteger("craftingState")];
            AddedActiveCraftingTask task = new AddedActiveCraftingTask(recipe, uuidCraft);
            task.ticksCrafting = tick;
            task.totalCraftingTime = total;
            task.setState(state);
            task.craftingData = compound.getCompoundTag("craftingData");
            task.attemptRecoverEffects(previous);
            return task;
        }
    }

    @Nonnull
    public NBTTagCompound serialize() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("recipeId", getRecipeToCraft().getUniqueRecipeId());
        compound.setInteger("recipeTick", getTicksCrafting());
        compound.setInteger("totalCraftingTime", getTotalCraftingTime());
        compound.setUniqueId("crafterUUID", getPlayerCraftingUUID());
        compound.setInteger("craftingState", getState().ordinal());
        compound.setTag("craftingData", craftingData);
        return compound;
    }

    public static enum CraftingState {

        ACTIVE, //All valid, continuing to craft.

        WAITING, //Potentially waiting for user interaction. Recipe itself is fully valid.

        PAUSED //Something of the recipe is not valid, waiting with continuation; nothing user-related.

    }

}
