package name.fireballboom.mixin;

import name.fireballboom.FireballBoom;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin {
	@Inject(at = @At("HEAD"), method = "useOn",cancellable = true)
	private void preventUseFireball(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
		Player player = useOnContext.getPlayer();
		if(player instanceof ServerPlayer) {
			FireballBoom.summonFireballFromPlayer(player, useOnContext.getHand());
		}
		cir.setReturnValue(InteractionResult.sidedSuccess(useOnContext.getLevel().isClientSide));
	}
}