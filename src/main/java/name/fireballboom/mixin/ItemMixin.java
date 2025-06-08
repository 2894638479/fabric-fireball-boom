package name.fireballboom.mixin;

import name.fireballboom.FireballBoom;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "use",cancellable = true)
    void useThrowFireball(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        ItemStack stack = player.getItemInHand(interactionHand);
        if(stack.is(Items.FIRE_CHARGE)){
            if(player instanceof ServerPlayer) {
                FireballBoom.summonFireballFromPlayer(player, interactionHand);
            }
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        }
    }
}
