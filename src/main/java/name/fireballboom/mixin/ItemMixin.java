package name.fireballboom.mixin;

import name.fireballboom.FireballBoom;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "use",cancellable = true)
    void useThrowFireball(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        ItemStack stack = user.getStackInHand(hand);
        if(stack.isOf(Items.FIRE_CHARGE)){
            if(user instanceof ServerPlayerEntity) {
                FireballBoom.summonFireballFromPlayer(user, hand);
            }
            cir.setReturnValue(ActionResult.SUCCESS_SERVER);
        }
    }
}
