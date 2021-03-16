package dev.demon.venom.impl.check.impl.inventory;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.ClickWindowInEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

public class InventoryB extends Check{
    public InventoryB(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ClickWindowInEvent) {
            if (!user.getMiscData().isInventoryOpen()) {
                if (((ClickWindowInEvent) e).getId() == 1) {
                    handleDetection(user,"Clicked in inventory while closed");
                }
            }
        }
    }
}
