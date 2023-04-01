package org.udder.peutilities.listeners;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEBoatController;

import static org.bukkit.Bukkit.getServer;

public class MountListener implements Listener {
    private final JavaPlugin plugin;
    public MountListener(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onMount(ModelMountEvent event){
        // Create a scheduler, to run the event with a 1t delay
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                // We check if the vehicle is a PE boat, if so the gravity might have been turned off so we turn it on
                // (Gravity gets turned off when a boat is dismounted outside waters)
                ModeledEntity vehicle = event.getVehicle();
                if(vehicle.getBase().getOriginal() instanceof Entity &&
                    vehicle.getMountManager().getDriverController() instanceof PEBoatController)
                {
                    ((Entity) vehicle.getBase().getOriginal()).setGravity(true);
                }
            }
        }, 1L);
    }

}
