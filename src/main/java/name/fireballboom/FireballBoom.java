package name.fireballboom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireballBoom {
	public static Logger logger = LoggerFactory.getLogger("fireball-boom");
	public static void summonFireball(Player player, InteractionHand interactionHand){
		ItemStack stack = player.getItemInHand(interactionHand);
		if(stack.is(Items.FIRE_CHARGE) && stack.getCount() > 0){
            Level level = player.level();
			Vec3 look = player.getLookAngle();
			Vec3 spawnPos = player.getEyePosition();
			LargeFireball fireball = new LargeFireball(level,player,look.x,look.y,look.z,6);
			fireball.setPos(spawnPos);
			level.addFreshEntity(fireball);
			player.getItemInHand(interactionHand).shrink(1);
		}
	}
}