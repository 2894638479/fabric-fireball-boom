package name.fireballboom.mixin;

import name.fireballboom.FireballBoom;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin {
	@Inject(at = @At("HEAD"), method = "useOnBlock",cancellable = true)
	private void changeFireballUsage(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		PlayerEntity player = context.getPlayer();
		if(player instanceof ServerPlayerEntity) {
			FireballBoom.summonFireballFromPlayer(player, context.getHand());
		}
		cir.setReturnValue(ActionResult.success(context.getWorld().isClient));
	}
}