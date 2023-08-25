package org.rm20.enderpearlfixer;

import org.bukkit.plugin.java.JavaPlugin;
import org.rm20.enderpearlfixer.Fix.StrongholdFix;
public final class EnderPearlFixer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        StrongholdFix strongholdFix = new StrongholdFix();
        strongholdFix.onStart();
        //console output
        getLogger().info("EnderPearlFixer has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
