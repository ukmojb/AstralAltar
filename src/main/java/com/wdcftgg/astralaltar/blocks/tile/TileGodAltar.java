package com.wdcftgg.astralaltar.blocks.tile;

import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.crafting.AddedAbstractAltarRecipe;
import com.wdcftgg.astralaltar.crafting.AddedActiveCraftingTask;
import com.wdcftgg.astralaltar.crafting.AddedAltarRecipeRegistry;
import com.wdcftgg.astralaltar.init.RegistryStructures;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.PositionedLoopSound;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.change.ChangeSubscriber;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import hellfirepvp.astralsorcery.common.tile.ILiquidStarlightPowered;
import hellfirepvp.astralsorcery.common.tile.IMultiblockDependantTile;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.block.SimpleSingleFluidCapabilityTank;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

public class TileGodAltar extends TileReceiverBaseInventory implements IWandInteract, IMultiblockDependantTile, ILiquidStarlightPowered {

    private static final Random rand = new Random();
    private float posDistribution = -1.0F;
    private AddedActiveCraftingTask craftingTask = null;
    private Object clientCraftSound = null;
    private TileGodAltar.AltarLevel level;
    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch;
    private boolean multiblockMatches;
    private ItemStack focusItem;
    private boolean doesSeeSky;
    private int starlightStored;
    private static final int TANK_SIZE = Integer.MAX_VALUE;
    private SimpleSingleFluidCapabilityTank tank;


    public TileGodAltar() {
        super(42);
        this.inventorySize = 42;
        this.structureMatch = null;
        this.multiblockMatches = false;
        this.focusItem = ItemStack.EMPTY;
        this.doesSeeSky = false;
        this.starlightStored = 0;
        this.level = AltarLevel.GOD_CRAFT;

        tank = new SimpleSingleFluidCapabilityTank(TANK_SIZE, EnumFacing.DOWN);
        this.tank.setOnUpdate(this::markForUpdate);
    }

    @Override
    protected TileReceiverBaseInventory.ItemHandlerTile createNewItemHandler() {
        TileReceiverBaseInventory.ItemHandlerTile tile = new TileReceiverBaseInventory.ItemHandlerTileFiltered(this) {
            public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
                if (!super.canInsertItem(slot, toAdd, existing)) {
                    return false;
                } else {

                    int allowed = level.getAccessibleInventorySize();
                    return slot >= 0 && slot < allowed;
                }
            }
        };
        tile.setSize(42);

        return tile;
    }

    public void receiveStarlight(@Nullable IWeakConstellation type, double amount) {
        if (!(amount <= 0.001)) {
            this.starlightStored = Math.min(this.getMaxStarlightStorage(), (int)((double)this.starlightStored + amount * 200.0));
            this.markForUpdate();
        }
    }

    @Override
    public void update() {
        super.update();
        if ((this.ticksExisted & 15) == 0) {
            this.updateSkyState(MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, this.doesSeeSky));
        }

        if (!this.world.isRemote) {
            boolean needUpdate = false;
            this.matchStructure();
            needUpdate = this.starlightPassive(needUpdate);
            needUpdate = this.doTryCraft(needUpdate);
            if (needUpdate) {
                this.markForUpdate();
            }
        } else {
            if (this.getAddedActiveCraftingTask() != null) {
                this.doCraftEffects();
                this.doCraftSound();
            }

            if (this.getAltarLevel() != null && this.getAltarLevel().ordinal() >= TileGodAltar.AltarLevel.TRAIT_CRAFT.ordinal() && this.getMultiblockState()) {
//                System.out.println("asdaweawe");
                this.playAltarEffects();
            }
        }

    }

    public TileAltar.AltarLevel getAltarLevel() {
        return TileAltar.AltarLevel.TRAIT_CRAFT;
    }


    @SideOnly(Side.CLIENT)
    private void playAltarEffects() {
        if (Minecraft.isFancyGraphicsEnabled() && rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle((double)this.getPos().getX() + 0.5, (double)this.getPos().getY() + 4.4, (double)this.getPos().getZ() + 0.5);
            p.motion((double)(rand.nextFloat() * 0.03F * (float)(rand.nextBoolean() ? 1 : -1)), (double)(rand.nextFloat() * 0.03F * (float)(rand.nextBoolean() ? 1 : -1)), (double)(rand.nextFloat() * 0.03F * (float)(rand.nextBoolean() ? 1 : -1)));
            p.scale(0.15F).setColor(Color.WHITE).setMaxAge(25);
        }

    }

    @SideOnly(Side.CLIENT)
    private void doCraftSound() {
        if (Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0.0F) {
            if (this.clientCraftSound == null || ((PositionedLoopSound)this.clientCraftSound).hasStoppedPlaying()) {
                this.clientCraftSound = SoundHelper.playSoundLoopClient(Sounds.attunement, new Vector3(this), 0.25F, 1.0F, () -> {
                    return this.isInvalid() || Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) <= 0.0F || this.craftingTask == null;
                });
            }
        } else {
            this.clientCraftSound = null;
        }

    }

    @Nullable
    public IConstellation getFocusedConstellation() {
        return !this.focusItem.isEmpty() && this.focusItem.getItem() instanceof ItemConstellationFocus ? ((ItemConstellationFocus)this.focusItem.getItem()).getFocusConstellation(this.focusItem) : null;
    }

    @Nonnull
    public ItemStack getFocusItem() {
        return this.focusItem;
    }

    public void setFocusStack(@Nonnull ItemStack stack) {
        this.focusItem = stack;
        AstralAltar.Log(stack.getDisplayName());
        this.markForUpdate();
    }

    @Override
    public void onBreak() {
        super.onBreak();
        if (!this.world.isRemote && !this.focusItem.isEmpty()) {
            ItemUtils.dropItemNaturally(this.world, (double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5, this.focusItem);
            this.focusItem = ItemStack.EMPTY;
        }

    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB box = super.getRenderBoundingBox().expand(0.0, 10.0, 0.0);
        if (this.level != null) {
            box = box.grow(3.0, 0.0, 3.0);
        }

        return box;
    }

    @SideOnly(Side.CLIENT)
    private void doCraftEffects() {
        this.craftingTask.getRecipeToCraft().onCraftClientTick(this, this.craftingTask.getState(), ClientScheduler.getClientTick(), rand);
    }

    private void matchStructure() {
        PatternBlockArray structure = this.getRequiredStructure();
        if (structure != null && this.structureMatch == null) {
            this.structureMatch = PatternMatchHelper.getOrCreateMatcher(this.getWorld(), this.getPos(), structure);
        }

        boolean matches = structure == null || this.structureMatch.matches(this.getWorld());
        if (matches != this.multiblockMatches) {
            LogCategory.STRUCTURE_MATCH.info(() -> {
                return "Structure match updated: " + this.getClass().getName() + " at " + this.getPos() + " (" + this.multiblockMatches + " -> " + matches + ")";
            });
            this.multiblockMatches = matches;
            this.markForUpdate();
        }

    }

    private boolean doTryCraft(boolean needUpdate) {
        if (this.craftingTask == null) {
            return needUpdate;
        } else {
            AddedAbstractAltarRecipe altarRecipe = this.craftingTask.getRecipeToCraft();
            if (!this.doesRecipeMatch(altarRecipe, true)) {
                this.abortCrafting();
                return true;
            } else if (!altarRecipe.fulfillesStarlightRequirement(this)) {
                if (this.craftingTask.shouldPersist(this)) {
                    this.craftingTask.setState(AddedActiveCraftingTask.CraftingState.PAUSED);
                    return true;
                } else {
                    this.abortCrafting();
                    return true;
                }
            } else if (this.ticksExisted % 5 == 0 && this.matchDownNewMultiblocks(AltarLevel.GOD_CRAFT) == null) {
                this.abortCrafting();
                return true;
            } else if (this.craftingTask.isFinished()) {
                this.finishCrafting();
                return true;
            } else if (!this.craftingTask.tick(this)) {
                this.craftingTask.setState(AddedActiveCraftingTask.CraftingState.WAITING);
                return true;
            } else {
                AddedActiveCraftingTask.CraftingState prev = this.craftingTask.getState();
                this.craftingTask.setState(AddedActiveCraftingTask.CraftingState.ACTIVE);
                this.craftingTask.getRecipeToCraft().onCraftServerTick(this, AddedActiveCraftingTask.CraftingState.ACTIVE, this.craftingTask.getTicksCrafting(), this.craftingTask.getTotalCraftingTime(), rand);
                return prev != this.craftingTask.getState() || needUpdate;
            }
        }
    }

    private void finishCrafting() {
        if (this.craftingTask != null) {
            AddedAbstractAltarRecipe recipe = this.craftingTask.getRecipeToCraft();
            ShapeMap current = this.copyGetCurrentCraftingGrid();
            ItemStack out = recipe.getOutput(current, this);
            if (!out.isEmpty()) {
                out = ItemUtils.copyStackWithSize(out, out.getCount());
            }

            ForgeHooks.setCraftingPlayer(this.craftingTask.tryGetCraftingPlayerServer());
            recipe.handleInputConsumption(this, this.craftingTask, this.getInventoryHandler());
            ForgeHooks.setCraftingPlayer((EntityPlayer)null);
            if (!out.isEmpty() && !(out.getItem() instanceof ItemBlockAltar) && out.getCount() > 0) {
                ItemUtils.dropItem(this.world, (double)this.pos.getX() + 0.5, (double)this.pos.getY() + 1.3, (double)this.pos.getZ() + 0.5, out).setNoDespawn();
            }

            this.starlightStored = Math.max(0, this.starlightStored - recipe.getPassiveStarlightRequired());
            if (!recipe.allowsForChaining() || !this.doesRecipeMatch(recipe, false) || this.matchDownNewMultiblocks(AltarLevel.GOD_CRAFT) == null) {
                if (this.getAltarLevel().ordinal() >= TileAltar.AltarLevel.CONSTELLATION_CRAFT.ordinal()) {
                    Vector3 pos = (new Vector3(this.getPos())).add(0.5, 0.0, 0.5);
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CRAFT_FINISH_BURST, pos.getX(), pos.getY() + 0.05, pos.getZ());
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(this.getWorld(), this.getPos(), 32.0));
                }

                this.craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);
                if (!recipe.getOutputForMatching().isEmpty()) {
                    ItemStack match = recipe.getOutputForMatching();
                    if (match.getItem() instanceof ItemBlockAltar) {
                        TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[MathHelper.clamp(match.getItemDamage(), 0, TileAltar.AltarLevel.values().length - 1)];
//                        this.tryForceLevelUp(to, true);
                    }
                }

                SoundHelper.playSoundAround(Sounds.craftFinish, this.world, this.getPos(), 1.0F, 1.7F);
                EntityFlare.spawnAmbient(this.world, (new Vector3(this)).add((double)(-3.0F + rand.nextFloat() * 7.0F), 0.6, (double)(-3.0F + rand.nextFloat() * 7.0F)));
                this.craftingTask = null;
            }

            this.markForUpdate();
        }
    }

    public ShapeMap copyGetCurrentCraftingGrid() {
        ShapeMap current = new ShapeMap();

        for(int i = 0; i < 9; ++i) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.values()[i];
            ItemStack stack = this.getInventoryHandler().getStackInSlot(i);
            if (!stack.isEmpty()) {
                current.put(slot, new ItemHandle(ItemUtils.copyStackWithSize(stack, 1)));
            }
        }

        return current;
    }



    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    private void abortCrafting() {
        this.craftingTask = null;
        this.markForUpdate();
    }

    private boolean starlightPassive(boolean needUpdate) {
        if (this.starlightStored > 0) {
            needUpdate = true;
        }

        this.starlightStored = (int)((double)this.starlightStored * 0.95);
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(this.getWorld());
        if (this.doesSeeSky() && handle != null) {
            int yLevel = this.getPos().getY();
            if (yLevel > 40) {
                float collect = 160.0F;
                float dstr;
                if (yLevel > 120) {
                    dstr = 1.0F + (float)(yLevel - 120) / 272.0F;
                } else {
                    dstr = (float)(yLevel - 20) / 100.0F;
                }

                if (this.posDistribution == -1.0F) {
                    this.posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(this.world, this.pos);
                }

                collect *= dstr;
                collect = (float)((double)collect * (0.6 + 0.4 * (double)this.posDistribution));
                collect = (float)((double)collect * (0.2 + 0.8 * (double)ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(this.getWorld())));
                int num = Math.min(this.getMaxStarlightStorage(), (int)((float)this.starlightStored + collect));
                this.starlightStored = num;
                this.starlightStored = (int) Math.min(num + this.getMaxStarlightStorage() * 0.03, this.getMaxStarlightStorage());
                return true;
            }
        }

        return needUpdate;
    }

    @Nullable
    public AddedActiveCraftingTask getAddedActiveCraftingTask() {
        return this.craftingTask;
    }

    public boolean getMultiblockState() {
        return this.multiblockMatches;
    }

    @Override
    @Nullable
    public PatternBlockArray getRequiredStructure() {
        return RegistryStructures.patternAltarGod;
    }

    @Override
    @Nonnull
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    public float getAmbientStarlightPercent() {
        return (float)this.starlightStored / (float)this.getMaxStarlightStorage();
    }

    public int getStarlightStored() {
        return this.starlightStored;
    }

    public int getMaxStarlightStorage() {
        return TileAltar.AltarLevel.TRAIT_CRAFT.getStarlightMaxStorage() + AltarLevel.GOD_CRAFT.getStarlightMaxStorage();
    }

    public boolean doesRecipeMatch(AddedAbstractAltarRecipe recipe, boolean ignoreStarlightRequirement) {
        if (!recipe.getOutputForMatching().isEmpty()) {
            ItemStack match = recipe.getOutputForMatching();
            if (match.getItem() instanceof ItemBlockAltar) {
                TileGodAltar.AltarLevel to = TileGodAltar.AltarLevel.values()[MathHelper.clamp(match.getItemDamage(), 0, TileGodAltar.AltarLevel.values().length - 1)];
                if (this.getGodAltarLevel().ordinal() >= to.ordinal()) {
                    return false;
                }
            }
        }


        return recipe.matches(this, this.getInventoryHandler(), ignoreStarlightRequirement);
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneaking) {
        if (!world.isRemote) {
            if (this.getAddedActiveCraftingTask() != null) {
                AddedAbstractAltarRecipe altarRecipe = this.craftingTask.getRecipeToCraft();
                if (this.matchDownNewMultiblocks(AltarLevel.GOD_CRAFT) == null || !this.doesRecipeMatch(altarRecipe, false)) {
                    this.abortCrafting();
                    return;
                }
            }

            this.findRecipe(player);
        }

    }

    @Nullable
    public TileGodAltar.AltarLevel matchDownMultiblocks(TileGodAltar.AltarLevel levelDownTo) {
        return null;
    }

    public TileGodAltar.AltarLevel matchDownNewMultiblocks(TileGodAltar.AltarLevel levelDownTo) {
        for(int i = this.getGodAltarLevel().ordinal(); i >= levelDownTo.ordinal(); --i) {
            TileGodAltar.AltarLevel al = TileGodAltar.AltarLevel.values()[i];
            PatternBlockArray pattern = al.getPattern();
            if (pattern == null || pattern.matches(this.getWorld(), this.getPos())) {
                return al;
            }
        }

        return null;
    }

    private void findRecipe(EntityPlayer crafter) {
        if (this.craftingTask == null) {
            Object recipe = AddedAltarRecipeRegistry.findMatchingRecipe(this, false);
//            AddedAbstractAltarRecipe recipe = AddedAltarRecipeRegistry.findMatchingRecipe(this, false);
            if (!(recipe instanceof IGatedRecipe) || ((IGatedRecipe)recipe).hasProgressionServer(crafter)) {
//                if (recipe != null) {
                    if (recipe instanceof AddedAbstractAltarRecipe) {
                        AddedAbstractAltarRecipe recipe1 = (AddedAbstractAltarRecipe) recipe;
                        int divisor = Math.max(0, this.getAltarLevel().ordinal() - recipe1.getNeededLevel().ordinal());
                        divisor = (int) Math.round(Math.pow(2.0, (double) divisor));
                        this.craftingTask = new AddedActiveCraftingTask(recipe1, divisor, crafter.getUniqueID());
                        this.markForUpdate();
                    }
                    if (recipe instanceof AbstractAltarRecipe) {
                        AbstractAltarRecipe recipe1 = (AbstractAltarRecipe) recipe;
                        int divisor = Math.max(0, this.getAltarLevel().ordinal() - recipe1.getNeededLevel().ordinal());
                        divisor = (int) Math.round(Math.pow(2.0, (double) divisor));
                        this.craftingTask = new AddedActiveCraftingTask(recipe1, divisor, crafter.getUniqueID());
                        this.markForUpdate();
                    }
            }
        }
    }

    protected void updateSkyState(boolean seesSky) {
        boolean update = this.doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if (update) {
            this.markForUpdate();
        }

    }

    public boolean doesSeeSky() {
        return this.doesSeeSky;
    }

    public TileGodAltar.AltarLevel getGodAltarLevel() {
        return this.level;
    }

    public int getCraftingRecipeWidth() {
        return 3;
    }

    public int getCraftingRecipeHeight() {
        return 3;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.level = AltarLevel.GOD_CRAFT;
        this.starlightStored = compound.getInteger("starlight");
        this.multiblockMatches = compound.getBoolean("multiblockMatches");
        this.tank = SimpleSingleFluidCapabilityTank.deserialize(compound.getCompoundTag("tank"));
        if(!tank.hasCapability(EnumFacing.DOWN)) {
            tank.accessibleSides.add(EnumFacing.DOWN);
        }
        this.tank.setOnUpdate(this::markForUpdate);
        if (compound.hasKey("craftingTask")) {
            this.craftingTask = AddedActiveCraftingTask.deserialize(compound.getCompoundTag("craftingTask"), this.craftingTask);
        } else {
            this.craftingTask = null;
        }

        this.focusItem = this.getInventoryHandler().getStackInSlot(25);
//        if (compound.hasKey("focusItem")) {
//            this.focusItem = new ItemStack(compound.getCompoundTag("focusItem"));
//        }


    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setInteger("level", this.level.ordinal());
        compound.setInteger("starlight", this.starlightStored);
        compound.setBoolean("multiblockMatches", this.multiblockMatches);
        compound.setTag("tank", tank.writeNBT());
        if (!this.focusItem.isEmpty()) {
            ItemStack var10002 = this.focusItem;
            NBTHelper.setAsSubTag(compound, "focusItem", var10002::writeToNBT);
        }

        if (this.craftingTask != null) {
            compound.setTag("craftingTask", this.craftingTask.serialize());
        }

    }

    @Override
    @Nullable
    public String getUnLocalizedDisplayName() {
        return "tile.blockaltar.general.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TileGodAltar.TransmissionReceiverAltar(at);
    }

    public void onPlace(TileAltar.AltarLevel level) {
        this.level = AltarLevel.GOD_CRAFT;
        this.markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    public static void finishBurst(PktParticleEvent event) {
        EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone()).setPosition(event.getVec()).setScale((float)(5 + rand.nextInt(2))).setNoRotation((float)rand.nextInt(360));
    }

    public SimpleSingleFluidCapabilityTank getTank() {
        return tank;
    }

    @Override
    public boolean canAcceptStarlight(int mbLiquidStarlight) {
        return getHeldFluid() == null ||
                getFluidAmount() <= 0 ||
                (getHeldFluid() == BlocksAS.fluidLiquidStarlight &&
                        getFluidAmount() + mbLiquidStarlight <= TANK_SIZE);
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    @Nullable
    public Fluid getHeldFluid() {
        return tank.getTankFluid();
    }

    @Override
    public void acceptStarlight(int mbLiquidStarlight) {
        if(canAcceptStarlight(mbLiquidStarlight)) {
            getTank().fill(new FluidStack(BlocksAS.fluidLiquidStarlight, mbLiquidStarlight), true);
            markForUpdate();
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) tank.getCapability(facing);
        }
        return super.getCapability(capability, facing);
    }

    public static class AltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {
        public AltarReceiverProvider() {
        }

        public TileAltar.TransmissionReceiverAltar provideEmptyNode() {
            return new TileAltar.TransmissionReceiverAltar((BlockPos)null);
        }

        public String getIdentifier() {
            return "astralsorcery:TransmissionReceiverAltar";
        }
    }

    public static class TransmissionReceiverAltar extends SimpleTransmissionReceiver {
        public TransmissionReceiverAltar(BlockPos thisPos) {
            super(thisPos);
        }

        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if (isChunkLoaded) {
                TileAltar ta = (TileAltar)MiscUtils.getTileAt(world, this.getLocationPos(), TileAltar.class, false);
                if (ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }

        }

        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new TileGodAltar.AltarReceiverProvider();
        }
    }

    public static enum AltarLevel  {
        DISCOVERY(9, () -> {
            return null;
        }),
        ATTUNEMENT(13, () -> {
            return MultiBlockArrays.patternAltarAttunement;
        }),
        CONSTELLATION_CRAFT(21, () -> {
            return MultiBlockArrays.patternAltarConstellation;
        }),
        TRAIT_CRAFT(25, () -> {
            return MultiBlockArrays.patternAltarTrait;
        }),
        GOD_CRAFT(42, () -> {
//        GOD_CRAFT(41, () -> {
            return RegistryStructures.patternAltarGod;
        }),
        BRILLIANCE(25, () -> {
            return null;
        });

        private final int maxStarlightStorage;
        private final int accessibleInventorySize;
        private final Provider<PatternBlockArray> patternProvider;

        private AltarLevel(int invSize, Provider patternProvider) {
            this.patternProvider = patternProvider;
            this.accessibleInventorySize = invSize;
            this.maxStarlightStorage = (int)(1000.0 * Math.pow(2.0, (double)this.ordinal()));
        }

        public BlockAltar.AltarType getCorrespondingAltarType() {
            return BlockAltar.AltarType.values()[this.ordinal()];
        }

        @Nullable
        public PatternBlockArray getPattern() {
            return (PatternBlockArray)this.patternProvider.provide();
        }

        public int getStarlightMaxStorage() {
            return this.maxStarlightStorage;
        }

        public int getAccessibleInventorySize() {
            return this.accessibleInventorySize;
        }

        public BlockAltar.AltarType getType() {
            return BlockAltar.AltarType.values()[this.ordinal()];
        }

        public TileGodAltar.AltarLevel next() {
            return this;
        }
    }

    @Override
    public void markForUpdate() {
        IBlockState thisState = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, thisState, thisState, 3);
        this.markDirty();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared ()
    {
        return 65536.0D;
    }
}
