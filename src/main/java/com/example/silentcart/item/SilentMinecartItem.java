package com.example.silentcart.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;

/**
 * 静音トロッコの配置アイテム。サーバー側では SilentMinecartEntity をスポーンさせ、
 * クライアントには「バニラのトロッコ」+ ITEM_MODEL コンポーネント（カスタムモデル）として見える。
 * （モデル・テクスチャは Polymer のリソースパック配信でバニラクライアントにも届く）
 */
public class SilentMinecartItem extends MinecartItem implements PolymerItem {
	public SilentMinecartItem(EntityType<? extends AbstractMinecart> type, Properties properties) {
		super(type, properties);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
		return Items.MINECART;
	}
}
