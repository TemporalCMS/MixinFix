package dev.skogrine.mixinfix.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BuildCreativeModeTabContentsEvent.class)
public class BuildCreativeModeTabContentsEventMixin {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("CreativeTabDuplicateFix");

    @WrapMethod(
            method = "accept(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)V"
    )
    private void mixinfix$ignoreDuplicates(ItemStack stack,
                                           CreativeModeTab.TabVisibility visibility,
                                           Operation<Void> original) {
        try {
            original.call(stack, visibility);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Doublon d'onglet creatif ignore: {}", e.getMessage());
        }
    }
}
