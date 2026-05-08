package com.narxoz.rpg.quest;

import java.util.List;

/**
 * Traverses quests from newest arrival back to oldest arrival.
 */
public class ReverseQuestIterator implements QuestIterator {

    private final List<Quest> snapshot;
    private int cursor;

    public ReverseQuestIterator(QuestLog questLog) {
        this.snapshot = questLog.snapshot();
        this.cursor = snapshot.size() - 1;
    }

    @Override
    public boolean hasNext() {
        return cursor >= 0;
    }

    @Override
    public Quest next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException("No more quests in ReverseQuestIterator");
        }
        return snapshot.get(cursor--);
    }
}
