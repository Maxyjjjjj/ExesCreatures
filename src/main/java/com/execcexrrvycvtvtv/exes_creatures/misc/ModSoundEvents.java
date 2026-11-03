package com.execcexrrvycvtvtv.exes_creatures.misc;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSoundEvents {
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, ExesCreatures.MODID);

    public static final Supplier<SoundEvent> SAOLA_AMBIENT = register("entity.saola.ambient");
    public static final Supplier<SoundEvent> SAOLA_HURT = register("entity.saola.hurt");
    public static final Supplier<SoundEvent> SAOLA_DEATH = register("entity.saola.death");

    public static final Supplier<SoundEvent> SERVAL_AMBIENT = register("entity.serval.ambient");
    public static final Supplier<SoundEvent> SERVAL_HURT = register("entity.serval.hurt");
    public static final Supplier<SoundEvent> SERVAL_DEATH = register("entity.serval.death");

    public static final Supplier<SoundEvent> JAY_AMBIENT = register("entity.jay.ambient");
    public static final Supplier<SoundEvent> JAY_HURT = register("entity.jay.hurt");
    public static final Supplier<SoundEvent> JAY_DEATH = register("entity.jay.death");

    public static final Supplier<SoundEvent> MONKEY_AMBIENT = register("entity.monkey.ambient");
    public static final Supplier<SoundEvent> MONKEY_HURT = register("entity.monkey.hurt");
    public static final Supplier<SoundEvent> MONKEY_DEATH = register("entity.monkey.death");

    public static final Supplier<SoundEvent> PHILIPPINE_EAGLE_AMBIENT = register("entity.philippine_eagle.ambient");
    public static final Supplier<SoundEvent> PHILIPPINE_EAGLE_HURT = register("entity.philippine_eagle.hurt");
    public static final Supplier<SoundEvent> PHILIPPINE_EAGLE_DEATH = register("entity.philippine_eagle.death");

    private static Supplier<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, SoundEvent::createVariableRangeEvent);
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
