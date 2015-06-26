package com.scottkillen.mod.dendrology.compat.chisel;

import com.cricketcraft.chisel.api.carving.CarvableHelper;
import com.cricketcraft.chisel.api.carving.CarvingUtils;
import com.cricketcraft.chisel.api.carving.ICarvingRegistry;
import com.scottkillen.mod.dendrology.TheMod;
import com.scottkillen.mod.dendrology.config.Settings;
import com.scottkillen.mod.dendrology.content.overworld.OverworldTreeSpecies;
import com.scottkillen.mod.koresample.compat.Integrator;
import com.scottkillen.mod.koresample.tree.block.WoodBlock;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public final class ChiselMod extends Integrator
{
    private static final String MOD_ID = "chisel";
    private static final String MOD_NAME = "Chisel 2";

    @Method(modid = MOD_ID)
    private static void preInit()
    {
        loadBlocks();
    }

    private static void assignAttributes(ChiselWoodBlock chiselWoodBlock)
    {
        OreDictionary.registerOre("plankWood", new ItemStack(chiselWoodBlock, 1, WILDCARD_VALUE));
        chiselWoodBlock.setCreativeTab(getChiselCreativeTab());
        Blocks.fire.setFireInfo(chiselWoodBlock, 5, 20);
    }

    @Method(modid = MOD_ID)
    private static void finalizeVariationGroup(WoodBlock woodBlock, int subBlockIndex, String variationGroupName)
    {
        final ICarvingRegistry chisel = CarvingUtils.getChiselRegistry();
        chisel.addVariation(variationGroupName, woodBlock, subBlockIndex, 0);
        chisel.setVariationSound(variationGroupName, MOD_ID + ":chisel.wood");
    }

    private static CreativeTabs getChiselCreativeTab()
    {
        for (final CreativeTabs tab : CreativeTabs.creativeTabArray)
            if ("tabWoodChiselBlocks".equals(tab.getTabLabel())) return tab;

        return TheMod.INSTANCE.creativeTab();
    }

    @Method(modid = MOD_ID)
    private static void loadBlocks()
    {
        for (final OverworldTreeSpecies species : OverworldTreeSpecies.values())
        {
            final String speciesName = species.speciesName();
            final String variationGroupName = String.format("%s%s", speciesName, "_planks");
            final ChiselWoodBlock chiselWoodBlock = newChiselWoodBlock(speciesName);

            registerVariations(variationGroupName, chiselWoodBlock);
            finalizeVariationGroup(species.woodBlock(), species.woodSubBlockIndex(), variationGroupName);
            assignAttributes(chiselWoodBlock);
        }
    }

    private static ChiselWoodBlock newChiselWoodBlock(String speciesName) {return new ChiselWoodBlock(speciesName);}

    @Method(modid = MOD_ID)
    private static void registerVariations(String variationGroupName, ChiselWoodBlock chiselWoodBlock)
    {
        final CarvableHelper carvableHelper = new CarvableHelper(chiselWoodBlock);

        for (int i = 0; i < 15; i++)
            carvableHelper.addVariation(chiselWoodBlock.getVariationName(i), i, chiselWoodBlock);

        carvableHelper.registerAll(chiselWoodBlock, variationGroupName);
    }

    @Override
    public void doIntegration(ModState modState)
    {
        if (Loader.isModLoaded(MOD_ID)&& Settings.INSTANCE.integrateChisel())
        {
            switch (modState)
            {
                case PREINITIALIZED:
                    preInit();
                    break;
                default:
            }
        }
    }

    @Override
    protected String modID() { return MOD_ID; }

    @Override
    protected String modName() { return MOD_NAME; }
}
