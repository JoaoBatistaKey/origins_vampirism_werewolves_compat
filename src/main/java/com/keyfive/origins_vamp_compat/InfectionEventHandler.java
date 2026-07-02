package com.keyfive.origins_vamp_compat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = OriginsVampCompatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfectionEventHandler {

    private static final Map<UUID, Boolean> FACTION_SET = new HashMap<>();
    private static final Map<UUID, ResourceLocation> PLAYER_ORIGINS = new HashMap<>();

    private static final ResourceLocation VAMPIRE_ORIGIN =
        ResourceLocation.tryParse("origins_vamp_compat:vampire");
    private static final ResourceLocation WEREWOLF_ORIGIN =
        ResourceLocation.tryParse("origins_vamp_compat:werewolf");
    private static final ResourceLocation HUNTER_ORIGIN =
        ResourceLocation.tryParse("origins_vamp_compat:hunter");
    private static final ResourceLocation HUMAN_ORIGIN =
        ResourceLocation.tryParse("origins:human");
    private static final ResourceLocation EMPTY_ORIGIN =
        ResourceLocation.tryParse("origins:empty");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side == LogicalSide.CLIENT) return;
        if (event.player.tickCount % 40 != 0) return;

        processPlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        FACTION_SET.remove(event.getEntity().getUUID());
        PLAYER_ORIGINS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerDrinkBlood(BloodDrinkEvent.PlayerDrinkBloodEvent event) {
        try {
            if (!(event.getVampire() instanceof Player vampirePlayer)) return;
            if (vampirePlayer.level().isClientSide) return;

            var bloodSource = event.getBloodSource().getEntity().orElse(null);
            if (!(bloodSource instanceof Player targetPlayer)) return;

            ResourceLocation targetOrigin = getPlayerOriginId(targetPlayer);
            if (targetOrigin == null) return;

            if (HUMAN_ORIGIN.equals(targetOrigin)) return;
            if (HUNTER_ORIGIN.equals(targetOrigin)) return;

            vampirePlayer.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1, false, true, true));
            System.out.println("[OriginsVampCompat] Vampiro " + vampirePlayer.getName().getString()
                + " levou wither por sugar sangue de " + targetPlayer.getName().getString()
                + " (origem: " + targetOrigin + ")");
        } catch (Exception e) {
            System.out.println("[OriginsVampCompat] Erro em onPlayerDrinkBlood: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public static void onFactionChangePre(PlayerFactionEvent.FactionLevelChangePre event) {
        try {
            Player player = event.getPlayer().getPlayer();
            if (player == null || player.level().isClientSide) return;

            IPlayableFaction<?> currentFaction = event.getPlayer().getCurrentFaction();
            IPlayableFaction<?> newFaction = event.getCurrentFaction();

            if (currentFaction != null) return;
            if (newFaction == null) return;

            ResourceLocation originId = getPlayerOriginId(player);
            if (originId == null) return;

            if (VAMPIRE_ORIGIN.equals(originId)) return;
            if (WEREWOLF_ORIGIN.equals(originId)) return;
            if (HUNTER_ORIGIN.equals(originId)) return;
            if (HUMAN_ORIGIN.equals(originId)) return;
            if (EMPTY_ORIGIN.equals(originId)) return;

            event.setCanceled(true);
            System.out.println("[OriginsVampCompat] Bloqueada infeccao para " + player.getName().getString() + " (origem: " + originId + ")");
        } catch (Exception e) {
            System.out.println("[OriginsVampCompat] Erro em onFactionChangePre: " + e.getMessage());
        }
    }

    private static void processPlayer(Player player) {
        try {
            UUID uuid = player.getUUID();
            ResourceLocation originId = getPlayerOriginId(player);

            if (originId == null) {
                System.out.println("[OriginsVampCompat] " + player.getName().getString() + " sem origem ainda");
                return;
            }

            System.out.println("[OriginsVampCompat] Processando " + player.getName().getString() + " origem=" + originId);

            ResourceLocation previousOrigin = PLAYER_ORIGINS.get(uuid);
            if (previousOrigin != null && !previousOrigin.equals(originId)) {
                System.out.println("[OriginsVampCompat] Origem mudou de " + previousOrigin + " para " + originId + " - removendo faccoes antigas");
                removeFactionLevels(player);
                FACTION_SET.put(uuid, false);
            }
            PLAYER_ORIGINS.put(uuid, originId);

            boolean factionSet = FACTION_SET.getOrDefault(uuid, false);

            if (VAMPIRE_ORIGIN.equals(originId)) {
                if (!factionSet) {
                    System.out.println("[OriginsVampCompat] Detectado vampire, setando faction");
                    setFactionLevel(player, "vampirism:vampire", 5);
                    FACTION_SET.put(uuid, true);
                }
                return;
            }

            if (WEREWOLF_ORIGIN.equals(originId)) {
                if (!factionSet) {
                    System.out.println("[OriginsVampCompat] Detectado werewolf, setando faction");
                    setFactionLevel(player, "werewolves:werewolf", 5);
                    FACTION_SET.put(uuid, true);
                }
                return;
            }

            if (HUNTER_ORIGIN.equals(originId)) {
                if (!factionSet) {
                    System.out.println("[OriginsVampCompat] Detectado hunter, setando faction");
                    setFactionLevel(player, "vampirism:hunter", 5);
                    FACTION_SET.put(uuid, true);
                }
                return;
            }

            if (HUMAN_ORIGIN.equals(originId)) return;
            if (EMPTY_ORIGIN.equals(originId)) return;

            System.out.println("[OriginsVampCompat] Detectada origem protegida: " + originId + " - aplicando efeitos");
            applyProtectiveEffects(player);

        } catch (Exception e) {
            System.out.println("[OriginsVampCompat] Erro processando " + player.getName().getString() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ResourceLocation getPlayerOriginId(Player player) {
        IOriginContainer container = IOriginContainer.get(player).resolve().orElse(null);
        if (container == null) {
            System.out.println("[OriginsVampCompat] IOriginContainer nao disponivel para " + player.getName().getString());
            return null;
        }

        var layers = OriginsAPI.getActiveLayers();
        if (layers == null || layers.isEmpty()) {
            System.out.println("[OriginsVampCompat] Nenhuma layer ativa encontrada para " + player.getName().getString());
            return null;
        }

        ResourceLocation mainLayerId = ResourceLocation.tryParse("origins:origin");
        for (var layerHolder : layers) {
            ResourceKey<OriginLayer> layerKey = layerHolder.unwrapKey().orElse(null);
            if (layerKey == null) continue;
            if (!mainLayerId.equals(layerKey.location())) continue;

            ResourceKey<?> originKey = container.getOrigin(layerKey);
            if (originKey == null) continue;

            System.out.println("[OriginsVampCompat] " + player.getName().getString() + " origin=" + originKey.location() + " (layer " + mainLayerId + ")");
            return originKey.location();
        }

        System.out.println("[OriginsVampCompat] Layer " + mainLayerId + " nao encontrada ou sem origin para " + player.getName().getString());
        return null;
    }

    private static void applyProtectiveEffects(Player player) {
        try {
            MobEffect garlic = ForgeRegistries.MOB_EFFECTS.getValue(
                ResourceLocation.tryParse("vampirism:garlic"));
            if (garlic != null) {
                player.addEffect(new MobEffectInstance(garlic, 200, 0, false, true, true));
                System.out.println("[OriginsVampCompat] Aplicado sangue de alho em " + player.getName().getString());
            } else {
                System.out.println("[OriginsVampCompat] Efeito vampirism:garlic nao encontrado!");
            }

            MobEffect wolfsbane = ForgeRegistries.MOB_EFFECTS.getValue(
                ResourceLocation.tryParse("werewolves:wolfsbane"));
            if (wolfsbane != null) {
                player.addEffect(new MobEffectInstance(wolfsbane, 200, 0, false, true, true));
                System.out.println("[OriginsVampCompat] Aplicado wolfsbane em " + player.getName().getString());
            } else {
                System.out.println("[OriginsVampCompat] Efeito werewolves:wolfsbane nao encontrado!");
            }
        } catch (Exception e) {
            System.out.println("[OriginsVampCompat] Erro ao aplicar efeitos protetivos: " + e.getMessage());
        }
    }

    private static void setFactionLevel(Player player, String factionId, int level) {
        try {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player)
                .resolve().orElse(null);
            if (handler == null) {
                System.out.println("[OriginsVampCompat] Faction handler nao disponivel para " + player.getName().getString());
                return;
            }

            IPlayableFaction<?> faction = (IPlayableFaction<?>)
                VampirismAPI.factionRegistry().getFactionByID(ResourceLocation.tryParse(factionId));

            if (faction == null) {
                System.out.println("[OriginsVampCompat] Faction nao encontrada: " + factionId);
                return;
            }

            int currentLevel = handler.getCurrentLevel(faction);
            if (currentLevel < level) {
                handler.setFactionLevel(faction, level);
                System.out.println("[OriginsVampCompat] Setado " + player.getName().getString() + " para " + factionId + " nivel " + level);
            } else {
                System.out.println("[OriginsVampCompat] " + player.getName().getString() + " ja tem " + factionId + " nivel " + currentLevel);
            }
        } catch (Exception e) {
            System.out.println("[OriginsVampCompat] Falha ao setar faction: " + e.getMessage());
        }
    }

    private static void removeFactionLevels(Player player) {
        try {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player)
                .resolve().orElse(null);
            if (handler == null) {
                System.out.println("[OriginsVampCompat] Faction handler nao disponivel para " + player.getName().getString());
                return;
            }

            IPlayableFaction<?> vampire = (IPlayableFaction<?>)
                VampirismAPI.factionRegistry().getFactionByID(ResourceLocation.tryParse("vampirism:vampire"));
            IPlayableFaction<?> werewolf = (IPlayableFaction<?>)
                VampirismAPI.factionRegistry().getFactionByID(ResourceLocation.tryParse("werewolves:werewolf"));
            IPlayableFaction<?> hunter = (IPlayableFaction<?>)
                VampirismAPI.factionRegistry().getFactionByID(ResourceLocation.tryParse("vampirism:hunter"));

            if (vampire != null) handler.setFactionLevel(vampire, 0);
            if (werewolf != null) handler.setFactionLevel(werewolf, 0);
            if (hunter != null) handler.setFactionLevel(hunter, 0);

            System.out.println("[OriginsVampCompat] Removidos niveis de faccao para " + player.getName().getString());
        } catch (Exception e) {
            System.out.println("[OriginsVampCompat] Erro ao remover faccoes: " + e.getMessage());
        }
    }
}
