package de.fatzzke.fattyoven;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.fatzzke.blocks.OvenBlock;
import de.fatzzke.entities.OvenBlockEnity;
import de.fatzzke.inventory.OvenInventory;
import de.fatzzke.items.EnergySticker;
import de.fatzzke.items.GoldSticker;
import de.fatzzke.items.OvenBlockItem;
import de.fatzzke.items.UpgradeSticker;
import de.fatzzke.items.UpgradigerSticker;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FattysOven.MODID)
public class FattysOven {
        // Define mod id in a common place for everything to reference
        public static final String MODID = "fattysoven";
        // Directly reference a slf4j logger
        public static final Logger LOGGER = LogUtils.getLogger();
        // Create a Deferred Register to hold Blocks which will all be registered under
        // the "fattysoven" namespace
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
        // Create a Deferred Register to hold Items which will all be registered under
        // the "fattysoven" namespace
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
        // Create a Deferred Register to hold CreativeModeTabs which will all be
        // registered under the "fattysoven" namespace
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
                        .create(Registries.CREATIVE_MODE_TAB, MODID);

        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
                        .create(Registries.BLOCK_ENTITY_TYPE, MODID);

        public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU,
                        MODID);

        public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister
                        .create(BuiltInRegistries.SOUND_EVENT, MODID);

        public static final DeferredBlock<OvenBlock> OVEN_BLOCK = BLOCKS.registerBlock(
                        "oven_block",
                        OvenBlock::new, // The factory that the properties will be passed into.
                        BlockBehaviour.Properties.of().noOcclusion() // The properties to use.
        );

        public static final Supplier<MenuType<OvenInventory>> OVEN_INVENTORY = CONTAINERS.register("oven_inventory",
                        () -> IMenuTypeExtension.create(OvenInventory::new));

        public static final DeferredItem<OvenBlockItem> OVEN_BLOCK_ITEM = ITEMS.register("oven_block",
                        () -> new OvenBlockItem(OVEN_BLOCK.get(), new Item.Properties()));

        public static final Supplier<BlockEntityType<OvenBlockEnity>> OVEN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
                        "oven_block_entity",
                        () -> BlockEntityType.Builder.of(OvenBlockEnity::new, OVEN_BLOCK.get()).build(null));

        public static final DeferredItem<UpgradeSticker> UPGRADE_ITEM = ITEMS.register("upgrade_sticker",
                        () -> new UpgradeSticker(new Item.Properties().stacksTo(1)));

        public static final DeferredItem<GoldSticker> GOLD_ITEM = ITEMS.register("gold_sticker",
                        () -> new GoldSticker(new Item.Properties().stacksTo(1)));

        public static final DeferredItem<EnergySticker> ENERGY_ITEM = ITEMS.register("energy_sticker",
                        () -> new EnergySticker(new Item.Properties().stacksTo(1)));

        public static final DeferredItem<UpgradigerSticker> UPGRADIGER_ITEM = ITEMS.register("upgradiger_sticker",
                        () -> new UpgradigerSticker(new Item.Properties().stacksTo(1)));

        public static final Holder<SoundEvent> OVEN_OPEN_SOUND = SOUND_EVENTS.register(
                        "oven_open",
                        SoundEvent::createVariableRangeEvent);

        public static final Holder<SoundEvent> OVEN_DONE_SOUND = SOUND_EVENTS.register(
                        "oven_done",
                        SoundEvent::createVariableRangeEvent);

        public static final Holder<SoundEvent> OVEN_CLOSE_SOUND = SOUND_EVENTS.register(
                        "oven_close",
                        SoundEvent::createVariableRangeEvent);

        // Creates a creative tab with the id "fattysoven:example_tab" for the example
        // item, that is placed after the combat tab
        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS
                        .register("example_tab", () -> CreativeModeTab.builder()
                                        .title(Component.translatable("itemGroup.fattysoven")) // The language key for
                                                                                               // the title of your
                                                                                               // CreativeModeTab
                                        .withTabsBefore(CreativeModeTabs.COMBAT)
                                        .icon(() -> UPGRADE_ITEM.get().getDefaultInstance())
                                        .displayItems((parameters, output) -> {
                                                // Add the example item to the tab.
                                                // For your own tabs, this
                                                // method is preferred over the event
                                                output.accept(OVEN_BLOCK_ITEM.get());
                                                output.accept(UPGRADE_ITEM.get());
                                                output.accept(GOLD_ITEM.get());
                                                output.accept(ENERGY_ITEM.get());
                                                output.accept(UPGRADIGER_ITEM.get());
                                        }).build());

        // The constructor for the mod class is the first code that is run when your mod
        // is loaded.
        // FML will recognize some parameter types like IEventBus or ModContainer and
        // pass them in automatically.
        public FattysOven(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
                // Register the commonSetup method for modloading
                modEventBus.addListener(this::commonSetup);

                // Register the Deferred Register to the mod event bus so blocks get registered
                BLOCKS.register(modEventBus);
                // Register the Deferred Register to the mod event bus so items get registered
                ITEMS.register(modEventBus);
                // Register the Deferred Register to the mod event bus so tabs get registered
                CREATIVE_MODE_TABS.register(modEventBus);

                BLOCK_ENTITY_TYPES.register(modEventBus);

                CONTAINERS.register(modEventBus);

                SOUND_EVENTS.register(modEventBus);

                // Register ourselves for server and other game events we are interested in.
                // Note that this is necessary if and only if we want *this* class (FattysOven)
                // to respond directly to events.
                // Do not add this line if there are no @SubscribeEvent-annotated functions in
                // this class, like onServerStarting() below.
                NeoForge.EVENT_BUS.register(this);

                // Register the item to a creative tab
                modEventBus.addListener(this::addCreative);

                modEventBus.addListener(this::addCapabilities);

                // Register our mod's ModConfigSpec so that FML can create and load the config
                // file for us
                modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

                if (dist.isClient()) {
                        modEventBus.addListener(FattysOvenClient::setupMenus);
                }
        }

        private void commonSetup(FMLCommonSetupEvent event) {
                // Some common setup code
                LOGGER.info("HELLO FROM COMMON SETUP!");

        }

        // Add the example block item to the building blocks tab
        private void addCreative(BuildCreativeModeTabContentsEvent event) {
                if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
                        event.accept(OVEN_BLOCK_ITEM);
                }
        }

        private void addCapabilities(RegisterCapabilitiesEvent event) {
                event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, OVEN_BLOCK_ENTITY.get(),
                                (myBlockEntity, side) -> myBlockEntity.getEnergyStorage(side));
                event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, OVEN_BLOCK_ENTITY.get(),
                                (myBlockEntity, side) -> myBlockEntity.getInventory());
        }

        // You can use SubscribeEvent and let the Event Bus discover methods to call
        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
                // Do something when the server starts
                LOGGER.info("HELLO from server starting");
        }
}
