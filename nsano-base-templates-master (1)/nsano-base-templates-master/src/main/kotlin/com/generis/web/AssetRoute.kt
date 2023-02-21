package com.generis.web

import com.generis.com.generis.stream.kafka.StreamTopic
import com.generis.controller.AssetController
import com.generis.model.CreateAssetDto
import com.generis.model.SearchAndFilterAsset
import com.generis.model.UpdateAssetDto
import com.generis.util.wrapFailureInResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

private const val BASE_URL = "api/v1/assets"

fun Routing.assetRoutes(){

    route(BASE_URL){
        post {

            val createAssetDTO = call.receive<CreateAssetDto>()

            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.Created,
                controller.create(createAssetDTO)
            )
        }

        put {
            val updateAssetDTO = call.receive<UpdateAssetDto>()

            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.OK,
                controller.update(updateAssetDTO))
        }

        get("/{id}") {
            val id = call.parameters["id"]?: throw BadRequestException(message = "Asset id is undefined")

            val assetID= Integer.parseInt(id)
            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.OK,
                controller.get(assetID))

        }

        get("/customer/id/{id}") {
            val id = call.parameters["id"]?: throw BadRequestException(message = "Customer id is undefined")

            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.OK,
                controller.getAllByCustomerId(id))

        }

        get{

            val queryParameters = call.request.queryParameters
            application.log.info("new request received for : ${call.request.uri}")

            val page = queryParameters["page"]?.toIntOrNull()
            val size = queryParameters["size"]?.toIntOrNull()

            val maps = HashMap<String, Any?>()

            maps["name"] = queryParameters["name"]
            maps["customerId"] = queryParameters["customerId"]
            maps["customerMsisdn"] = queryParameters["customerMsisdn"]
            maps["valueGreaterThanEq"] = queryParameters["valueGreaterThanEq"]
            maps["valueLessThanEq"] = queryParameters["valueLessThanEq"]
            maps["currency"] = queryParameters["currency"]
            maps["registrationNumber"] = queryParameters["registrationNumber"]
            maps["registrationDate"] = queryParameters["registrationDate"]

            maps["size"] = size
            maps["page"] = page

            application.log.info("all parameters to be used are $maps")

            val request = SearchAndFilterAsset.from(maps)

            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.OK,
                controller.getAll(request)
            )

        }

        get("/csv"){

            val queryParameters = call.request.queryParameters
            application.log.info("new request received for : ${call.request.uri}")

            val page = queryParameters["page"]?.toIntOrNull()
            val size = queryParameters["size"]?.toIntOrNull()

            val maps = HashMap<String, Any?>()

            maps["name"] = queryParameters["name"]
            maps["customerId"] = queryParameters["customerId"]
            maps["customerMsisdn"] = queryParameters["customerMsisdn"]
            maps["valueGreaterThanEq"] = queryParameters["valueGreaterThanEq"]
            maps["valueLessThanEq"] = queryParameters["valueLessThanEq"]
            maps["currency"] = queryParameters["currency"]
            maps["registrationNumber"] = queryParameters["registrationNumber"]
            maps["registrationDate"] = queryParameters["registrationDate"]

            maps["size"] = size
            maps["page"] = page

            application.log.info("all parameters to be used are $maps")

            val request = SearchAndFilterAsset.from(maps)

            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.OK,
                controller.export(request, StreamTopic.EXPORT_ASSET)
            )

        }

        delete("/{id}") {
            val id = call.parameters["id"]?:
            return@delete call.respond(
                status = HttpStatusCode.BadRequest,
                message = wrapFailureInResponse<String>("Asset id is undefined")
            )

            val assetID= Integer.parseInt(id)
            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.OK,
                controller.delete(assetID))

        }

        delete("/delete/all") {
            val controller by closestDI().instance<AssetController>()
            call.respond(
                status = HttpStatusCode.OK,
                controller.deleteAll())
        }


    }
}
