package services

import com.mongodb.client.MongoCollection
import models.Business
import models.Entrepreneur
import models.Menu
import org.litote.kmongo.*


open class DatabaseService {
    val client = KMongo.createClient()
    val database = client.getDatabase("mcardDB")


    fun getCollectionOfEntrepreneur(): MongoCollection<Entrepreneur> {
        return database.getCollection<Entrepreneur>()
    }

    fun insertEntrepreneur(entrepreneur: Entrepreneur) {
        getCollectionOfEntrepreneur().insertOne(entrepreneur)
    }

    fun getCollectionOfBusiness(): MongoCollection<Business> {
        return database.getCollection<Business>()
    }

    fun insertBusiness(business: Business) {
        getCollectionOfBusiness().insertOne(business)
    }

    fun getCollectionOfMenu(): MongoCollection<Menu> {
        return database.getCollection<Menu>()
    }

    fun insertMenu(menu: Menu) {
        getCollectionOfMenu().insertOne(menu)
    }

    fun checkEntrepreneurExist(_id: String): Boolean {
        return getCollectionOfEntrepreneur().findOne { Entrepreneur::_id eq _id } != null
    }

    fun checkBusinessNameExist(name: String): Boolean {
        return getCollectionOfBusiness().findOne { Business::name eq name } != null
    }

    fun updateMenuCardIdsInBusiness(businessName: String, menu_id: String, expand: Boolean): Boolean {
        val businessCol = getCollectionOfBusiness()
        val business = businessCol.findOne { Business::name eq businessName }
        val isDeleted = false
        if (business != null) {
            if (expand) {
                businessCol.updateOne(
                    Business::name eq businessName,
                    setValue(Business::menu_ids, business.menu_ids + menu_id)
                )
            } else {
                val updatedMenuIds = business.menu_ids
                val isDeleted = updatedMenuIds.remove(menu_id)
                businessCol.updateOne(
                    Business::name eq businessName,
                    setValue(Business::menu_ids, updatedMenuIds)
                )

            }
        }
        return isDeleted
    }
}