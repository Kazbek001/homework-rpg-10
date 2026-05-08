package com.narxoz.rpg.guild;

import java.util.List;

/**
 * Part 4 — Mediator open/closed proof.
 *
 * The Loremaster studies cursed quests, ancient lore, and historical records.
 * It is added to the guild WITHOUT modifying any existing colleague:
 *   - Quartermaster, Scout, Healer, Captain are untouched
 *   - GuildHall and GuildMediator are untouched
 *   - The new colleague subscribes to its own topics: "lore", "curse", "history"
 *
 * Existing colleagues do not gain any reference to Loremaster — communication
 * still flows only through {@link GuildHall#dispatch(String, GuildMember, String)}.
 */
public class Loremaster extends GuildMember {

    private int loreEntriesRecorded = 0;
    private int cursesAnalyzed = 0;

    public Loremaster(String name, GuildMediator mediator) {
        super(name, mediator);
    }

    @Override
    public List<String> subscribedTopics() {
        return List.of("lore", "curse", "history");
    }

    /**
     * Convenience outbound call — shares a piece of lore with anyone listening.
     */
    public void shareLore(String topic, String payload) {
        getMediator().dispatch(topic, this, payload);
    }

    @Override
    public void receive(String topic, GuildMember from, String payload) {
        String fromName = (from == null) ? "<system>" : from.getName();
        switch (topic) {
            case "lore":
                loreEntriesRecorded++;
                System.out.println("      <- Loremaster    '" + getName()
                        + "' archives lore from " + fromName
                        + ": \"" + payload + "\"  (entry #" + loreEntriesRecorded + ")");
                break;
            case "curse":
                cursesAnalyzed++;
                System.out.println("      <- Loremaster    '" + getName()
                        + "' analyzes curse from " + fromName
                        + ": \"" + payload + "\"  (curse #" + cursesAnalyzed + ")");
                break;
            case "history":
                System.out.println("      <- Loremaster    '" + getName()
                        + "' cross-references history from " + fromName
                        + ": \"" + payload + "\"");
                break;
            default:
                // ignore
        }
    }

    public int getLoreEntriesRecorded() {
        return loreEntriesRecorded;
    }

    public int getCursesAnalyzed() {
        return cursesAnalyzed;
    }
}
