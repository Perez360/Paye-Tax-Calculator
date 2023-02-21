package com.generis.controller

import com.generis.com.generis.stream.kafka.StreamTopic
import com.generis.com.generis.stream.rabbitMq.StreamAddress
import com.generis.model.*


interface AssetController {

    /**
     * Add  an Asset
     * */
    fun create(createAssetDTO: CreateAssetDto): APIResponse<List<Asset>>

    /**
     * Updates Asset
     * */
    fun update(updateAssetDTO: UpdateAssetDto): APIResponse<List<Asset>>

    /**
     * Update Asset size
     * */
    fun updateAssetSize(id: Int, size: Int): APIResponse<List<Asset>>

    /**
     * Get one Asset by id
     * */
    fun get(id: Int): APIResponse<List<Asset>>

    /**
     * Get all Assets by customerId
     * */
    fun getAllByCustomerId(customerId: String): APIResponse<List<Asset>>

    /**
     * List all Assets
     * */
    fun getAll(searchAndFilterAsset: SearchAndFilterAsset): APIResponse<List<Asset>>

    fun export(searchAndFilterAsset: SearchAndFilterAsset, address: StreamAddress): APIResponse<List<Boolean>>

    fun export(searchAndFilterAsset: SearchAndFilterAsset, topic: StreamTopic): APIResponse<List<Boolean>>

    /**
     * Delete an Asset
     * */
    fun delete(id: Int): APIResponse<List<Boolean>>

    /**
     * delete all assets
     * */
    fun deleteAll(): APIResponse<List<Asset>>
}