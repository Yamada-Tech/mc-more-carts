package com.example.silentcart.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.example.silentcart.SilentCartMod;

/**
 * バニラのトロッコと同一性能（Minecart をそのまま継承）の静音トロッコ。
 *
 * サーバーサイド完結の実装:
 * - setSilent(true) で同期フラグ DATA_SILENT を立てる。バニラの音再生経路
 *   （クライアント側 MinecartSoundInstance / RidingEntitySoundInstance の canPlaySound、
 *   サーバー側 Entity#playSound）はいずれも isSilent() を参照するため、
 *   MOD 未導入（バニラ）クライアントでも走行音・乗車音・破壊音が一切鳴らない。
 * - PolymerEntity として、クライアントにはバニラの MINECART として表示する。
 */
public class SilentMinecartEntity extends Minecart implements PolymerEntity {
	public SilentMinecartEntity(EntityType<?> type, Level level) {
		super(type, level);
		// 音の完全抑制（同期データなので全クライアントに伝播する）
		this.setSilent(true);
	}

	@Override
	public EntityType<?> getPolymerEntityType(PacketContext context) {
		// クライアント（バニラクライアント含む）には普通のトロッコとして表示
		return EntityTypes.MINECART;
	}

	@Override
	protected Item getDropItem() {
		return SilentCartMod.SILENT_MINECART_ITEM;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(SilentCartMod.SILENT_MINECART_ITEM);
	}
}
