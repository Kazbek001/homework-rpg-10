# Homework 10 — The Adventurers' Guild: Iterator + Mediator

RPG-style demo of two behavioral design patterns working side by side:

- **Iterator** — traverse a `QuestLog` in different orders without exposing its internal list
- **Mediator** — coordinate guild officers (Captain, Quartermaster, Scout, Healer, Loremaster) without any direct references between them

---

## Project Structure

```
src/com/narxoz/rpg/
├── Main.java                              # Entry point — runs the full demo
├── combatant/
│   └── Hero.java                          # Simple hero model
├── quest/
│   ├── Quest.java                         # Immutable quest entry
│   ├── QuestPriority.java                 # LOW / NORMAL / HIGH / URGENT
│   ├── QuestIterator.java                 # Custom iterator interface
│   ├── QuestLog.java                      # Aggregate (snapshot is package-private)
│   ├── OrderedQuestIterator.java          # Iterator #1 — arrival order
│   ├── ReverseQuestIterator.java          # Iterator #2 — newest first
│   ├── PriorityQuestIterator.java         # Iterator #3 — priority threshold
│   └── RewardSortedQuestIterator.java     # Iterator #4 — open/closed proof
├── guild/
│   ├── GuildMediator.java                 # Mediator interface
│   ├── GuildHall.java                     # Topic-based mediator implementation
│   ├── GuildMember.java                   # Abstract colleague base
│   ├── Captain.java                       # Colleague — issues orders
│   ├── Quartermaster.java                 # Colleague — supplies & rewards
│   ├── Scout.java                         # Colleague — recon & danger
│   ├── Healer.java                        # Colleague — healing & triage
│   └── Loremaster.java                    # Colleague — open/closed proof
└── council/
    ├── CouncilEngine.java                 # Orchestrates the demo
    └── CouncilRunResult.java              # Run summary
```

---

## How to Run

### From IntelliJ IDEA
1. Open the project folder.
2. Right-click `Main.java` → **Run 'Main.main()'**.

### From the command line
```bash
javac -d out $(find src -name "*.java")
java -cp out com.narxoz.rpg.Main
```

Requires **Java 17 or newer** (uses `List.of()` and pattern-matching `instanceof`).

---

## Pattern #1 — Iterator

**Goal:** walk the quest log in different orders while keeping the internal list hidden.

- `QuestIterator` is a **custom** interface — `boolean hasNext()` and `Quest next()`. We deliberately do not use `java.util.Iterator`.
- `QuestLog` never exposes its `List<Quest>` publicly. Its `snapshot()` method is **package-private**, so only iterator classes in the same package can read it.
- Clients (Main, CouncilEngine) request iterators via factory methods on the aggregate — `ordered()`, `reverse()`, `priorityAtLeast(...)`, `rewardSorted()`.

**Concrete iterators:**

| Iterator | Order |
|---|---|
| `OrderedQuestIterator` | Arrival order |
| `ReverseQuestIterator` | Newest → oldest |
| `PriorityQuestIterator` | Only quests `>= threshold` |
| `RewardSortedQuestIterator` | (Part 4) sorted by reward gold, descending |

The 4th iterator is the open/closed proof — it lives in the same package so it can use `snapshot()`, but it didn't require any change to `QuestLog`, the iterator interface, or any other iterator class.

---

## Pattern #2 — Mediator

**Goal:** the four (then five) guild officers must not hold direct references to each other. All cross-colleague communication routes through `GuildHall`.

- `GuildMediator` defines `register(member)` and `dispatch(topic, from, payload)`.
- `GuildHall` is a topic-based mediator with `Map<String, List<GuildMember>>` routing. Each member declares which topics it subscribes to via `subscribedTopics()`, and the hall registers them in those buckets.
- `GuildMember` is the abstract colleague base. It only exposes the mediator and a `receive(...)` hook — no concrete references.

**Topic map:**

| Member | Subscribes to |
|---|---|
| Captain | `scouting`, `healing`, `supplies`, `reward` |
| Quartermaster | `supplies`, `orders`, `reward` |
| Scout | `scouting`, `orders`, `danger` |
| Healer | `healing`, `orders`, `danger` |
| Loremaster (Part 4) | `lore`, `curse`, `history` |

Echo prevention: the hall does not deliver a message back to its sender. Otherwise messages are fully decoupled.

**Open/closed proof:** `Loremaster` was added without modifying any existing colleague. Existing colleagues never gain a reference to it — they continue to publish to their own topics, and the hall simply has one more subscriber list now.

---

## Demo Flow (`CouncilEngine.runCouncil`)

1. **Phase 1 — High-priority sweep.** `PriorityQuestIterator` walks quests `>= HIGH`. The Captain issues an `orders` dispatch for each one (Quartermaster, Scout, Healer all react). URGENT quests trigger an extra `danger` dispatch.
2. **Phase 2 — Debrief sweep.** `ReverseQuestIterator` walks newest-first. The Quartermaster posts a `reward` note per quest; the Captain confirms.
3. **Phase 3 — Open/closed proof.** `RewardSortedQuestIterator` walks by reward descending. For each quest, a `lore` message goes to the Loremaster; URGENT quests also trigger a `curse` analysis.

The console output labels every phase and prints `[GuildHall] 'topic' from <sender> -> delivered to N member(s)` so the routing is visible at a glance.

---

## Sample Output (truncated)

```
=== Homework 10 Demo: Iterator + Mediator ===

-- Quest Log (6 quests) -- (arrival order)
  - Quest{title='Goblin Camp Cleanup', priority=LOW, ...}
  - Quest{title='Caravan Escort to Stoneford', priority=NORMAL, ...}
  ...

-- Registering Guild Members --
    [GuildHall] registered 'Sir Roderic' for topics [scouting, healing, supplies, reward]
    [GuildHall] registered 'Bren Hollow' for topics [supplies, orders, reward]
    ...

>>> Phase 1: High-priority sweep (PriorityQuestIterator)
  Quest #1: Hunt the River Wyrm [HIGH, 400g]
    [GuildHall] 'orders' from Sir Roderic -> delivered to 3 member(s)
      <- Quartermaster 'Bren Hollow' received order ... -> packing rations & rope
      <- Scout         'Kira Whisperwind' received order ... -> mapping approach
      <- Healer        'Sister Maela' received order ... -> brewing potions
  ...

>>> Phase 3: Open/Closed proof
  Reward-rank #1: Dragon Roost Assault (900g, URGENT)
    [GuildHall] 'lore' from <system> -> delivered to 1 member(s)
      <- Loremaster 'Old Theron' archives lore ...
    [GuildHall] 'curse' from <system> -> delivered to 1 member(s)
      <- Loremaster 'Old Theron' analyzes curse ...
  ...

-- Colleague work tally --
  Captain orders issued:        4
  Quartermaster packs prepared: 3
  Scout routes scouted:         3
  Healer aid plans:             3
  Loremaster lore entries:      6
  Loremaster curses analyzed:   1

=== CouncilRunResult{questsTraversed=15, messagesRouted=17, membersNotified=24} ===
```

---

## Diagrams

Two UML diagrams are included:

- **`Diagram_1_Iterator.png`** — `QuestIterator`, `QuestLog`, all four concrete iterators
- **`Diagram_2_Mediator.png`** — `GuildMediator`, `GuildHall`, `GuildMember`, all five concrete colleagues

---

## Why the Two Patterns Stay Independent

- `QuestLog` does not know `GuildHall` exists.
- No colleague has a `QuestIterator` field.
- The two patterns meet only inside `CouncilEngine`, which iterates quests in one hand and dispatches mediator messages in the other. The patterns share a use case, not a contract.
