package org.udder.peutilities.listeners;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEFlyingMountController;

import java.util.HashMap;

import static org.bukkit.Bukkit.getServer;

public class MountListener implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<String, Integer> dismountCounter = new HashMap<String, Integer>();
    public MountListener(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onMount(ModelMountEvent event){
        // We check if the vehicle is a PE boat, if so the gravity might have been turned off so we turn it on
        // (Gravity gets turned off when a boat is dismounted outside waters)
        ModeledEntity vehicle = event.getVehicle();
        if(vehicle.getBase().getOriginal() instanceof Entity)
        {
            ((Entity) vehicle.getBase().getOriginal()).setGravity(true);
        }
    }

    @EventHandler
    public void onWaterDismount(ModelDismountEvent event){
        ModeledEntity vehicle = event.getVehicle();
        if(vehicle.getBase().getOriginal() instanceof Entity)
        {
            // When we dismount, and we are inside of water. We turn off gravity, to stay floating
            if (String.valueOf(vehicle.getBase().getLocation().getBlock().getType()).equals("WATER")){
                ((Entity) vehicle.getBase().getOriginal()).setGravity(false);
            }
        }
    }

    @EventHandler
    public void doubleSneakDismount(ModelDismountEvent event){
        ModeledEntity vehicle = event.getVehicle();
        String uuid = String.valueOf(event.getPassenger().getUniqueId());
        if(vehicle.getMountManager().getDriverController() instanceof PEFlyingMountController)
        {
            // We detect double sneak here, So if it does not contain the value it means this is our first sneak
            if (!dismountCounter.containsKey(uuid)){
                // Add a sneak count for this player (gets removed after 10t if not double sneaked)
                dismountCounter.put(uuid, 1);
                // Scheduler to remove the sneak with a delay
                BukkitScheduler scheduler = getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        // Remove the sneak counter of this player, if they havent double sneaked yet
                        if(dismountCounter.containsKey(uuid)){
                            dismountCounter.remove(uuid);
                        }
                    }
                }, 10L);
                event.setCancelled(true);
            }else{
                //If the player double sneaked we end up in this else statement
                dismountCounter.remove(uuid);
                noGravityDismount(vehicle);
            }
        }
    }

    private void noGravityDismount(ModeledEntity vehicle){
        // When dismounting a flying mount, it will always have no gravity
        if(vehicle.getBase().getOriginal() instanceof Entity) {
            ((Entity) vehicle.getBase().getOriginal()).setGravity(false);
            new BukkitRunnable(){public void run(){
                if(vehicle.getBase().getOriginal() != null){
                    ((Entity) vehicle.getBase().getOriginal()).setVelocity(new Vector(0, 0, 0));
                }
            }}.runTaskLater(this.plugin, 5L);
        }
    }
}
