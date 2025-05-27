package name.fireballboom;

import net.fabricmc.api.ModInitializer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireballBoom implements ModInitializer {
	public static final String MOD_ID = "fireball-boom";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
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