package com.example.android.politicalpreparedness.repository

import android.content.Context
import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.tag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val database: ElectionDatabase, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val civicsApiService = CivicsApi.retrofitService

    suspend fun getUpcomingElections(): Result<List<Election>> =
        withContext(ioDispatcher) {
            try {
                Result.Success(civicsApiService.getElections().elections)
            } catch (e: Exception) {
                Log.e(tag(), "Unable to load upcoming elections list", e)
                Result.Failure(e)
            }
        }

    fun getSavedElections() = database.electionDao.getElections()

    suspend fun getVoterInfo(address: String, electionId: Int): Result<VoterInfoResponse> =
        withContext(ioDispatcher) {
            try {
                Result.Success(civicsApiService.getVoterInfo(address, electionId))
            } catch (e: Exception) {
                Log.e(tag(), "Unable to load voter info", e)
                Result.Failure(e)
            }
        }

    suspend fun getRepresentativeInfo(address: String): Result<RepresentativeResponse> = withContext(ioDispatcher) {
        try {
            Result.Success(civicsApiService.getRepresentativeInfo(address))
        } catch (e: Exception) {
            Log.e(tag(), "Unable to load representative info", e)
            Result.Failure(e)
        }
    }

    fun getSavedElectionFlow(id: Int) = database.electionDao.getElectionByIdFlow(id)

    suspend fun saveElection(election: Election) = withContext(ioDispatcher) { database.electionDao.saveElection(election) }

    suspend fun deleteElection(election: Election) = withContext(ioDispatcher) { database.electionDao.deleteElection(election) }

    companion object {
        private lateinit var INSTANCE: ElectionRepository

        fun getInstance(context: Context): ElectionRepository {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = ElectionRepository(ElectionDatabase.getInstance(context))
                }
            }
            return INSTANCE
        }
    }
}