package name.fireballboom;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireballBoom implements ModInitializer {
	public static Logger logger = LoggerFactory.getLogger("fireball-boom");
	public static void summonFireballFromPlayer(Player player, InteractionHand interactionHand){
		cooldownFireballItem(player);
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
	public static void summonFireballFromDispenser(BlockSource blockSource, ItemStack itemStack){
		if(!itemStack.is(Items.FIRE_CHARGE)) return;
		Level level = blockSource.getLevel();
		Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
		Position spawnPos = DispenserBlock.getDispensePosition(blockSource);
		LargeFireball fireball = new LargeFireball(EntityType.FIREBALL,level);
		fireball.setOwner(fireball);
		Vec3i speed = direction.getNormal();
		fireball.xPower = speed.getX() * 0.1;
		fireball.yPower = speed.getY() * 0.1;
		fireball.zPower = speed.getZ() * 0.1;
		fireball.setPos(spawnPos.x(),spawnPos.y(),spawnPos.z());
		fireball.setYRot(direction.toYRot());
		fireball.setXRot(direction.getStepX() * -90);
		level.addFreshEntity(fireball);
		itemStack.shrink(1);
	}
	public static void cooldownFireballItem(Player player){
		int ticks = player.level().getGameRules().getInt(FIREBALL_THROW_COOLDOWN);
		if(ticks <= 0) return;
		player.getCooldowns().addCooldown(Items.FIRE_CHARGE,ticks);
	}
	public static GameRules.Key<GameRules.IntegerValue> FIREBALL_THROW_COOLDOWN;

	@Override
	public void onInitialize() {
		FIREBALL_THROW_COOLDOWN = GameRuleRegistry.register(
				"fireballThrowCooldown",
				GameRules.Category.PLAYER,
				GameRuleFactory.createIntRule(4)
		);
	}
}