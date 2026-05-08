package com.narxoz.rpg.guild;

import java.util.List;

/**
 * Guild officer responsible for gear, supplies, and rewards.
 *
 * Subscribes to: "supplies", "orders", "reward"
 *
 * Notice: this class never holds a reference to Scout, Healer, or Captain.
 * All cross-colleague communication leaves through {@code getMediator().dispatch(...)}.
 */
public class Quartermaster extends GuildMember {

    private int packsPrepared = 0;

    public Quartermaster(String name, GuildMediator mediator) {
        super(name, mediator);
    }

    @Override
    public List<String> subscribedTopics() {
        return List.of("supplies", "orders", "reward");
    }

    /**
     * Convenience outbound call — sends a supplies message to the hall.
     */
    public void requestSupplies(String topic, String payload) {
        getMediator().dispatch(topic, this, payload);
    }

    @Override
    public void receive(String topic, GuildMember from, String payload) {
        String fromName = (from == null) ? "<system>" : from.getName();
        switch (topic) {
            case "orders":
                packsPrepared++;
                System.out.println("      <- Quartermaster '" + getName()
                        + "' received order from " + fromName
                        + ": \"" + payload + "\"  -> packing rations & rope (#" + packsPrepared + ")");
                break;
            case "supplies":
                System.out.println("      <- Quartermaster '" + getName()
                        + "' acknowledges supply note from " + fromName
                        + ": \"" + payload + "\"");
                break;
            case "reward":
                System.out.println("      <- Quartermaster '" + getName()
                        + "' logs reward from " + fromName
                        + ": \"" + payload + "\" -> updating ledger");
                break;
            default:
                // ignore other topics
        }
    }

    public int getPacksPrepared() {
        return packsPrepared;
    }
}
