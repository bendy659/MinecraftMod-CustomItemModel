package ru.benos.custom_item_model.client.data.supports

enum class CimItemSupportsAnimation(animationName: String) {
    ALL("all"),

    // Hands //
    HANDS_MAIN_EQUIPPED("hands.main.equipped"),
    HANDS_MAIN_HOLDING("hands.main.holding"),
    HANDS_MAIN_USED("hands.main.used"),
    HANDS_MAIN_USED_ALT("hands.main.used.alt"),
    HANDS_MAIN_ATTACKED("hands.main.attacked"),
    HANDS_MAIN_BLOCK_START("hands.main.block.start"),
    HANDS_MAIN_BLOCK_IDLE("hands.main.block.idle"),
    HANDS_MAIN_BLOCK_BLOCKED("hands.main.block.blocked"),
    HANDS_MAIN_BLOCK_END("hands.main.block.end"),
    HANDS_OFF_EQUIPPED("hands.off.equipped"),
    HANDS_OFF_HOLDING("hands.main.holding"),
    HANDS_OFF_USED("hands.off.used"),
    HANDS_OFF_USED_ALT("hands.off.used.alt"),
    HANDS_OFF_ATTACKED("hands.off.attacked"),

    // Hotbar //
    HOTBAR_IDLE("hotbar.idle"),
    HOTBAR_SELECTED("hotbar.selected"),
    HOTBAR_UNSELECTED("hotbar.unselected"),
    HOTBAR_USED("hotbar.used"),
    HOTBAR_USED_ALT("hotbar.used.alt"),
    HOTBAR_ATTACKED("hotbar.attacked"),
    HANDS_OFF_BLOCK_START("hands.off.block.start"),
    HANDS_OFF_BLOCK_IDLE("hands.off.block.idle"),
    HANDS_OFF_BLOCK_BLOCKED("hands.off.block.blocked"),
    HANDS_OFF_BLOCK_END("hands.off.block.end"),

    // Inventory //
    INVENTORY_IDLE("inventory.idle"),
    INVENTORY_HOVER("inventory.hover"),
    INVENTORY_GRABBED("inventory.grabbed"),
    INVENTORY_PLACED("inventory.placed"),

    // World //
    WORLD_DROPPED("world.dropped"),
    WORLD_LANDED("world.landed"),
    WORLD_IDLE("world.idle"),
    WORLD_PICKUP("world.pickup"),

    // Item frame //
    ITEM_FRAME_PLACED("item_frame.placed"),
    ITEM_FRAME_ROTATE_0("item_frame.rotate_0"),
    ITEM_FRAME_ROTATE_1("item_frame.rotate_1"),
    ITEM_FRAME_ROTATE_2("item_frame.rotate_2"),
    ITEM_FRAME_ROTATE_3("item_frame.rotate_3"),
    ITEM_FRAME_ROTATE_4("item_frame.rotate_4"),
    ITEM_FRAME_ROTATE_5("item_frame.rotate_5"),
    ITEM_FRAME_ROTATE_6("item_frame.rotate_6"),
    ITEM_FRAME_ROTATE_7("item_frame.rotate_7"),

    // SHELF (>=1.21)
    SHELF_PLACED("shelf.placed"),
    SHELF_IDLE_0("shelf.idle_0"),
    SHELF_IDLE_1("shelf.idle_1"),
    SHELF_IDLE_2("shelf.idle_2");

    val serialName = animationName
}