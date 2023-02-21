package com.generis.repo

import com.generis.model.Asset
import com.generis.model.CreateAssetDto
import com.generis.model.SearchAndFilterAsset
import com.generis.model.UpdateAssetDto
import java.io.Closeable

interface AssetDAO: Closeable {

    fun add(createAssetDto: CreateAssetDto): Int

    fun update(updateAssetDTO: UpdateAssetDto): Int

    fun updateAssetSize(id: Int, assetSize: Int): Int

    fun get(id: Int): Asset?

    fun getAll(searchAndFilterAsset: SearchAndFilterAsset): List<Asset>

    fun findAllByCustomerMsisdn(msisdn: String): List<Asset>

    fun findAllCustomerId(id: String): List<Asset>

    fun delete(id: Int): Int

    fun deleteAll(): Int
}
