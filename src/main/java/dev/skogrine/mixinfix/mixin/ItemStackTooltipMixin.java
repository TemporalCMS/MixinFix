package dev.skogrine.mixinfix.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {

    @Unique
    private static final Logger mixinFix$LOGGER = LoggerFactory.getLogger("MixinFix");

    @Unique
    private static final Set<String> ALREADY_LOGGED = ConcurrentHashMap.newKeySet();

    @WrapMethod(
            method = "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;"
    )
    private List<Component> mixinfix$guardTooltip(Item.TooltipContext context,
                                                  Player player,
                                                  TooltipFlag flag,
                                                  Operation<List<Component>> original) {
        try {
            return original.call(context, player, flag);
        } catch (Exception e) {
            ItemStack self = (ItemStack) (Object) this;

            String id = "unknown";
            List<Component> fallback = new ArrayList<>();
            try {
                id = BuiltInRegistries.ITEM.getKey(self.getItem()).toString();
                fallback.add(self.getHoverName());
            } catch (Exception nested) {
                fallback.add(Component.literal("Item"));
            }

            if (ALREADY_LOGGED.add(id)) {
                mixinFix$LOGGER.warn("Tooltip crash removed for item '{}'", id, e);
            }
            return fallback;
        }
    }
}
