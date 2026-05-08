package com.narxoz.rpg.guild;

import java.util.List;

/**
 * Guild officer responsible for wounds, potions, and recovery plans.
 *
 * Subscribes to: "healing", "orders", "danger"
 */
public class Healer extends GuildMember {

    private int aidPlansPrepared = 0;

    public Healer(String name, GuildMediator mediator) {
        super(name, mediator);
    }

    @Override
    public List<String> subscribedTopics() {
        return List.of("healing", "orders", "danger");
    }

    public void prepareAid(String topic, String payload) {
        getMediator().dispatch(topic, this, payload);
    }

    @Override
    public void receive(String topic, GuildMember from, String payload) {
        String fromName = (from == null) ? "<system>" : from.getName();
        switch (topic) {
            case "orders":
                aidPlansPrepared++;
                System.out.println("      <- Healer        '" + getName()
                        + "' received order from " + fromName
                        + ": \"" + payload + "\"  -> brewing potions (plan #" + aidPlansPrepared + ")");
                break;
            case "healing":
                System.out.println("      <- Healer        '" + getName()
                        + "' acknowledges aid request from " + fromName
                        + ": \"" + payload + "\"");
                break;
            case "danger":
                System.out.println("      <- Healer        '" + getName()
                        + "' prepares triage kit after danger flag from " + fromName
                        + ": \"" + payload + "\"");
                break;
            default:
                // ignore
        }
    }

    public int getAidPlansPrepared() {
        return aidPlansPrepared;
    }
}
