package com.narxoz.rpg.guild;

import java.util.List;

/**
 * Guild officer responsible for orders and mission coordination.
 *
 * Subscribes to: "scouting", "healing", "supplies", "reward"
 *
 * The captain hears reports back from the other officers but issues orders
 * via the mediator, never via direct calls.
 */
public class Captain extends GuildMember {

    private int ordersIssued = 0;

    public Captain(String name, GuildMediator mediator) {
        super(name, mediator);
    }

    @Override
    public List<String> subscribedTopics() {
        return List.of("scouting", "healing", "supplies", "reward");
    }

    /**
     * Issues a top-level command. The mediator broadcasts it to whoever
     * is subscribed to the given topic.
     */
    public void issueOrder(String topic, String payload) {
        ordersIssued++;
        getMediator().dispatch(topic, this, payload);
    }

    @Override
    public void receive(String topic, GuildMember from, String payload) {
        String fromName = (from == null) ? "<system>" : from.getName();
        switch (topic) {
            case "scouting":
                System.out.println("      <- Captain       '" + getName()
                        + "' notes scouting report from " + fromName
                        + ": \"" + payload + "\"");
                break;
            case "healing":
                System.out.println("      <- Captain       '" + getName()
                        + "' notes healing plan from " + fromName
                        + ": \"" + payload + "\"");
                break;
            case "supplies":
                System.out.println("      <- Captain       '" + getName()
                        + "' notes supply status from " + fromName
                        + ": \"" + payload + "\"");
                break;
            case "reward":
                System.out.println("      <- Captain       '" + getName()
                        + "' confirms reward note from " + fromName
                        + ": \"" + payload + "\"");
                break;
            default:
                // ignore
        }
    }

    public int getOrdersIssued() {
        return ordersIssued;
    }
}
