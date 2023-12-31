package com.alan.clients.module.impl.combat;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.combat.antibot.*;
import com.alan.clients.value.impl.BooleanValue;

@Rise
@ModuleInfo(name = "module.combat.antibot.name", description = "module.combat.antibot.description", category = Category.COMBAT)
public final class AntiBot extends Module {
    private final BooleanValue advancedAntiBot = new BooleanValue("Always Nearby Check", this, false,
            new AdvancedAntiBot("", this));

    private final BooleanValue watchdogAntiBot = new BooleanValue("Watchdog Check", this, false,
            new WatchdogAntiBot("", this));

    private final BooleanValue funcraftAntiBot = new BooleanValue("Funcraft Check", this, false,
            new FuncraftAntiBot("", this));

    private final BooleanValue hytAntiBot = new BooleanValue("HuaYuTing", this, false,
            new HuaYuTingAntiBot("", this));

    private final BooleanValue ncps = new BooleanValue("NPC Detection Check", this, false,
            new NPCAntiBot("", this));

    private final BooleanValue middleClick = new BooleanValue("Middle Click Bot", this, false,
            new MiddleClickBot("", this));

    @Override
    protected void onDisable() {
        Client.INSTANCE.getBotManager().clear();
    }
}
