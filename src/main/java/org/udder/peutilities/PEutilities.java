package org.udder.peutilities;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import com.ticxo.modelengine.api.ModelEngineAPI;
import org.udder.peutilities.listeners.MountListener;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEBoatController;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEDiveBoatController;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEFlyingMountController;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEMountController;

public final class PEutilities extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getLogger().info("PEutilities loaded");
        addpemounts();
        getServer().getPluginManager().registerEvents(new MountListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void addpemounts() {
        ModelEngineAPI.getControllerRegistry().register("pemount", PEMountController::new);
        ModelEngineAPI.getControllerRegistry().register("peboat", PEBoatController::new);
        ModelEngineAPI.getControllerRegistry().register("peflymount", PEFlyingMountController::new);
        ModelEngineAPI.getControllerRegistry().register("pediveboat", PEDiveBoatController::new);
    }
}
