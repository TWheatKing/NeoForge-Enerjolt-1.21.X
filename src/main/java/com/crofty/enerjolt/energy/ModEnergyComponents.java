package com.crofty.enerjolt.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import com.crofty.enerjolt.Enerjolt;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

/**
 * Data Components for storing energy information in items
 */
public class ModEnergyComponents {
    public static final DeferredRegister<DataComponentType<?>> ENERGY_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Enerjolt.MOD_ID);

    // Simple energy storage component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            register("energy", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    // Advanced energy storage with tier and efficiency
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyData>> ENERGY_DATA =
            register("energy_data", builder -> builder.persistent(EnergyData.CODEC)
                    .networkSynchronized((StreamCodec<? super RegistryFriendlyByteBuf, EnergyData>) EnergyData.STREAM_CODEC));

    // Energy tier component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyTier>> ENERGY_TIER =
            register("energy_tier", builder -> builder.persistent(EnergyTier.CODEC)
                    .networkSynchronized((StreamCodec<? super RegistryFriendlyByteBuf, EnergyTier>) EnergyTier.STREAM_CODEC));

    // Energy configuration for tools/items
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyConfig>> ENERGY_CONFIG =
            register("energy_config", builder -> builder.persistent(EnergyConfig.CODEC)
                    .networkSynchronized((StreamCodec<? super RegistryFriendlyByteBuf, EnergyConfig>) EnergyConfig.STREAM_CODEC));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                           UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return ENERGY_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        ENERGY_COMPONENT_TYPES.register(eventBus);
    }

    /**
     * Comprehensive energy data storage for items
     */
    public record EnergyData(int energy, int capacity, EnergyTier tier, float efficiency) {
        public static final Codec<EnergyData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy").forGetter(EnergyData::energy),
                        ExtraCodecs.POSITIVE_INT.fieldOf("capacity").forGetter(EnergyData::capacity),
                        EnergyTier.CODEC.fieldOf("tier").forGetter(EnergyData::tier),
                        Codec.FLOAT.fieldOf("efficiency").forGetter(EnergyData::efficiency)
                ).apply(instance, EnergyData::new));

        // Fixed StreamCodec - specify the buffer type explicitly
        public static final StreamCodec<RegistryFriendlyByteBuf, EnergyData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, EnergyData::energy,
                ByteBufCodecs.VAR_INT, EnergyData::capacity,
                EnergyTier.STREAM_CODEC, EnergyData::tier,
                ByteBufCodecs.FLOAT, EnergyData::efficiency,
                EnergyData::new);

        public EnergyData withEnergy(int newEnergy) {
            return new EnergyData(Math.max(0, Math.min(capacity, newEnergy)), capacity, tier, efficiency);
        }

        public EnergyData withEfficiency(float newEfficiency) {
            return new EnergyData(energy, capacity, tier, Math.max(0.1f, Math.min(1.0f, newEfficiency)));
        }

        public float getStoredPercentage() {
            return capacity > 0 ? (float) energy / capacity : 0;
        }

        public boolean isEmpty() {
            return energy == 0;
        }

        public boolean isFull() {
            return energy >= capacity;
        }
    }

    /**
     * Energy configuration for tools and machines
     */
    public record EnergyConfig(int maxReceive, int maxExtract, boolean canReceive, boolean canExtract,
                               boolean hasLoss, int consumeRate) {
        public static final Codec<EnergyConfig> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxReceive").forGetter(EnergyConfig::maxReceive),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxExtract").forGetter(EnergyConfig::maxExtract),
                        Codec.BOOL.fieldOf("canReceive").forGetter(EnergyConfig::canReceive),
                        Codec.BOOL.fieldOf("canExtract").forGetter(EnergyConfig::canExtract),
                        Codec.BOOL.fieldOf("hasLoss").forGetter(EnergyConfig::hasLoss),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("consumeRate").forGetter(EnergyConfig::consumeRate)
                ).apply(instance, EnergyConfig::new));

        // Fixed StreamCodec - specify the buffer type explicitly
        public static final StreamCodec<RegistryFriendlyByteBuf, EnergyConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, EnergyConfig::maxReceive,
                ByteBufCodecs.VAR_INT, EnergyConfig::maxExtract,
                ByteBufCodecs.BOOL, EnergyConfig::canReceive,
                ByteBufCodecs.BOOL, EnergyConfig::canExtract,
                ByteBufCodecs.BOOL, EnergyConfig::hasLoss,
                ByteBufCodecs.VAR_INT, EnergyConfig::consumeRate,
                EnergyConfig::new);

        public static EnergyConfig createDefault(EnergyTier tier) {
            int transfer = tier.getVoltage();
            return new EnergyConfig(transfer, transfer, true, true, tier != EnergyTier.CREATIVE, tier.getVoltage() / 10);
        }

        public static EnergyConfig createTool(EnergyTier tier, int consumeRate) {
            return new EnergyConfig(tier.getVoltage(), 0, true, false, true, consumeRate);
        }

        public static EnergyConfig createGenerator(EnergyTier tier, int generateRate) {
            return new EnergyConfig(0, generateRate, false, true, false, 0);
        }

        public static EnergyConfig createStorage(EnergyTier tier) {
            int transfer = tier.getVoltage() * 4;
            return new EnergyConfig(transfer, transfer, true, true, tier != EnergyTier.CREATIVE, 0);
        }
    }
}