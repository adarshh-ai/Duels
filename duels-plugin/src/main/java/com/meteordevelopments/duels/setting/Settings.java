package com.meteordevelopments.duels.setting;

import com.meteordevelopments.duels.party.Party;
import lombok.Getter;
import lombok.Setter;
import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.gui.settings.SettingsGui;
import com.meteordevelopments.duels.kit.KitImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Settings {

    private final DuelsPlugin plugin;
    private final SettingsGui gui;

    @Getter
    private UUID target;
    @Getter
    private KitImpl kit;
    @Getter
    @Setter
    private ArenaImpl arena;
    @Getter
    @Setter
    private int bet;
    @Getter
    @Setter
    private boolean itemBetting;
    @Getter
    private boolean ownInventory;
    @Getter
    private Map<UUID, CachedInfo> cache = new HashMap<>();
    @Getter
    @Setter
    private Party senderParty;
    @Getter
    @Setter
    private Party targetParty;

    public Settings(final DuelsPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.gui = player != null ? plugin.getGuiListener().addGui(player, new SettingsGui(plugin)) : null;
        // If kits are disabled, then ownInventory is enabled by default.
        this.ownInventory = !plugin.getConfiguration().isKitSelectingEnabled();
    }

    public Settings(final DuelsPlugin plugin) {
        this(plugin, null);
    }

    public void reset() {
        target = null;
        senderParty = null;
        targetParty = null;
        kit = null;
        arena = null;
        bet = 0;
        itemBetting = false;
        ownInventory = !plugin.getConfiguration().isKitSelectingEnabled();
        clearCache();
    }

    public void setTarget(final Player target) {
        if (this.target != null && !this.target.equals(target.getUniqueId())) {
            reset();
        }

        this.target = target.getUniqueId();
    }

    public void updateGui(final Player player) {
        if (gui != null) {
            gui.update(player);
        }
    }

    public void clearCache() {
        cache.clear();
    }

    public void openGui(final Player player) {
        gui.open(player);
    }

    public void setBaseLoc(final Player player) {
        cache.computeIfAbsent(player.getUniqueId(), result -> new CachedInfo()).setLocation(player.getLocation().clone());
    }

    public Location getBaseLoc(final Player player) {
        final CachedInfo info = cache.get(player.getUniqueId());

        if (info == null) {
            return null;
        }

        return info.getLocation();
    }

    public void setDuelzone(final Player player, final String duelzone) {
        cache.computeIfAbsent(player.getUniqueId(), result -> new CachedInfo()).setDuelzone(duelzone);
    }

    public String getDuelzone(final Player player) {
        final CachedInfo info = cache.get(player.getUniqueId());

        if (info == null) {
            return null;
        }

        return info.getDuelzone();
    }

    public boolean isPartyDuel() {
        return senderParty != null && targetParty != null;
    }

    public void setKit(final KitImpl kit) {
        this.kit = kit;
        this.ownInventory = false;
    }

    public void setOwnInventory(final boolean ownInventory) {
        this.ownInventory = ownInventory;

        if (ownInventory) {
            this.kit = null;
        }
    }

    // Don't copy the gui since it won't be required to start a match
    public Settings lightCopy() {
        final Settings copy = new Settings(plugin);
        copy.target = target;
        copy.senderParty = senderParty;
        copy.targetParty = targetParty;
        copy.kit = kit;
        copy.arena = arena;
        copy.bet = bet;
        copy.itemBetting = itemBetting;
        copy.ownInventory = ownInventory;
        copy.cache = new HashMap<>(cache);
        return copy;
    }
}
