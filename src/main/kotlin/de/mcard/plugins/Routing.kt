package de.mcard.plugins

import com.mongodb.BasicDBObject
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import models.Business
import models.Entrepreneur
import models.Food
import models.Menu
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import services.DatabaseService
import java.util.*

val dbService = DatabaseService()

fun Application.configureRouting() {
    routing {
        /*
        Menu Card
         */
        // get mcard by business
        get("/{business_name}/mcard") {
            try {

                val menuCol = dbService.getCollectionOfMenu()
                val businessName = call.parameters["business_name"]?.lowercase()
                val menu = menuCol.findOne { Menu::owner eq businessName }
                if (menu != null) {
                    call.respond(menu)
                } else {
                    call.respond(status = HttpStatusCode.NotFound, "A menu of business $businessName does not exist")
                }

            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e)
            }
        }
        // create mcard by business and list of food
        post("/{business_name}/mcard") {
            try {
                val menuList = call.receive<MutableList<Food>>()
                val businessName = call.parameters["business_name"].toString().lowercase()
                val businessNameExist = dbService.checkBusinessNameExist(businessName)
                val menuId = UUID.randomUUID().toString()
                if (businessNameExist) {
                    val menu = Menu(
                        menuId,
                        menuList,
                        businessName
                    )
                    dbService.insertMenu(
                        menu
                    )
                    dbService.updateMenuCardIdsInBusiness(businessName, menuId, true)
                    call.respond(menu)
                } else {
                    call.respond(status = HttpStatusCode.NotFound, "A business named $businessName does not exist")
                }
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e.toString())
            }
        }
        // delete mcard by id and business
        delete("/{business_name}/{menu_id}/mcard") {
            try {
                val menuId = call.parameters["menu_id"]
                val businessName = call.parameters["business_name"]
                dbService.getCollectionOfMenu().findOneAndDelete(Menu::_id eq menuId)
                if (businessName != null && menuId != null) {
                    dbService.updateMenuCardIdsInBusiness(businessName, menuId, false)
                }
                call.respond("delete $menuId succeed")
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e.toString())
            }
        }
        /*
        Business
         */
        // create or update business by name and owner
        put("/create/business") {
            try {
                val formData = call.receiveParameters()
                val entrepreneurId = formData["entrepreneur_id"].toString()
                val businessName = (formData["business_name"].toString()).lowercase()
                val street = formData["street"].toString()
                val city = formData["city"].toString()

                val entrepreneurExist = dbService.checkEntrepreneurExist(entrepreneurId)
                val businessNameExist = dbService.checkBusinessNameExist(businessName)

                if (entrepreneurExist) {
                    val business = Business(
                        UUID.randomUUID().toString(),
                        if (!businessNameExist) {
                            businessName
                        } else {
                            "$businessName-$city-$street"
                        },
                        entrepreneurId,
                        mutableListOf(),
                        city,
                        formData["zip"].toString(),
                        street
                    )
                    dbService.insertBusiness(
                        business
                    )
                    call.respond(status = HttpStatusCode.OK, business)
                } else call.respond("A Entrepreneur with the id $entrepreneurId does not exist")
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e.toString())
            }
        }
        // search business by name
        get("/search/{business_name}") {
            try {
                val searchQry = BasicDBObject()
                searchQry["name"] = call.parameters["business_name"]
                val businesses = dbService.getCollectionOfBusiness().find(searchQry)
                call.respond(businesses.toList())
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e.toString())
            }
        }
        // search business by zip
        get("/search/zip/{zip}") {
            try {
                val searchQry = BasicDBObject()
                searchQry["zip"] = call.parameters["zip"]
                val businesses = dbService.getCollectionOfBusiness().find(searchQry)
                call.respond(businesses.toList())
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e.toString())
            }
        }
        /*
        Entrepreneur
        */
        // create entrepreneur
        put("/create/entrepreneur") {
            try {
                val formParameters = call.receiveParameters()
                if (!call.parameters["_id"]?.let { it1 -> dbService.checkEntrepreneurExist(it1) }!!) {
                    val entrepreneur = Entrepreneur(
                        call.parameters["_id"].toString(), // Maybe Email Adress as Id, for Auth0 authentication?
                        formParameters["firstname"].toString(),
                        formParameters["lastname"].toString(),
                        formParameters["city"].toString(),
                        formParameters["zip"].toString(),
                        formParameters["street"].toString()
                    )
                    dbService.insertEntrepreneur(
                        entrepreneur
                    )
                    call.respond(entrepreneur)
                }
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e)
            }
        }
        // get entrepreneur by id
        get("/entrepreneur/{id}") {
            try {
                val entrepreneurId = call.parameters["id"]
                val entrepreneur =
                    dbService.getCollectionOfEntrepreneur().findOne { Entrepreneur::_id eq entrepreneurId }
                if (entrepreneur != null)
                    call.respond(entrepreneur) else {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        "A Entrepreneur with the id $entrepreneurId does not exist"
                    )
                }
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.NotFound, e.toString())
            }
        }
    }
}

