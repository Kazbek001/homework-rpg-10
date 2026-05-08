package com.narxoz.rpg.quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate root for quests.
 *
 * The internal list is deliberately hidden. Clients must request a
 * QuestIterator instead of reaching into the aggregate's collection.
 */
public class QuestLog {

    private final List<Quest> quests = new ArrayList<>();

    public void add(Quest quest) {
        if (quest != null) {
            quests.add(quest);
        }
    }

    public int size() {
        return quests.size();
    }

    public QuestIterator ordered() {
        return new OrderedQuestIterator(this);
    }

    public QuestIterator reverse() {
        return new ReverseQuestIterator(this);
    }

    public QuestIterator priorityAtLeast(QuestPriority threshold) {
        return new PriorityQuestIterator(this, threshold);
    }

    public QuestIterator rewardSorted() {
        return new RewardSortedQuestIterator(this);
    }

    List<Quest> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(quests));
    }
}
