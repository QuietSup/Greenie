package com.example.greenie.data.repositories

import com.example.greenie.data.MainDatabase
import com.example.greenie.data.models.Plant

class PlantRepository(private val mainDatabase: MainDatabase) {
    fun getAllPlants() = mainDatabase.plantDao().getAllPlants()

    fun getPlantById(id: Int) = mainDatabase.plantDao().getPlantById(id)

    fun getPlantsSortedByModifiedAt() = mainDatabase.plantDao().getPlantsSortedByModifiedAt()

    suspend fun updatePlant(plant: Plant) = mainDatabase.plantDao().updatePlant(plant)
}