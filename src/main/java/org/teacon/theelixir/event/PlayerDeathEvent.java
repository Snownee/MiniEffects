package org.teacon.theelixir.event;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.teacon.theelixir.capability.CapabilityRegistryHandler;
import org.teacon.theelixir.capability.TheElixirCapability;
import org.teacon.theelixir.item.Restrainer;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber
public class PlayerDeathEvent {
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) livingEntity;

            if (event.getSource().getTrueSource() instanceof ServerPlayerEntity) {
                ServerPlayerEntity attacker = (ServerPlayerEntity) event.getSource().getTrueSource();

                if (attacker.getHeldItem(Hand.MAIN_HAND).getItem() instanceof Restrainer) {
                    return;
                }
            }

            TheElixirCapability capability = serverPlayerEntity.getCapability(CapabilityRegistryHandler.THE_ELIXIR_CAPABILITY).orElse(null);

            if (capability.isUsedElixir()) {
                //死亡信息广播
                boolean flag = serverPlayerEntity.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
                if (flag) {
                    ITextComponent itextcomponent = serverPlayerEntity.getCombatTracker().getDeathMessage();
                    Team team = serverPlayerEntity.getTeam();
                    if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {
                        if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {
                            serverPlayerEntity.server.getPlayerList().sendMessageToAllTeamMembers(serverPlayerEntity, itextcomponent);
                        } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {
                            serverPlayerEntity.server.getPlayerList().sendMessageToTeamOrAllPlayers(serverPlayerEntity, itextcomponent);
                        }
                    } else {
                        serverPlayerEntity.server.getPlayerList().func_232641_a_(itextcomponent, ChatType.SYSTEM, Util.DUMMY_UUID);
                    }
                }

                capability.difficultyPoint += 1;

                //物品消耗，事件取消
                event.setCanceled(true);

                //中立生物仇恨重置（func_241157_eT_()）
                if (serverPlayerEntity.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
                    forgivePlayer(serverPlayerEntity);
                }

                //计分板数据更新
                serverPlayerEntity.getWorldScoreboard().forAllObjectives(ScoreCriteria.DEATH_COUNT, serverPlayerEntity.getScoreboardName(), Score::incrementScore);

                //统计数据（被生物杀死）更新
                LivingEntity damageSource = serverPlayerEntity.getAttackingEntity();
                if (damageSource != null) {
                    serverPlayerEntity.addStat(Stats.ENTITY_KILLED_BY.get(damageSource.getType()));
                    CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger(serverPlayerEntity, damageSource, event.getSource());
                }

                //死亡相关统计数据更新
                serverPlayerEntity.addStat(Stats.DEATHS);
                serverPlayerEntity.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
                serverPlayerEntity.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));

                net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerRespawnEvent(serverPlayerEntity, false);

                //生命回复，饱食度回复，所有效果（药水&火焰&窒息）清除，无敌5s（考虑调用玩家重生的方法，避免可能的mod自定义玩家数值无法回复）
                livingEntity.setHealth(serverPlayerEntity.getMaxHealth());

                //重置战斗纪录
                serverPlayerEntity.getCombatTracker().reset();
            }
        }
    }

    public static void forgivePlayer(ServerPlayerEntity player)
    {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(player.getPosition())).grow(32.0D, 10.0D, 32.0D);
        player.world
                .getLoadedEntitiesWithinAABB(MobEntity.class, axisalignedbb)
                .stream()
                .filter((entity) -> entity instanceof IAngerable)
                .forEach((mobEntity) -> ((IAngerable) mobEntity).func_233681_b_(player));
    }
}
