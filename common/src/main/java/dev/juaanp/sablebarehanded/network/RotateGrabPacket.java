package dev.juaanp.sablebarehanded.network;

import dev.juaanp.sablebarehanded.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RotateGrabPacket(double deltaX, double deltaY) implements CustomPacketPayload {

    public static final Type<RotateGrabPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "rotate_grab"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RotateGrabPacket> CODEC = StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.DOUBLE, RotateGrabPacket::deltaX,
            net.minecraft.network.codec.ByteBufCodecs.DOUBLE, RotateGrabPacket::deltaY,
            RotateGrabPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}