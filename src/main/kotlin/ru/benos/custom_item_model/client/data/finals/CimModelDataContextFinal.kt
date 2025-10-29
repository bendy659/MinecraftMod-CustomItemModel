package ru.benos.custom_item_model.client.data.finals

import net.minecraft.resources.ResourceLocation
import ru.benos.custom_item_model.client.CIM.normalizeLocation
import ru.benos.custom_item_model.client.CIM.rl
import ru.benos.custom_item_model.client.data.serializables.CimModelDataContext
import ru.benos.custom_item_model.client.data.serializables.PredicateMode

data class CimModelDataContextFinal(
    val predicate: Map<String, Any> = emptyMap(),
    val predicateMode: PredicateMode = PredicateMode.ALL,
    val model: ResourceLocation,
    val force: Boolean
) {
    companion object {
        val CimModelDataContext.toFinal: CimModelDataContextFinal
            get() = CimModelDataContextFinal(
                this.predicate,
                this.predicateMode,
                this.model.rl.normalizeLocation,
                this.force
            )
    }
}
