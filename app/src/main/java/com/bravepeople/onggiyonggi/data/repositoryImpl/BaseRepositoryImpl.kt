package com.bravepeople.onggiyonggi.data.repositoryImpl

import com.bravepeople.onggiyonggi.data.datasource.BaseDataSource
import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestRegisterReviewDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestRegisterStoreDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAddMaxDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseCollectionDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseGetPetDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseGetStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAllCharacterDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDetailDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseSearchStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseStoreDetailDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseDeleteStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponsePhotoDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseReceiptDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseLikeDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseGetMyReviewsDto
import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseMyDto
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

class BaseRepositoryImpl @Inject constructor(
    private val baseDataSource:BaseDataSource
):BaseRepository {
    // signup
    override suspend fun checkId(id: String): Result<ResponseCheckSignUpDto> {
        return runCatching {
            baseDataSource.checkId(id)
        }.onFailure {
            Timber.e("base repository check id fail!!: $it")
        }
    }

    override suspend fun checkNickName(nickname: String): Result<ResponseCheckSignUpDto> {
        return runCatching {
            baseDataSource.checkNickName(nickname)
        }.onFailure {
            Timber.e("base repository check nickname fail!!: $it")
        }
    }

    override suspend fun signUp(
        id: String,
        password: String,
        nickname: String
    ): Result<ResponseSignUpDto> {
        return runCatching {
            baseDataSource.signUp(RequestSignUpDto(id,password,nickname))
        }.onFailure {
            Timber.e("base repository sign up fail!!: $it")
        }
    }

    // login
    override suspend fun login(id: String, password: String): Result<ResponseLoginDto> {
        return runCatching {
            baseDataSource.login(RequestLoginDto(id, password))
        }.onFailure {
            Timber.e("base repository login fail!!: $it")
        }
    }

    // home
    override suspend fun getStore(
        token: String,
        latitude: Double,
        longitude: Double,
        radius: Int
    ): Result<ResponseGetStoreDto> {
        return runCatching {
            baseDataSource.getStore(token, latitude, longitude, radius)
        }.onFailure {
            Timber.e("base repository get store fail!!: $it")
        }
    }

    override suspend fun storeDetail(token: String, id: Int): Result<ResponseStoreDetailDto> {
        return runCatching {
            baseDataSource.storeDetail(token,id)
        }.onFailure {
            Timber.e("base repository store detail fail!!: $it")
        }
    }

    override suspend fun getReviews(
        token: String,
        storeId: Int,
        cursor: String,
        size: Int
    ): Result<ResponseReviewDto> {
        return runCatching {
            baseDataSource.getReviews(token,  storeId, cursor, size)
        }.onFailure {
            Timber.e("base repository get reviews fail!!: $it")
        }
    }

    override suspend fun getReviewDetail(token: String, id: Int): Result<ResponseReviewDetailDto> {
        return runCatching {
            baseDataSource.getReviewDetail(token,id)
        }.onFailure {
            Timber.e("base repository get review detail fail!!: $it")
        }
    }

    override suspend fun like(token: String, id: Int): Result<ResponseLikeDto> {
        return runCatching {
            baseDataSource.like(token,id)
        }.onFailure {
            Timber.e("base repository like fail!!: $it")
        }
    }

    override suspend fun getEnum(token: String): Result<ResponseReviewEnumDto> {
        return runCatching {
            baseDataSource.getEnum(token)
        }.onFailure {
            Timber.e("base repository get enum fail!!: $it")
        }
    }
    override suspend fun searchStore(
        token: String,
        keyword: String
    ): Result<ResponseSearchStoreDto> {
        return runCatching {
            baseDataSource.searchStore(token, keyword)
        }.onFailure {
            Timber.e("base repository search store faill!!: $it")
        }
    }

    // register
    override suspend fun registerStore(
        token: String,
        storeRank: String,
        storeType: String,
        latitude: Double,
        longitude: Double,
        address: String,
        name: String,
        businessHours: String
    ): Result<ResponseRegisterStoreDto> {
        return runCatching {
            baseDataSource.registerStore(token, storeRank, RequestRegisterStoreDto(storeType, latitude, longitude, address, name, businessHours))
        }.onFailure {
            Timber.e("base repository register store fail!!: $it")
        }
    }

    override suspend fun deleteStore(token: String, id: Int): Result<ResponseDeleteStoreDto> {
        return runCatching {
            baseDataSource.deleteStore(token,id)
        }.onFailure {
            Timber.e("base repository delete store fail!!: $it")
        }
    }

    override suspend fun receipt(token: String, file: MultipartBody.Part): Result<ResponseReceiptDto> {
        return runCatching {
            baseDataSource.receipt(token,file)
        }.onFailure {
            Timber.e("base repository receipt fail!!: $it")
        }
    }

    override suspend fun photo(token: String, file: MultipartBody.Part): Result<ResponsePhotoDto> {
        return runCatching {
            baseDataSource.photo(token,file)
        }.onFailure {
            Timber.e("base repository photo fail!: $it")
        }
    }

    override suspend fun registerReview(
        token: String,
        storeId: Int,
        imageUrl: String,
        fileId: Int,
        content: String,
        reusableContainerType: String,
        reusableContainerSize: String,
        fillLevel: String,
        foodTaste: String
    ): Result<ResponseRegisterReviewDto> {
        return runCatching {
            baseDataSource.registerReview(token, RequestRegisterReviewDto(storeId, imageUrl, fileId, content, reusableContainerType, reusableContainerSize, fillLevel, foodTaste))
        }.onFailure {
            Timber.e("base repository register review fail!: $it")
        }
    }

    // character
    override suspend fun getPet(token: String): Result<ResponseGetPetDto> {
        return runCatching {
            baseDataSource.getPet(token)
        }.onFailure {
            Timber.e("base repository get pet fail!!: $it")
        }
    }

    override suspend fun randomPet(token: String): Result<ResponseGetPetDto> {
        return runCatching {
            baseDataSource.randomPet(token)
        }.onFailure {
            Timber.e("base repository random pet fail!!: $it")
        }
    }

    override suspend fun levelUp(token: String): Result<ResponseGetPetDto> {
        return runCatching {
            baseDataSource.levelUp(token)
        }.onFailure {
            Timber.e("base repository level up fail!!: $it")
        }
    }

    override suspend fun collection(token: String): Result<ResponseCollectionDto> {
        return runCatching {
            baseDataSource.collection(token)
        }.onFailure {
            Timber.e("base repository collection fail!!: $it")
        }
    }

    override suspend fun allCharacter(token: String): Result<ResponseAllCharacterDto> {
        return runCatching {
            baseDataSource.allCharacter(token)
        }.onFailure {
            Timber.e("base repository all character fail!!: $it")
        }
    }

    override suspend fun addMax(token: String): Result<ResponseAddMaxDto> {
        return runCatching {
            baseDataSource.addMax(token)
        }.onFailure {
            Timber.e("base repository add max fail!!: $it")
        }
    }

    // my
    override suspend fun getMyInfo(token: String): Result<ResponseMyDto> {
        return runCatching {
            baseDataSource.getMyInfo(token)
        }.onFailure {
            Timber.e("base repository get my info fail!!: $it")
        }
    }

    override suspend fun getMyReviews(token: String): Result<ResponseGetMyReviewsDto> {
        return runCatching {
            baseDataSource.getMyReviews(token)
        }.onFailure {
            Timber.e("base repository get my reviews fail!!: $it")
        }
    }
}