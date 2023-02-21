package com.generis.controller


import com.generis.domain.CODE_SUCCESS
import com.generis.exceptions.ServiceException
import com.generis.model.Asset
import com.generis.model.CreateAssetDto
import com.generis.repo.AssetDAO
import com.generis.util.LocalDateTimeTypeManufacturer

import io.mockk.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.kodein.di.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.co.jemos.podam.api.PodamFactoryImpl
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssetImplTest {

    private lateinit var assetDao: AssetDAO
    private lateinit var systemConfig: Properties
    private lateinit  var di: DI
    private lateinit var asset: Asset
    private lateinit  var underTest: AssetController
    private  val factory: PodamFactoryImpl = PodamFactoryImpl()

    private val logger : Logger = LoggerFactory.getLogger(this::class.java)

    init {
        factory.strategy.addOrReplaceTypeManufacturer(LocalDateTime::class.java, LocalDateTimeTypeManufacturer())
    }

    @BeforeEach
    internal fun setUp() {
        assetDao = mockk(relaxed = true)
        asset = mockk(relaxed = true)
        systemConfig = mockk(relaxed = true)

        di = DI{
            bindSingleton { assetDao }
            bindSingleton { asset }
            bindConstant(tag="systemProperties"){ systemConfig }
        }

        underTest = AssetImpl(di)

        asset = factory.manufacturePojo(Asset::class.java)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkAll()
    }

    @Test
    fun shouldCreateAsset() {
        val req: CreateAssetDto = mockk(relaxed = true)

        every { assetDao.add(any()) } returns 1
        every { assetDao.get(any()) } returns simulatedAsset()

        val expected = underTest.create(createAssetDTO = req)
        logger.info("expected result $expected")

        verify { assetDao.add(any()) }
        verify { assetDao.get(any()) }

        Assertions.assertThat(expected.code).isEqualTo(CODE_SUCCESS)
        Assertions.assertThat(expected.data!!.isNotEmpty())
    }

    @Test
    fun shouldThrowServiceExceptionWhenAddAssetFails() {
        val req: CreateAssetDto = mockk(relaxed = true)

        every { assetDao.add(any()) } returns -1

        assertThrows<ServiceException>{
            underTest.create(createAssetDTO = req)
        }
    }

    private fun simulatedAsset(): Asset {
        return factory.manufacturePojo(Asset::class.java)
    }
}