package com.narxoz.rpg.guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Topic-based mediator for the Adventurers' Guild war council.
 *
 * Members register a list of subscribed topics; dispatch routes the message
 * to every subscriber of that topic (excluding the sender, to avoid echo).
 *
 * The hall does not implement any colleague-specific behavior — colleagues
 * supply that themselves in {@link GuildMember#receive(String, GuildMember, String)}.
 */
public class GuildHall implements GuildMediator {

    private final Map<String, List<GuildMember>> membersByTopic = new HashMap<>();
    private final Set<GuildMember> uniqueMembers = new LinkedHashSet<>();

    private int messagesRouted = 0;
    private int memberDeliveries = 0;

    @Override
    public void register(GuildMember member) {
        if (member == null) {
            return;
        }
        uniqueMembers.add(member);
        for (String topic : member.subscribedTopics()) {
            addSubscriber(topic, member);
        }
        System.out.println("    [GuildHall] registered '" + member.getName()
                + "' for topics " + member.subscribedTopics());
    }

    @Override
    public void dispatch(String topic, GuildMember from, String payload) {
        messagesRouted++;
        List<GuildMember> subscribers = subscribersFor(topic);
        if (subscribers.isEmpty()) {
            System.out.println("    [GuildHall] '" + topic + "' (no subscribers)");
            return;
        }
        // Count deliveries excluding sender.
        int deliveryCount = 0;
        for (GuildMember s : subscribers) {
            if (s != from) deliveryCount++;
        }
        memberDeliveries += deliveryCount;

        String fromName = (from == null) ? "<system>" : from.getName();
        System.out.println("    [GuildHall] '" + topic + "' from " + fromName
                + " -> delivered to " + deliveryCount + " member(s)");

        for (GuildMember subscriber : subscribers) {
            if (subscriber == from) {
                continue; // skip echo to sender
            }
            subscriber.receive(topic, from, payload);
        }
    }

    /**
     * Total dispatch calls observed by the hall.
     */
    public int getMessagesRouted() {
        return messagesRouted;
    }

    /**
     * Total subscriber-side deliveries observed by the hall.
     */
    public int getMemberDeliveries() {
        return memberDeliveries;
    }

    /**
     * Number of distinct registered members.
     */
    public int getRegisteredMemberCount() {
        return uniqueMembers.size();
    }

    protected void addSubscriber(String topic, GuildMember member) {
        membersByTopic.computeIfAbsent(topic, key -> new ArrayList<>()).add(member);
    }

    protected List<GuildMember> subscribersFor(String topic) {
        return membersByTopic.getOrDefault(topic, List.of());
    }
}
