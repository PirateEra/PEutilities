package org.udder.peutilities.listeners;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
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
        // We check if the vehicle is a PE boat, if so the gravity might have been turned off so we turn it on
        // (Gravity gets turned off when a boat is dismounted outside waters)
        ModeledEntity vehicle = event.getVehicle();
        if(vehicle.getBase().getOriginal() instanceof Entity)
        {
            ((Entity) vehicle.getBase().getOriginal()).setGravity(true);
        }
    }

    @EventHandler
    public void onDismount(ModelDismountEvent event){
        ModeledEntity vehicle = event.getVehicle();
        if(vehicle.getBase().getOriginal() instanceof Entity)
        {
            // When we dismount, and we are inside of water. We turn off gravity, to stay floating
            if (String.valueOf(vehicle.getBase().getLocation().getBlock().getType()).equals("WATER")){
                ((Entity) vehicle.getBase().getOriginal()).setGravity(false);
            }
        }
    }

}
