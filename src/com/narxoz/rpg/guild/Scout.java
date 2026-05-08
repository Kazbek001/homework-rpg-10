package com.narxoz.rpg.guild;

import java.util.List;

/**
 * Guild officer responsible for route reports and reconnaissance.
 *
 * Subscribes to: "scouting", "orders", "danger"
 */
public class Scout extends GuildMember {

    private int routesScouted = 0;

    public Scout(String name, GuildMediator mediator) {
        super(name, mediator);
    }

    @Override
    public List<String> subscribedTopics() {
        return List.of("scouting", "orders", "danger");
    }

    public void reportRoute(String topic, String payload) {
        getMediator().dispatch(topic, this, payload);
    }

    @Override
    public void receive(String topic, GuildMember from, String payload) {
        String fromName = (from == null) ? "<system>" : from.getName();
        switch (topic) {
            case "orders":
                routesScouted++;
                System.out.println("      <- Scout         '" + getName()
                        + "' received order from " + fromName
                        + ": \"" + payload + "\"  -> mapping approach (route #" + routesScouted + ")");
                break;
            case "scouting":
                System.out.println("      <- Scout         '" + getName()
                        + "' shares terrain note from " + fromName
                        + ": \"" + payload + "\"");
                break;
            case "danger":
                System.out.println("      <- Scout         '" + getName()
                        + "' notes danger flag from " + fromName
                        + ": \"" + payload + "\"  -> raising alert");
                break;
            default:
                // ignore
        }
    }

    public int getRoutesScouted() {
        return routesScouted;
    }
}
