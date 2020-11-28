package dev.demon.venom.impl.events.inevents;


import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerSettingsEvent extends AnticheatEvent {

    @Getter
    @Setter
    private int version;
    private String langauge;

    public PlayerSettingsEvent(String langauge, int version) {
        this.langauge = langauge;
        this.version = version;
    }

}
