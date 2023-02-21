package com.generis.controller

import com.generis.com.generis.stream.kafka.StreamTopic
import com.generis.com.generis.stream.rabbitMq.StreamAddress
import com.generis.stream.rabbitMq.publisher.StreamPublisher
import com.generis.domain.CODE_SUCCESS
import com.generis.exceptions.ServiceException
import com.generis.model.*
import com.generis.repo.AssetDAO
import com.generis.stream.kafka.producer.StreamProducer
import com.generis.util.JacksonUtils
import com.generis.util.wrapFailureInResponse
import com.generis.util.wrapSuccessInResponse
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.slf4j.LoggerFactory

class AssetImpl(override val di: DI) : AssetController, DIAware {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val assetDAO: AssetDAO by di.instance()
    private val rabbitStreamPublisher: StreamPublisher by di.instance()
    private val kafkaStreamProducer: StreamProducer by di.instance()



    override fun create(createAssetDTO: CreateAssetDto): APIResponse<List<Asset>> {
        val savedAssetId = assetDAO.add(createAssetDTO)

        if (savedAssetId < 1)
            throw ServiceException(-1,
                "Could not save asset.")

        val savedAsset = assetDAO.get(savedAssetId) ?:
        throw ServiceException(-1, "Could fetch asset after saving ")

        return wrapSuccessInResponse(listOf(savedAsset))
    }

    override fun update(updateAssetDTO: UpdateAssetDto): APIResponse<List<Asset>> {
        val updatedAssetCount = assetDAO.update(updateAssetDTO)

        if (updatedAssetCount < 1)
            throw ServiceException(-4,
                "Could not update asset. ")

        val updateAsset = assetDAO.get(updateAssetDTO.id) ?:
            throw ServiceException(-2,"Asset does not exists." )

        return wrapSuccessInResponse(listOf(updateAsset))
    }

    override fun updateAssetSize(id: Int, size: Int): APIResponse<List<Asset>> {
        val updatedAssetCount = assetDAO.updateAssetSize(
            id = id,
            assetSize = size
        )

        if (updatedAssetCount < 1)
            throw ServiceException(-4,
                "Could not update asset. ")

        val updateAsset = assetDAO.get(id) ?:
        throw ServiceException(-2,"Asset does not exists." )

        return wrapSuccessInResponse(listOf(updateAsset))
    }

    override fun get(id: Int): APIResponse<List<Asset>> {
        val assetDto = assetDAO.get(id) ?: return wrapFailureInResponse("Asset not found")
        return wrapSuccessInResponse(listOf(assetDto))
    }

    override fun getAllByCustomerId(customerId: String): APIResponse<List<Asset>> {
        val assetDtos = assetDAO.findAllCustomerId(customerId)
        if (assetDtos.isEmpty()) return wrapFailureInResponse("No assets found for customerId::: $customerId")

        return wrapSuccessInResponse(assetDtos)
    }

    override fun getAll(searchAndFilterAsset: SearchAndFilterAsset): APIResponse<List<Asset>> {

        searchAndFilterAsset.page = searchAndFilterAsset.page ?: 1
        searchAndFilterAsset.size = searchAndFilterAsset.size ?: 10
        val assetDtos = assetDAO.getAll(searchAndFilterAsset)
        if (assetDtos.isEmpty()) return wrapFailureInResponse("No assets found")

        return wrapSuccessInResponse(assetDtos)
    }

    override fun export(searchAndFilterAsset: SearchAndFilterAsset, address: StreamAddress): APIResponse<List<Boolean>> {
        val assetApiResponse = getAll(searchAndFilterAsset)

        if (assetApiResponse.code != CODE_SUCCESS)
            return wrapFailureInResponse(assetApiResponse.message)

        if (assetApiResponse.data == null)
            return wrapFailureInResponse("No assets")

        val jsonMessage = JacksonUtils.getJacksonMapper().writeValueAsString(assetApiResponse.data)
        rabbitStreamPublisher.publish(jsonMessage, address)

        return wrapSuccessInResponse(listOf(true))
    }

    override fun export(searchAndFilterAsset: SearchAndFilterAsset, topic: StreamTopic): APIResponse<List<Boolean>> {
        val assetApiResponse = getAll(searchAndFilterAsset)

        if (assetApiResponse.code != CODE_SUCCESS)
            return wrapFailureInResponse(assetApiResponse.message)

        if (assetApiResponse.data == null)
            return wrapFailureInResponse("No assets")

        val jsonMessage = JacksonUtils.getJacksonMapper().writeValueAsString(assetApiResponse.data)
        kafkaStreamProducer.publish(jsonMessage, topic)

        return wrapSuccessInResponse(listOf(true))
    }

    override fun delete(id: Int): APIResponse<List<Boolean>> {
        assetDAO.get(id) ?: return wrapFailureInResponse("No asset found")
        val deletedAssetCount = assetDAO.delete(id)

        if (deletedAssetCount < 1)
            throw ServiceException(-4, "Could not delete asset by id ::: $id")

        return wrapSuccessInResponse(listOf(true))
    }


    override fun deleteAll(): APIResponse<List<Asset>> {
        val deleteAssetCount = assetDAO.deleteAll()

        if (deleteAssetCount < 1)
            throw ServiceException(-4, "could not delete")

        return wrapSuccessInResponse(listOf())
    }

}
