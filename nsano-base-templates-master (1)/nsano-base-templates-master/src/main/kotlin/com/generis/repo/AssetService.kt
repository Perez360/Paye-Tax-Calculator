package com.generis.repo

import com.generis.model.Asset
import com.generis.model.CreateAssetDto
import com.generis.model.SearchAndFilterAsset
import com.generis.model.UpdateAssetDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime


class AssetService : AssetDAO {
    override fun add(createAssetDto: CreateAssetDto): Int = transaction {
        Assets.insert {
            it[name] = createAssetDto.name
            it[customerId] = createAssetDto.customerId
            it[customerMsisdn] = createAssetDto.customerMsisdn
            it[value] = createAssetDto.value
            it[currency] = createAssetDto.currency
            it[extras] = createAssetDto.extras
            it[registrationNumber] = createAssetDto.registrationNumber
            it[registrationDate] = createAssetDto.registrationDate
            it[size] = createAssetDto.size
            it[createdDate] = LocalDateTime.now()
            it[updatedDate] = LocalDateTime.now()
        }get Assets.id
    }

    override fun update(updateAssetDTO: UpdateAssetDto): Int = transaction {
        Assets.update ({ Assets.id eq updateAssetDTO.id }){
            it[name] = updateAssetDTO.name
            it[value] = updateAssetDTO.value
            it[currency] = updateAssetDTO.currency
            it[registrationDate] = updateAssetDTO.registrationDate
            it[registrationNumber] = updateAssetDTO.registrationNumber
            it[size] = updateAssetDTO.size
            it[updatedDate] = LocalDateTime.now()
        }
    }

    override fun updateAssetSize(id: Int, assetSize: Int): Int = transaction {
        Assets.update ({ Assets.id eq id }){
            it[size] = assetSize
            it[updatedDate] = LocalDateTime.now()
        }
    }

    override fun get(id: Int): Asset?  = transaction {
        Assets.select{ Assets.id eq id}.map { Assets.toAssets(it) }.singleOrNull()
    }

    override fun getAll(searchAndFilterAsset: SearchAndFilterAsset): List<Asset> = transaction {
        val query = Assets.selectAll()
        searchAndFilterAsset.name?.let {
            query.andWhere { Assets.name eq it }
        }

        searchAndFilterAsset.customerId?.let {
            query.andWhere { Assets.customerId eq it }
        }

        searchAndFilterAsset.customerMsisdn?.let {
            query.andWhere { Assets.customerMsisdn eq it }
        }

        searchAndFilterAsset.valueGreaterThanEq?.let {
            query.andWhere { Assets.value greaterEq  it }
        }

        searchAndFilterAsset.valueLessThanEq?.let {
            query.andWhere { Assets.value lessEq  it }
        }

        searchAndFilterAsset.currency?.let {
            query.andWhere { Assets.currency lessEq  it }
        }

        searchAndFilterAsset.currency?.let {
            query.andWhere { Assets.currency lessEq  it }
        }

        searchAndFilterAsset.registrationNumber?.let {
            query.andWhere { Assets.registrationNumber eq  it }
        }

        query
            .limit(searchAndFilterAsset.size!!, ((searchAndFilterAsset.page!! - 1) * searchAndFilterAsset.size!!).toLong())
            .orderBy(Assets.createdDate, SortOrder.DESC)
            .map { Assets.toAssets(it) }

    }

    override fun findAllByCustomerMsisdn(msisdn: String): List<Asset> = transaction {
        Assets.select{ Assets.customerMsisdn eq msisdn}.map { Assets.toAssets(it) }
    }

    override fun findAllCustomerId(id: String): List<Asset>  = transaction {
        Assets.select{ Assets.customerId eq id}.map { Assets.toAssets(it) }
    }

    override fun delete(id: Int): Int = transaction {
        Assets.deleteWhere { Assets.id eq id }
    }

    override fun deleteAll(): Int = transaction {
        Assets.deleteAll()
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
