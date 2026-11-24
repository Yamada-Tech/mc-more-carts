package com.example.silentcart;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.example.silentcart.entity.SilentMinecartEntity;
import com.example.silentcart.item.SilentMinecartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SilentCartMod implements ModInitializer {
	public static final String MOD_ID = "silent-cart";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// --- Entity ---
	public static final ResourceKey<EntityType<?>> SILENT_MINECART_TYPE_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, id("silent_minecart"));

	// バニラ MINECART と同一のパラメータ（sized 0.98x0.7 / passengerAttachments 0.1875 / tracking 8）
	public static final EntityType<SilentMinecartEntity> SILENT_MINECART =
			EntityType.Builder.<SilentMinecartEntity>of(SilentMinecartEntity::new, MobCategory.MISC)
					.noLootTable()
					.sized(0.98f, 0.7f)
					.passengerAttachments(0.1875f)
					.clientTrackingRange(8)
					.build(SILENT_MINECART_TYPE_KEY);

	// --- Item ---
	public static final ResourceKey<Item> SILENT_MINECART_ITEM_KEY =
			ResourceKey.create(Registries.ITEM, id("silent_minecart"));

	// クライアントにはバニラトロッコ + カスタムアイテムモデル（silent-cart:item/silent_minecart）として見える
	public static final Item SILENT_MINECART_ITEM = new SilentMinecartItem(
			SILENT_MINECART,
			new Item.Properties()
					.stacksTo(1)
					.setId(SILENT_MINECART_ITEM_KEY)
					.component(DataComponents.ITEM_MODEL, id("silent_minecart"))
	);

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ENTITY_TYPE, SILENT_MINECART_TYPE_KEY, SILENT_MINECART);
		Registry.register(BuiltInRegistries.ITEM, SILENT_MINECART_ITEM_KEY, SILENT_MINECART_ITEM);

		// エンティティ型をサーバー専用としてマーク（バニラクライアントには送らずバニラ minecart に置換）
		PolymerEntityUtils.registerType(SILENT_MINECART);

		// Polymer 自動配信リソースパックに本 MOD のアセット（テクスチャ/lang）を含める
		if (FabricLoader.getInstance().isModLoaded("polymer-autohost")) {
			PolymerResourcePackUtils.addModAssets(MOD_ID);
		}

		// クリエイティブタブ: バニラのトロッコの直後に挿入（バニラトロッコを含まないタブでは何もしない）
		CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((tab, output) ->
				output.insertAfter(Items.MINECART, new ItemStack(SILENT_MINECART_ITEM)));

		LOGGER.info("Silent Cart registered (Polymer server-side): {}", SILENT_MINECART_TYPE_KEY.identifier());
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
