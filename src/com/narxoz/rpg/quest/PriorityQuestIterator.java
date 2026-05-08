package com.narxoz.rpg.quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Traverses only quests whose priority is at least the requested threshold.
 */
public class PriorityQuestIterator implements QuestIterator {

    private final List<Quest> snapshot;
    private int cursor;

    public PriorityQuestIterator(QuestLog questLog, QuestPriority threshold) {
        QuestPriority minimum = threshold == null ? QuestPriority.LOW : threshold;
        this.snapshot = new ArrayList<>();
        for (Quest quest : questLog.snapshot()) {
            if (quest.getPriority().ordinal() >= minimum.ordinal()) {
                snapshot.add(quest);
            }
        }
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < snapshot.size();
    }

    @Override
    public Quest next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException("No more quests in PriorityQuestIterator");
        }
        return snapshot.get(cursor++);
    }
}
