package ru.benos.custom_item_model.client

import kotlinx.serialization.json.*
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import ru.benos.custom_item_model.client.CIM.rl
import ru.benos.custom_item_model.client.data.finals.CimModelsFinal
import ru.benos.custom_item_model.client.data.serializables.CimModelData
import java.util.*

object Reload: SimpleSynchronousResourceReloadListener {
    private val json = Json { ignoreUnknownKeys = true }
    private var NAMESPACES: Set<String> = setOf()

    override fun getFabricId(): ResourceLocation = "${CIM.MODID}:resources".rl

    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        // Update namespaces //
        NAMESPACES = resourceManager.namespaces

        // Load cim_models //
        loadCimModels(resourceManager)
    }

    private fun loadCimModels(manager: ResourceManager) {
        if (NAMESPACES.isEmpty()) {
            CIM.LOGGER.warn("Could not find any namespaces while loading cim_models! Skip loading.")
            return
        }

        var totalCimModels = 0

        NAMESPACES.forEach { namespace ->
            val cimModelsRes = "$namespace:cim_models.json".getResource(manager)
            if (!cimModelsRes.isPresent) return@forEach

            val jsonElement = cimModelsRes.toJsonElement()
            if (!jsonElement.contain("entries")) {
                CIM.LOGGER.warn("cim_models.json in namespace '$namespace' does not contain 'entries' key! Skip loading.")
                return
            }

            try {
                val cimModelData = json.decodeFromJsonElement<CimModelData>(jsonElement)
                CimModelsFinal.applyCimModelData(cimModelData)
                totalCimModels += cimModelData.entries.size
            }
            catch (_: Exception) {
                CIM.LOGGER.warn("Failed to load cim_models.json in namespace '$namespace'! Skip loading.")
            }
        }
        CIM.LOGGER.info("Loaded total $totalCimModels cim_models entries.")

        CIM.LOGGER.flushAll()
    }

    private fun loadModelProperties(manager: ResourceManager) {

    }

    private fun tryParsePredicate() {

    }

    // Util's //

    fun ResourceLocation.getResource(manager: ResourceManager): Optional<Resource> =
        manager.getResource(this)

    fun String.getResource(manager: ResourceManager): Optional<Resource> =
        manager.getResource(this.rl)

    fun Optional<Resource>.toJsonElement(): JsonElement = json.parseToJsonElement(
        this.get().openAsReader().use { it.readText() }
    )

    fun JsonElement.contain(key: String): Boolean =
        key in this.jsonObject
}