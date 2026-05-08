package com.narxoz.rpg.guild;

import java.util.List;

/**
 * Base class for all guild officers that communicate through a mediator.
 */
public abstract class GuildMember {

    private final String name;
    private final GuildMediator mediator;

    protected GuildMember(String name, GuildMediator mediator) {
        this.name = name;
        this.mediator = mediator;
        mediator.register(this);
    }

    public String getName() {
        return name;
    }

    protected GuildMediator getMediator() {
        return mediator;
    }

    /**
     * Lists the topics this colleague subscribes to. The mediator uses this
     * list at registration time. Each concrete colleague decides which
     * topics it cares about — colleagues never reach into each other.
     *
     * @return non-null list of topic names
     */
    public abstract List<String> subscribedTopics();

    public abstract void receive(String topic, GuildMember from, String payload);
}
