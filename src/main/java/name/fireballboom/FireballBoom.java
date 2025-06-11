package name.fireballboom;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireballBoom implements ModInitializer {
	public static Logger logger = LoggerFactory.getLogger("fireball-boom");
	public static void summonFireballFromPlayer(PlayerEntity player, Hand interactionHand){
		cooldownFireballItem(player);
		ItemStack stack = player.getStackInHand(interactionHand);
		if(stack.isOf(Items.FIRE_CHARGE) && stack.getCount() > 0){
            World level = player.getWorld();
			Vec3d look = player.getRotationVector();
			Vec3d spawnPos = player.getEyePos();
			FireballEntity fireball = new FireballEntity(level,player,look.x,look.y,look.z,6);
			fireball.setPos(spawnPos.x,spawnPos.y,spawnPos.z);
			level.spawnEntity(fireball);
			stack.decrement(1);
		}
	}
	public static void summonFireballFromDispenser(BlockPointer blockSource, ItemStack itemStack){
		if(!itemStack.isOf(Items.FIRE_CHARGE)) return;
		World level = blockSource.getWorld();
		Direction direction = blockSource.getBlockState().get(DispenserBlock.FACING);
		Position spawnPos = DispenserBlock.getOutputLocation(blockSource);
		FireballEntity fireball = new FireballEntity(EntityType.FIREBALL,level);
		fireball.setOwner(fireball);
		Vec3i speed = direction.getVector();
		fireball.powerX = speed.getX() * 0.1;
		fireball.powerY = speed.getY() * 0.1;
		fireball.powerZ = speed.getZ() * 0.1;
		fireball.setPosition(spawnPos.getX(),spawnPos.getY(),spawnPos.getZ());
		fireball.setYaw(direction.asRotation());
		fireball.setPitch(direction.getOffsetX() * -90);
		level.spawnEntity(fireball);
		itemStack.decrement(1);
	}
	public static void cooldownFireballItem(PlayerEntity player){
		int ticks = player.getWorld().getGameRules().getInt(FIREBALL_THROW_COOLDOWN);
		if(ticks <= 0) return;
		player.getItemCooldownManager().set(Items.FIRE_CHARGE,ticks);
	}
	public static GameRules.Key<GameRules.IntRule> FIREBALL_THROW_COOLDOWN;

	@Override
	public void onInitialize() {
		FIREBALL_THROW_COOLDOWN = GameRuleRegistry.register(
				"fireballThrowCooldown",
				GameRules.Category.PLAYER,
				GameRuleFactory.createIntRule(4)
		);
	}
}