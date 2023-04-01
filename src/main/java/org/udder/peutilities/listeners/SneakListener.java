package org.udder.peutilities.listeners;

import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

import static org.bukkit.Bukkit.getServer;

public class SneakListener implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<String, Integer> sneakCounter = new HashMap<String, Integer>();
    public SneakListener(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if(player.isSneaking()){
            Bukkit.getServer().getLogger().info("sneak started");
            // We detect double sneak here, So if it does not contain the value it means this is our first sneak
            if (!sneakCounter.containsKey(player.getName())){
                // Add a sneak count for this player (gets removed after 10t if not double sneaked)
                sneakCounter.put(player.getName(), 1);
                // Scheduler to remove the sneak with a delay
                BukkitScheduler scheduler = getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        // Remove the sneak counter of this player, if they havent double sneaked yet
                        if(sneakCounter.containsKey(player.getName())){
                            sneakCounter.remove(player.getName());
                        }
                    }
                }, 10L);
            }else{
                //If the player double sneaked we end up in this else statement
                sneakCounter.remove(player.getName());
                // Double sneak logic
                Bukkit.getServer().getLogger().info(String.valueOf(ModelEngineAPI.getPlayerMountController(player.getUniqueId())));;
            }
        }
    }

}
